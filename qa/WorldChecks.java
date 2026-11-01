package com.example.examplemod;

import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

@Mod.EventBusSubscriber(modid="examplemod",value=Dist.CLIENT)
public final class WorldChecks {
    private static boolean started;private static int t;private static UUID dummy;
    static Minecraft mc(){return Minecraft.getInstance();}
    static <T> T server(Function<ServerPlayer,T> operation){
        var srv=mc().getSingleplayerServer();
        return srv.submit(()->operation.apply(srv.getPlayerList().getPlayers().getFirst())).join();
    }
    static Item item(String id){return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("zona",id));}
    static int rounds(ItemStack s){return s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getIntOr("rounds",0);}
    static void check(boolean ok,String label){if(!ok)throw new IllegalStateException("ZONA_QA_FAILED "+label);System.out.println("ZONA_QA_PASS "+label);}
    static void select(int slot){mc().player.getInventory().setSelectedSlot(slot);}
    static void reload(){KeyMapping.click(InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_R));}
    static float hp(ServerPlayer p){return ((LivingEntity)p.level().getEntity(dummy)).getHealth();}
    static void wall(ServerPlayer p,boolean solid){
        BlockPos pos=p.blockPosition().offset(0,1,2);p.level().setBlockAndUpdate(pos,solid?Blocks.STONE.defaultBlockState():Blocks.AIR.defaultBlockState());
    }
    static void use(){mc().gameMode.useItem(mc().player,InteractionHand.MAIN_HAND);}
    static float before,healed;
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent.Post ignored){
        var mc=mc();
        if(!started && mc.gui.screen() instanceof TitleScreen){
            started=true;
            var cfg=new LevelSettings("Zona QA",GameType.SURVIVAL,LevelSettings.DifficultySettings.DEFAULT,true,WorldDataConfiguration.DEFAULT);
            mc.createWorldOpenFlows().createFreshLevel("zona-qa",cfg,new WorldOptions(42,false,false),WorldPresets::createTestWorldDimensions,mc.gui.screen());
        }
        if(mc.player==null || mc.getSingleplayerServer()==null)return;
        t++;
        switch(t){
            case 100 -> {
                mc.player.setXRot(0);mc.player.setYRot(0);
                server(p->{var inv=p.getInventory();
                    String[] ids={"pistola","m4","municao_9mm","municao_556","bandagem","kit"};
                    int[] counts={1,1,17,48,2,2};
                    for(int i=0;i<ids.length;i++){check(item(ids[i])!=Items.AIR,"registered "+ids[i]);inv.setItem(i,new ItemStack(item(ids[i]),counts[i]));}
                    for(int i=0;i<3;i++){
                        String key=new String[]{"comum","corredor","tanque"}[i];
                        var type=BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("zona",key));
                        var mob=(Mob)type.create(p.level(),EntitySpawnReason.COMMAND);
                        check(mob!=null,"spawn "+key);mob.setNoAi(true);
                        mob.snapTo(p.getX()+i*3,p.getY(),p.getZ()+5);
                        mob.setYRot(180);mob.setYHeadRot(180);mob.setYBodyRot(180);p.level().addFreshEntity(mob);
                        if(i==0)dummy=mob.getUUID();
                    }
                    p.getFoodData().setFoodLevel(10);
                    for(int x=-3;x<=9;x++)for(int z=-2;z<=7;z++)p.level().setBlockAndUpdate(p.blockPosition().offset(x,4,z),Blocks.STONE.defaultBlockState());
                    return true;
                });
            }
            case 115 -> {select(0);mc.options.keyAttack.setDown(true);}
            case 120 -> mc.options.keyAttack.setDown(false);
            case 130 -> {check(server(p->rounds(p.getMainHandItem()))==0,"empty trigger");reload();}
            case 139 -> select(1);
            case 177 -> {
                check(server(p->rounds(p.getInventory().getItem(0))==0 && p.getInventory().getItem(2).getCount()==17),"reload cancellation on slot change");
                select(0);
            }
            case 185 -> reload();
            case 230 -> {
                check(server(p->rounds(p.getMainHandItem())==12 && p.getInventory().getItem(2).getCount()==5),"pistol magazine and inventory debit");
                before=server(p->{wall(p,true);return hp(p);});
                mc.options.keyUse.setDown(true);mc.options.keyAttack.setDown(true);
            }
            case 251 -> {mc.options.keyAttack.setDown(false);mc.options.keyUse.setDown(false);}
            case 258 -> {
                check(server(p->rounds(p.getMainHandItem())==11 && hp(p)==before),"one pistol shot per press and wall protection");
                server(p->{wall(p,false);return true;});mc.player.setXRot(0);
            }
            case 265 -> mc.options.keyAttack.setDown(true);
            case 269 -> mc.options.keyAttack.setDown(false);
            case 285 -> {check(server(p->hp(p)<before),"unobstructed shot damages infected");Screenshot.grab(mc,false);select(1);}
            case 296 -> reload();
            case 355 -> {
                check(server(p->rounds(p.getMainHandItem())==30 && p.getInventory().getItem(3).getCount()==18),"M4 magazine and caliber");
                mc.options.keyAttack.setDown(true);
            }
            case 364 -> Screenshot.grab(mc,false);
            case 371 -> mc.options.keyAttack.setDown(false);
            case 386 -> {check(server(p->rounds(p.getMainHandItem())<28),"automatic burst");select(4);server(p->{p.setHealth(6);return true;});}
            case 399 -> {mc.options.keyUse.setDown(true);use();}
            case 407 -> server(p->{p.hurtServer(p.level(),p.damageSources().generic(),2);return true;});
            case 408 -> mc.options.keyUse.setDown(false);
            case 444 -> {check(server(p->p.getInventory().getItem(4).getCount()==2),"interrupted bandage retains item");before=server(ServerPlayer::getHealth);mc.options.keyUse.setDown(true);use();}
            case 482 -> mc.options.keyUse.setDown(false);
            case 490 -> {check(server(p->p.getHealth()>before && p.getInventory().getItem(4).getCount()==1),"completed bandage heals");select(5);}
            case 503 -> {mc.options.keyUse.setDown(true);use();}
            case 518 -> server(p->{p.hurtServer(p.level(),p.damageSources().generic(),2);return true;});
            case 519 -> mc.options.keyUse.setDown(false);
            case 534 -> {check(server(p->p.getInventory().getItem(5).getCount()==2),"interrupted kit retains item");mc.options.keyUse.setDown(true);use();}
            case 607 -> mc.options.keyUse.setDown(false);
            case 615 -> {
                check(server(p->p.getInventory().getItem(5).getCount()==1 && p.getHealth()>before+4),"completed medical kit heals");
                select(0);mc.player.setYRot(-24);mc.player.setXRot(0);Screenshot.grab(mc,false);
            }
            case 630 -> {select(1);mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_FRONT);}
            case 638 -> Screenshot.grab(mc,false);
            case 655 -> {System.out.println("ZONA_QA_WORLD_COMPLETE");mc.stop();}
        }
    }
}



