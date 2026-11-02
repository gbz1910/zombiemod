package br.zona;
import net.minecraft.client.*;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleOptions;
@Mod.EventBusSubscriber(modid=Zona.ID,value=Dist.CLIENT)
public final class Client {
    static final KeyMapping RELOAD=new KeyMapping("key.zona.reload",com.mojang.blaze3d.platform.InputConstants.KEY_R,KeyMapping.Category.GAMEPLAY);
    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent e){e.register(RELOAD);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){
        e.registerEntityRenderer(Zona.COMMON.get(),c->new InfectedRenderer(c,0));
        e.registerEntityRenderer(Zona.RUNNER.get(),c->new InfectedRenderer(c,1));
        e.registerEntityRenderer(Zona.TANK.get(),c->new InfectedRenderer(c,2));
    }
    private static boolean held(){var p=Minecraft.getInstance().player;return p!=null && (p.getMainHandItem().getItem() instanceof Gun || p.getMainHandItem().getItem() instanceof Machete);}
    @SubscribeEvent public static boolean attack(InputEvent.InteractionKeyMappingTriggered.Attack e){
        if(!held())return false;e.setSwingHand(false);return true;
    }
    @SubscribeEvent public static boolean use(InputEvent.InteractionKeyMappingTriggered.UseItem e){
        if(!held())return false;e.setSwingHand(false);return true;
    }
    static boolean trigger,aim;
    static int burst,cooldown;
    private static void debugBox(AABB box,ParticleOptions particle){
        double[] lo={box.minX,box.minY,box.minZ},hi={box.maxX,box.maxY,box.maxZ};
        for(int axis=0;axis<3;axis++)for(int a=0;a<2;a++)for(int b=0;b<2;b++){
            int x=(axis+1)%3,y=(axis+2)%3;double[] start={0,0,0},end={0,0,0};
            start[axis]=lo[axis];end[axis]=hi[axis];start[x]=end[x]=a==0?lo[x]:hi[x];start[y]=end[y]=b==0?lo[y]:hi[y];
            Vec3 from=new Vec3(start[0],start[1],start[2]),delta=new Vec3(end[0]-start[0],end[1]-start[1],end[2]-start[2]);
            int dots=8;for(int i=0;i<=dots;i++){Vec3 p=from.add(delta.scale(i/(double)dots));Minecraft.getInstance().level.addParticle(particle,p.x,p.y,p.z,0,0,0);}
        }
    }
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent.Post event){
        var mc=Minecraft.getInstance();boolean armed=held() && mc.gui.screen()==null;
        boolean fire=armed && mc.options.keyAttack.isDown(), ads=armed && mc.options.keyUse.isDown();
        if(mc.getConnection()==null){trigger=false;aim=false;return;}
        if(ads!=aim)Combat.send(ads?Combat.AIM:Combat.HIP);
        if(fire!=trigger)Combat.send(fire?Combat.PRESS:Combat.RELEASE);
        if(armed && RELOAD.consumeClick())Combat.send(Combat.RELOAD);
        if(cooldown>0)cooldown--;
        if(fire && mc.player.getMainHandItem().getItem() instanceof Gun g && (!trigger || g.automatic) && cooldown==0 && g.rounds(mc.player.getMainHandItem())>0){
            cooldown=g.interval();burst=Math.min(8,burst+1);mc.player.setXRot(Math.max(-89,mc.player.getXRot()-(float)(Settings.RECOIL.get()*(ads?.5:1))*(1+burst*.1f)));
        }
        if(Settings.DEBUG_HITBOXES.get() && mc.level!=null && mc.player.tickCount%10==0)
            for(var z:mc.level.getEntitiesOfClass(Infected.class,mc.player.getBoundingBox().inflate(24))){
                debugBox(z.headBox(),net.minecraft.core.particles.ParticleTypes.END_ROD);
                debugBox(z.torsoBox(),net.minecraft.core.particles.ParticleTypes.CRIT);
                debugBox(z.getBoundingBox(),net.minecraft.core.particles.ParticleTypes.SMOKE);
            }
        if(!fire)burst=0;
        trigger=fire;aim=ads;
    }
    @SubscribeEvent public static void fov(ComputeFovModifierEvent e){if(aim && held())e.setNewFovModifier(e.getFovModifier()*.72f);}
}




