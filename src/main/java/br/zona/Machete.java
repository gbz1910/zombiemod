package br.zona;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.phys.Vec3;

/** Server-authoritative wide melee sweep. Every infected appears once in the query. */
final class Machete extends Item {
    private static final Map<ServerPlayer,Integer> animation=new WeakHashMap<>();
    private static final Map<ServerPlayer,Integer> draw=new WeakHashMap<>();
    Machete(Properties props){super(props.durability(480));}
    static void pose(ItemStack stack,int frame){
        var value=new CustomModelData(java.util.List.of((float)frame),java.util.List.of(),java.util.List.of(),java.util.List.of());
        if(!value.equals(stack.get(DataComponents.CUSTOM_MODEL_DATA)))stack.set(DataComponents.CUSTOM_MODEL_DATA,value);
    }
    static void tick(ServerPlayer player){
        if(player.getMainHandItem().getItem() instanceof Machete && !draw.containsKey(player))draw.put(player,4);
        int equip=draw.getOrDefault(player,0);
        if(equip>0){pose(player.getMainHandItem(),1);draw.put(player,equip-1);if(equip==1)draw.remove(player);}
        int left=animation.getOrDefault(player,0);
        ItemStack stack=player.getMainHandItem();
        if(!(stack.getItem() instanceof Machete)){animation.remove(player);draw.remove(player);return;}
        if(left<=0){if(equip<=0)pose(stack,0);return;}
        pose(stack,left>4?2:left>1?3:0);
        if(--left<=0)animation.remove(player);else animation.put(player,left);
    }
    static void strike(ServerPlayer player,ItemStack stack){
        if(player.getCooldowns().isOnCooldown(stack) || !player.isAlive())return;
        player.getCooldowns().addCooldown(stack,Settings.MACHETE_DELAY.get());animation.put(player,7);pose(stack,2);
        ServerLevel level=player.level();Vec3 origin=player.getEyePosition();Vec3 look=player.getLookAngle().normalize();
        double reach=4.4,cos=Math.cos(Math.toRadians(67));int hit=0;
        for(Infected zombie:level.getEntitiesOfClass(Infected.class,player.getBoundingBox().inflate(reach),z->z.isAlive())){
            Vec3 delta=zombie.position().subtract(player.position());double horizontal=Math.sqrt(delta.x*delta.x+delta.z*delta.z);
            if(horizontal<.01 || horizontal>reach || Math.abs(zombie.getY()+zombie.getBbHeight()*.5-player.getY()-1.0)>1.65 || (delta.x*look.x+delta.z*look.z)/horizontal<cos)continue;
            zombie.setInvulnerableTime(0);
            zombie.hurtServer(level,player.damageSources().playerAttack(player),Settings.MACHETE_DAMAGE.get());
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT,zombie.getX(),zombie.getY()+zombie.getBbHeight()*.55,zombie.getZ(),5,.18,.25,.18,.12);
            hit++;
        }
        for(int i=0;i<9;i++){
            double a=Math.toRadians(-67+i*134d/8);Vec3 side=new Vec3(-look.z,0,look.x);
            Vec3 point=origin.add(look.scale(Math.cos(a)*2.4)).add(side.scale(Math.sin(a)*2.4));
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK,point.x,point.y-.5,point.z,1,0,0,0,0);
        }
        level.playSound(null,player.blockPosition(),hit>0?SoundEvents.PLAYER_ATTACK_STRONG:SoundEvents.PLAYER_ATTACK_NODAMAGE,SoundSource.PLAYERS,.9f,hit>0?.82f:1.0f);
        stack.hurtAndBreak(1,player,EquipmentSlot.MAINHAND);
    }
}
