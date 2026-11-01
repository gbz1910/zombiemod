package br.zona;
import net.minecraft.client.*;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=Zona.ID,value=Dist.CLIENT)
public final class Client {
    static final KeyMapping RELOAD=new KeyMapping("key.zona.reload",com.mojang.blaze3d.platform.InputConstants.KEY_R,KeyMapping.Category.GAMEPLAY);
    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent e){e.register(RELOAD);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){
        e.registerEntityRenderer(Zona.COMMON.get(),c->new InfectedRenderer(c,0));
        e.registerEntityRenderer(Zona.RUNNER.get(),c->new InfectedRenderer(c,1));
        e.registerEntityRenderer(Zona.TANK.get(),c->new InfectedRenderer(c,2));
    }
    private static boolean held(){var p=Minecraft.getInstance().player;return p!=null && p.getMainHandItem().getItem() instanceof Gun;}
    @SubscribeEvent public static boolean attack(InputEvent.InteractionKeyMappingTriggered.Attack e){
        if(!held())return false;e.setSwingHand(false);return true;
    }
    @SubscribeEvent public static boolean use(InputEvent.InteractionKeyMappingTriggered.UseItem e){
        if(!held())return false;e.setSwingHand(false);return true;
    }
    static boolean trigger,aim;
    static int burst,cooldown;
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent.Post event){
        var mc=Minecraft.getInstance();boolean armed=held() && mc.gui.screen()==null;
        boolean fire=armed && mc.options.keyAttack.isDown(), ads=armed && mc.options.keyUse.isDown();
        if(mc.getConnection()==null){trigger=false;aim=false;return;}
        if(ads!=aim)Combat.send(ads?Combat.AIM:Combat.HIP);
        if(fire!=trigger)Combat.send(fire?Combat.PRESS:Combat.RELEASE);
        if(armed && RELOAD.consumeClick())Combat.send(Combat.RELOAD);
        if(cooldown>0)cooldown--;
        if(fire && mc.player.getMainHandItem().getItem() instanceof Gun g && (!trigger || g.automatic) && cooldown==0 && g.rounds(mc.player.getMainHandItem())>0){
            cooldown=g.interval();burst=Math.min(8,burst+1);mc.player.setXRot(Math.max(-89,mc.player.getXRot()-(ads?.3f:.6f)*(1+burst*.1f)));
        }
        if(!fire)burst=0;
        trigger=fire;aim=ads;
    }
    @SubscribeEvent public static void fov(ComputeFovModifierEvent e){if(aim && held())e.setNewFovModifier(e.getFovModifier()*.72f);}
}



