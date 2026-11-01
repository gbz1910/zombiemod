package com.example.examplemod;
import java.nio.file.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid="examplemod",value=Dist.CLIENT)
public final class PeerClient {
    private static boolean heardShot;
    @SubscribeEvent public static void sound(net.minecraftforge.client.event.sound.PlaySoundEvent e){
        if(e.getOriginalSound().getIdentifier().toString().equals("zona:pistol_fire"))heardShot=true;
    }
    private static boolean connecting,reported,seenFull;private static int tick;
    static int rounds(ItemStack s){return s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getIntOr("rounds",0);}
    static boolean pistol(ItemStack s){return BuiltInRegistries.ITEM.getKey(s.getItem()).toString().equals("zona:pistola");}
    static void report(String role) {
        if(reported)return;
        try{Files.writeString(Path.of(System.getenv("ZONA_SIGNALS"),role+".ok"),"acknowledged");}
        catch(Exception ex){throw new IllegalStateException(ex);}
        reported=true;System.out.println("ZONA_PEER_PASS "+role);
    }
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent.Post e){
        var mc=Minecraft.getInstance();
        if(!connecting && mc.gui.screen() instanceof TitleScreen title){
            connecting=true;String address="127.0.0.1:25577";
            ConnectScreen.startConnecting(title,mc,ServerAddress.parseString(address),new ServerData("Zona QA",address,ServerData.Type.OTHER),false,null);
        }
        if(mc.player==null || mc.level==null)return;
        String role=System.getenv("ZONA_ROLE");
        if("shooter".equals(role) && pistol(mc.player.getMainHandItem())){
            tick++;if(tick==35)KeyMapping.click(InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_R));
            if(tick==90){Minecraft.getInstance().player.setYRot(0);Minecraft.getInstance().player.setXRot(0);mc.options.keyUse.setDown(true);}
            if(tick==102)mc.options.keyAttack.setDown(true);
            if(tick==111){mc.options.keyAttack.setDown(false);mc.options.keyUse.setDown(false);}
            if(tick>=113 && rounds(mc.player.getMainHandItem())==11)report("shooter");
        } else if("observer".equals(role)){
            for(var player:mc.level.players())if(player!=mc.player && pistol(player.getMainHandItem())){
                int load=rounds(player.getMainHandItem());if(load==12)seenFull=true;
                if(seenFull && load==11 && heardShot){if(!reported)Screenshot.grab(mc,false);report("observer");}
            }
        }
    }
}


