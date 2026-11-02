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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
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
    private static final UUID[] meleeTargets=new UUID[3];
    private static final UUID[] initialTargets=new UUID[3];
    private static final UUID[] sunTargets=new UUID[3];
    static Minecraft mc(){return Minecraft.getInstance();}
    static <T> T server(Function<ServerPlayer,T> operation){
        var srv=mc().getSingleplayerServer();
        return srv.submit(()->operation.apply(srv.getPlayerList().getPlayers().getFirst())).join();
    }
    static Item item(String id){return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("zona",id));}
    static ItemStack stack(String id){return new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(id.contains(":")?id:"minecraft:"+id)));}
    static ItemStack[] grid(int size){var a=new ItemStack[size];Arrays.fill(a,ItemStack.EMPTY);return a;}
    static boolean hasItem(ServerPlayer p,Item wanted){for(int i=0;i<p.getInventory().getContainerSize();i++)if(p.getInventory().getItem(i).is(wanted))return true;return false;}
    static String recipe(ServerPlayer p,int w,int h,ItemStack[] items){return p.level().recipeAccess().getRecipeFor(RecipeType.CRAFTING,CraftingInput.of(w,h,Arrays.asList(items)),p.level()).map(r->r.id().identifier().getPath()).orElse("none");}
    static void recipeCheck(ServerPlayer p,String name,int w,int h,ItemStack[] items){check(recipe(p,w,h,items).equals(name),"crafting recipe "+name);}
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
                    String[] ids={"pistola","m4","municao_9mm","municao_556","bandagem","kit","machete","lanterna","lata_comida","garrafa_agua_limpa","sucata_metalica","mola_arma","gatilho","cano_arma","corpo_pistola","receptor_m4"};
                    int[] counts={1,1,17,48,2,2,1,1,2,2,3,1,1,1,1,1};
                    for(int i=0;i<ids.length;i++){check(item(ids[i])!=Items.AIR,"registered "+ids[i]);inv.setItem(i,new ItemStack(item(ids[i]),counts[i]));}
                    var g=grid(3);g[0]=stack("iron_ingot");g[1]=stack("iron_ingot");g[2]=stack("iron_ingot");recipeCheck(p,"sucata_metalica",3,1,g);
                    g=grid(3);for(int i=0;i<3;i++)g[i]=stack("iron_ingot");recipeCheck(p,"mola_arma",1,3,g);
                    g=grid(2);g[0]=stack("iron_nugget");g[1]=stack("zona:mola_arma");recipeCheck(p,"gatilho",2,1,g);
                    g=grid(3);for(int i=0;i<3;i++)g[i]=stack("zona:sucata_metalica");recipeCheck(p,"cano_arma",3,1,g);
                    g=grid(9);for(int i:new int[]{0,1,2,6,7,8})g[i]=stack("zona:sucata_metalica");g[3]=stack("iron_ingot");g[5]=stack("iron_ingot");recipeCheck(p,"corpo_pistola",3,3,g);
                    g=grid(9);for(int i:new int[]{0,1,2,6,7,8})g[i]=stack("zona:sucata_metalica");g[3]=stack("zona:sucata_metalica");g[4]=stack("iron_ingot");g[5]=stack("zona:sucata_metalica");recipeCheck(p,"receptor_m4",3,3,g);
                    g=grid(9);g[0]=stack("zona:corpo_pistola");g[1]=stack("zona:cano_arma");g[2]=stack("zona:gatilho");g[4]=stack("zona:mola_arma");g[5]=stack("iron_ingot");g[7]=stack("iron_ingot");recipeCheck(p,"pistola",3,3,g);
                    g=grid(9);g[0]=stack("zona:receptor_m4");g[1]=stack("zona:cano_arma");g[2]=stack("zona:gatilho");g[4]=stack("zona:mola_arma");for(int i=6;i<9;i++)g[i]=stack("iron_ingot");recipeCheck(p,"m4",3,3,g);
                    g=grid(9);g[2]=stack("iron_ingot");g[4]=stack("zona:sucata_metalica");g[5]=stack("iron_ingot");g[6]=stack("stick");recipeCheck(p,"machete",3,3,g);
                    g=grid(9);g[1]=stack("iron_nugget");g[3]=stack("iron_nugget");g[4]=stack("glass_pane");g[5]=stack("iron_nugget");g[7]=stack("torch");recipeCheck(p,"lanterna",3,3,g);
                    g=grid(2);g[0]=stack("glass_bottle");g[1]=stack("water_bucket");recipeCheck(p,"garrafa_agua_limpa",2,1,g);
                    for(int i=0;i<3;i++){
                        String key=new String[]{"comum","corredor","tanque"}[i];
                        var type=BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("zona",key));
                        var mob=(Mob)type.create(p.level(),EntitySpawnReason.COMMAND);
                        check(mob!=null,"spawn "+key);mob.setNoAi(true);
                        var clock=p.level().dimensionType().defaultClock().orElseThrow();
                        for(var period:new long[]{1000,7000,13000}){
                            p.level().getServer().clockManager().setTotalTicks(clock,period);
                            check(mob.checkSpawnRules(p.level(),EntitySpawnReason.NATURAL),"natural spawn rule "+key+" at "+period);
                        }
                        p.level().getServer().clockManager().setTotalTicks(clock,0);
                        mob.snapTo(p.getX()+i*3,p.getY(),p.getZ()+5);
                        mob.setYRot(180);mob.setYHeadRot(180);mob.setYBodyRot(180);p.level().addFreshEntity(mob);
                        initialTargets[i]=mob.getUUID();if(i==0)dummy=mob.getUUID();
                    }
                    var vanillaZombie=(Mob)BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace("zombie")).create(p.level(),EntitySpawnReason.COMMAND);
                    vanillaZombie.snapTo(p.getX()-3,p.getY(),p.getZ()+5);p.level().addFreshEntity(vanillaZombie);
                    p.getFoodData().setFoodLevel(10);
                    for(int x=-3;x<=9;x++)for(int z=-2;z<=7;z++)p.level().setBlockAndUpdate(p.blockPosition().offset(x,4,z),Blocks.STONE.defaultBlockState());
                    return true;
                });
            }
            case 108 -> check(server(p->p.level().getEntitiesOfClass(net.minecraft.world.entity.monster.zombie.Zombie.class,p.getBoundingBox().inflate(12)).stream().noneMatch(z->!"zona".equals(BuiltInRegistries.ENTITY_TYPE.getKey(z.getType()).getNamespace()))),"vanilla mobs blocked");
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
            case 240 -> Screenshot.grab(mc,false);
            case 251 -> {mc.options.keyAttack.setDown(false);mc.options.keyUse.setDown(false);}
            case 258 -> {
                check(server(p->rounds(p.getMainHandItem())==11 && hp(p)==before),"one pistol shot per press and wall protection");
                server(p->{wall(p,false);return true;});mc.player.setXRot(0);
            }
            case 265 -> mc.options.keyAttack.setDown(true);
            case 269 -> mc.options.keyAttack.setDown(false);
            case 285 -> {check(server(p->hp(p)==10),"pistol headshot doubles torso damage");Screenshot.grab(mc,false);mc.player.setXRot(7);mc.options.keyAttack.setDown(true);}
            case 286 -> mc.options.keyAttack.setDown(false);
            case 300 -> {check(server(p->hp(p)==4),"pistol torso shot uses normal damage");mc.player.setXRot(0);select(1);}
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
            case 630 -> {
                server(p->{double[] xs={-.55,0,.55};for(int i=0;i<xs.length;i++){
                    var type=BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("zona","comum"));
                    var mob=(Mob)type.create(p.level(),EntitySpawnReason.COMMAND);mob.setNoAi(true);
                    mob.snapTo(p.getX()+xs[i],p.getY(),p.getZ()+2.35);p.level().addFreshEntity(mob);meleeTargets[i]=mob.getUUID();
                }return true;});
                select(6);mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_FRONT);
            }
            case 635 -> mc.options.keyAttack.setDown(true);
            case 636 -> mc.options.keyAttack.setDown(false);
            case 646 -> {check(server(p->{for(UUID id:meleeTargets){var entity=p.level().getEntity(id);if(!(entity instanceof LivingEntity z)||z.getHealth()!=10)return false;}return true;}),"machete hits three nearby targets once each");Screenshot.grab(mc,false);select(7);}
            case 656 -> check(server(p->p.hasEffect(net.minecraft.world.effect.MobEffects.NIGHT_VISION)),"held lantern grants night vision");
            case 660 -> {select(8);server(p->{p.getFoodData().setFoodLevel(10);return true;});mc.options.keyUse.setDown(true);use();}
            case 700 -> mc.options.keyUse.setDown(false);
            case 706 -> {check(server(p->p.getFoodData().getFoodLevel()>10 && p.getInventory().getItem(8).getCount()==1),"canned food restores hunger");select(9);mc.options.keyUse.setDown(true);use();}
            case 750 -> mc.options.keyUse.setDown(false);
            case 756 -> {check(server(p->p.getFoodData().getFoodLevel()>10 && p.getInventory().getItem(9).getCount()==1 && hasItem(p,Items.GLASS_BOTTLE)),"clean water restores hunger and returns glass bottle");Screenshot.grab(mc,false);}
            case 765 -> server(p->{
                for(int x=-3;x<=9;x++)for(int z=-2;z<=7;z++)p.level().setBlockAndUpdate(p.blockPosition().offset(x,4,z),Blocks.AIR.defaultBlockState());
                var clock=p.level().dimensionType().defaultClock().orElseThrow();p.level().getServer().clockManager().setTotalTicks(clock,54000);
                for(int i=0;i<3;i++){
                    String key=new String[]{"comum","corredor","tanque"}[i];
                    var type=BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("zona",key));
                    var mob=(Mob)type.create(p.level(),EntitySpawnReason.COMMAND);mob.setNoAi(true);
                    mob.snapTo(p.getX()-3+i*3,p.getY(),p.getZ()+9);p.level().addFreshEntity(mob);sunTargets[i]=mob.getUUID();
                }
                return true;
            });
            case 780 -> check(server(p->{var seen=new HashSet<String>();for(var z:p.level().getEntitiesOfClass(LivingEntity.class,p.getBoundingBox().inflate(12)).stream().filter(e->e.getClass().getName().equals("br.zona.Infected")).toList()){
                String type=BuiltInRegistries.ENTITY_TYPE.getKey(z.getType()).getPath();double expected=type.equals("tanque")?72.8:type.equals("corredor")?14.56:22.88;
                if(z.getMaxHealth()<expected-.01)return false;seen.add(type);
            }
                for(UUID id:sunTargets){var entity=p.level().getEntity(id);if(!(entity instanceof LivingEntity z)||z.isOnFire())return false;}
                return seen.containsAll(Set.of("comum","corredor","tanque"));}),"all infected types evolve after two days and survive direct sunlight");
            case 790 -> {System.out.println("ZONA_QA_WORLD_COMPLETE");mc.stop();}
        }
    }
}




