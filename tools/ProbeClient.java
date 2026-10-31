package com.example.examplemod;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="examplemod", value=Dist.CLIENT)
public final class ProbeClient {
    @SubscribeEvent public static void open(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen) event.setNewScreen(new ProbeScreen());
    }
    static final class ProbeScreen extends Screen {
        private int ticks;
        private final ItemStack stack = ExampleMod.EXAMPLE_ITEM.get().getDefaultInstance();
        ProbeScreen() { super(Component.literal("Blender pipeline validation")); }
        @Override public void tick() {
            ticks++;
            int frame = (ticks / 10) % 9;
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of((float)frame), List.of(), List.of(), List.of()));
            if (ticks == 85 || ticks == 115) Screenshot.grab(Minecraft.getInstance(), false);
            if (ticks == 150) {
                System.out.println("BLENDER_PROBE_RENDER_LOOP_COMPLETED");
                Minecraft.getInstance().stop();
            }
        }
        @Override public void extractRenderState(GuiGraphicsExtractor g, int x, int y, float delta) {
            g.fill(0,0,width,height,0xFF18202B);
            g.centeredText(font,"Blender -> Minecraft 26.3 / Forge 66.1.0",width/2,20,0xFFFFFFFF);
            g.centeredText(font,"Geometry and animation pipeline probe",width/2,36,0xFF93C7A5);
            g.pose().pushMatrix();
            g.pose().translate(width/2f-48,height/2f-48);
            g.pose().scale(6,6);
            g.item(stack,0,0);
            g.pose().popMatrix();
        }
    }
}
