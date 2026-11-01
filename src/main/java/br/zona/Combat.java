package br.zona;
import java.util.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.*;
final class Combat {
    static final int PRESS=1, RELEASE=2, RELOAD=3, AIM=4, HIP=5;
    record Action(int kind) {
        static final StreamCodec<RegistryFriendlyByteBuf,Action> CODEC=StreamCodec.of((buf,msg)->buf.writeByte(msg.kind),buf->new Action(buf.readUnsignedByte()));
    }
    static final SimpleChannel NET=ChannelBuilder.named(Zona.id("combate")).networkProtocolVersion(1).simpleChannel()
        .play().serverbound().addMain(Action.class,Action.CODEC,Combat::receive).build();
    static final class State {
        final ItemStack weapon;
        boolean firing,aim;
        int reload,burst,cooldown,flash,equip=3;
        State(ItemStack stack){weapon=stack;}
    }
    static final Map<ServerPlayer,State> active=new WeakHashMap<>();
    static void register(){
        TickEvent.PlayerTickEvent.Post.BUS.addListener(e->{if(e.player() instanceof ServerPlayer p)tick(p);});
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(e->{if(e.getEntity() instanceof ServerPlayer p)active.remove(p);});
        LivingDamageEvent.BUS.addListener(e->{
            if(e.getEntity() instanceof ServerPlayer p && e.getAmount()>0 && p.getUseItem().getItem() instanceof Treatment)p.stopUsingItem();
            return false;
        });
    }
    static void send(int kind){NET.send(new Action(kind),PacketDistributor.SERVER.noArg());}
    private static State state(ServerPlayer p){
        var old=active.get(p);var stack=p.getMainHandItem();
        if(old!=null && old.weapon!=stack){Gun.pose(old.weapon,0);active.remove(p);old=null;}
        if(!p.isAlive() || !(stack.getItem() instanceof Gun))return null;
        if(old==null){old=new State(stack);active.put(p,old);}
        return old;
    }
    private static void receive(Action msg,CustomPayloadEvent.Context ctx){
        ServerPlayer p=ctx.getSender();if(p==null)return;
        var s=state(p);if(s==null)return;
        Gun g=(Gun)s.weapon.getItem();
        switch(msg.kind){
            case PRESS -> {if(!s.firing && s.reload==0){s.firing=true;shoot(p,s,g);}}
            case RELEASE -> {s.firing=false;s.burst=0;}
            case AIM -> s.aim=true;
            case HIP -> s.aim=false;
            case RELOAD -> {
                if(s.reload==0 && g.rounds(s.weapon)<g.capacity() && count(p,g.ammo())>0){
                    s.firing=false;s.burst=0;s.reload=g.reloadTime();sound(p,SoundEvents.IRON_TRAPDOOR_OPEN,.65f,1.4f);
                }
            }
        }
    }
    private static int count(ServerPlayer p,Item item){
        int n=0;var inv=p.getInventory();
        for(int i=0;i<inv.getContainerSize();i++)if(inv.getItem(i).is(item))n+=inv.getItem(i).getCount();
        return n;
    }
    private static void finishReload(ServerPlayer p,State s,Gun g){
        int rounds=g.rounds(s.weapon), need=g.capacity()-rounds;
        var inv=p.getInventory();
        for(int i=0;i<inv.getContainerSize() && need>0;i++){
            var stack=inv.getItem(i);if(!stack.is(g.ammo()))continue;
            int take=Math.min(stack.getCount(),need);stack.shrink(take);rounds+=take;need-=take;
        }
        g.load(s.weapon,rounds);sound(p,SoundEvents.IRON_TRAPDOOR_CLOSE,.6f,1.5f);
    }
    static void tick(ServerPlayer p){
        State s=state(p);if(s==null)return;Gun gun=(Gun)s.weapon.getItem();
        if(s.cooldown>0)s.cooldown--;
        if(s.flash>0)s.flash--;
        if(s.equip>0)s.equip--;
        if(s.reload>0){if(--s.reload==0)finishReload(p,s,gun);}
        else if(s.firing && gun.automatic && s.cooldown==0)shoot(p,s,gun);
        int frame=s.reload>0?5+Math.min(3,(gun.reloadTime()-s.reload)*4/gun.reloadTime()):s.flash>0?3:s.equip>0?1:0;
        Gun.pose(s.weapon,frame);
        if(p.tickCount%4==0)p.sendSystemMessage(Component.literal((s.reload>0?"Recarregando   ":"")+gun.rounds(s.weapon)+" / "+gun.capacity()),true);
    }
    private static void sound(ServerPlayer p,SoundEvent sound,float volume,float pitch){
        p.level().playSound(null,p.blockPosition(),sound,SoundSource.PLAYERS,volume,pitch);
    }
    static void shoot(ServerPlayer p,State s,Gun gun){
        if(s.cooldown>0 || s.reload>0 || !p.isAlive() || p.getMainHandItem()!=s.weapon)return;
        s.cooldown=gun.interval();
        int left=gun.rounds(s.weapon);
        if(left==0){sound(p,SoundEvents.LEVER_CLICK,.3f,1.7f);s.firing=false;return;}
        gun.load(s.weapon,left-1);s.flash=2;s.burst=Math.min(14,s.burst+1);
        ServerLevel level=p.level();Vec3 from=p.getEyePosition();var rng=p.getRandom();
        double spread=(s.aim?.002:.009)+(gun.automatic?.0017*s.burst:.001);
        Vec3 dir=p.getLookAngle().add(rng.nextGaussian()*spread,rng.nextGaussian()*spread,rng.nextGaussian()*spread).normalize();
        Vec3 far=from.add(dir.scale(80));
        Vec3 end=level.clip(new ClipContext(from,far,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,p)).getLocation();
        double nearest=from.distanceToSqr(end);LivingEntity victim=null;
        for(var entity:level.getEntitiesOfClass(LivingEntity.class,new AABB(from,end).inflate(.25),e->e!=p && e.isAlive() && !e.isSpectator())){
            var hit=entity.getBoundingBox().clip(from,end);
            if(hit.isPresent() && hit.get().distanceToSqr(from)<nearest){nearest=hit.get().distanceToSqr(from);victim=entity;}
        }
        if(victim!=null)victim.hurtServer(level,p.damageSources().playerAttack(p),gun.damage());
        Vec3 muzzle=from.add(dir.scale(.9));level.sendParticles(ParticleTypes.FLAME,muzzle.x,muzzle.y-.12,muzzle.z,2,.03,.03,.03,.01);
        sound(p,gun.automatic?SoundEvents.FIREWORK_ROCKET_BLAST:SoundEvents.FIREWORK_ROCKET_LARGE_BLAST,gun.automatic?2.4f:1.2f,gun.automatic?.75f:1.25f);
        if(s.burst==1 || s.burst%4==0){
            int radius=(gun.automatic?Settings.R_NOISE:Settings.P_NOISE).get(),n=0;
            for(var z:level.getEntitiesOfClass(Infected.class,p.getBoundingBox().inflate(radius))){
                if(z.distanceToSqr(p)>radius*radius)continue;z.setTarget(p);if(++n>=Settings.LIMIT.get())break;
            }
        }
    }
}

