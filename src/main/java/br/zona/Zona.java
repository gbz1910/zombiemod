package br.zona;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import net.minecraftforge.common.loot.IGlobalLootModifier;
@Mod(Zona.ID)
public final class Zona {
    public static final String ID="zona";
    static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,ID);
    static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,ID);
    static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,ID);
    static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS=DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,ID);
    static final RegistryObject<MapCodec<? extends IGlobalLootModifier>> CRATE_LOOT=LOOT_MODIFIERS.register("suprimentos_bau",()->CrateLoot.CODEC);
    static Identifier id(String path){return Identifier.fromNamespaceAndPath(ID,path);}
    static RegistryObject<Item> item(String name, Function<Item.Properties,Item> factory){
        return ITEMS.register(name,()->factory.apply(properties(name)));
    }
    static Item.Properties properties(String name){
        var props=new Item.Properties().setId(ITEMS.key(name));
        if(!name.startsWith("ovo_"))props.component(net.minecraft.core.component.DataComponents.LORE,new net.minecraft.world.item.component.ItemLore(java.util.List.of(Component.translatable("lore.zona."+name))));
        return props;
    }
    static RegistryObject<EntityType<Infected>> infected(String name,float w,float h){
        return ENTITIES.register(name,()->EntityType.Builder.<Infected>of(Infected::new,MobCategory.MONSTER)
            .sized(w,h).clientTrackingRange(8).build(ENTITIES.key(name)));
    }
    public static final RegistryObject<EntityType<Infected>> COMMON=infected("comum",.72f,1.98f), RUNNER=infected("corredor",.66f,1.92f), TANK=infected("tanque",1.3f,2.7f);
    static final RegistryObject<Item> NINE=item("municao_9mm",Item::new), NATO=item("municao_556",Item::new),
        SCRAP=item("sucata_metalica",Item::new), SPRING=item("mola_arma",Item::new), TRIGGER=item("gatilho",Item::new), BARREL=item("cano_arma",Item::new),
        PISTOL_BODY=item("corpo_pistola",Item::new), M4_RECEIVER=item("receptor_m4",Item::new);
    static final RegistryObject<Item> PISTOL=item("pistola",p->new Gun(p,false)), M4=item("m4",p->new Gun(p,true));
    static final RegistryObject<Item> BANDAGE=item("bandagem",p->new Treatment(p,32,4)), KIT=item("kit",p->new Treatment(p,65,12));
    static final RegistryObject<Item> MACHETE=item("machete",Machete::new), LANTERN=item("lanterna",Lantern::new),
        FOOD_CAN=item("lata_comida",p->new Item(p.food(new FoodProperties.Builder().nutrition(3).saturationModifier(.25f).build()))),
        CLEAN_WATER=item("garrafa_agua_limpa",CleanWater::new);
    static {
        for(var mob:java.util.List.of(COMMON,RUNNER,TANK))
            item("ovo_"+mob.getId().getPath(),p->new SpawnEggItem(p.spawnEgg(mob.get())));
        TABS.register("suprimentos",()->CreativeModeTab.builder().title(Component.translatable("tab.zona"))
            .icon(()->PISTOL.get().getDefaultInstance()).displayItems((params,out)->ITEMS.getEntries().forEach(e->out.accept(e.get()))).build());
    }
    public Zona(FMLJavaModLoadingContext ctx){
        var bus=ctx.getModBusGroup();
        Audio.SOUNDS.register(bus); ENTITIES.register(bus);ITEMS.register(bus);TABS.register(bus);LOOT_MODIFIERS.register(bus);
        ctx.registerConfig(ModConfig.Type.COMMON,Settings.SPEC);
        EntityAttributeCreationEvent.BUS.addListener(e->{
            e.put(COMMON.get(),Zombie.createAttributes().build());
            e.put(RUNNER.get(),Zombie.createAttributes().build());
            e.put(TANK.get(),Zombie.createAttributes().build());
        });
        SpawnPlacementRegisterEvent.BUS.addListener(e->{
            for(var type:java.util.List.of(COMMON,RUNNER,TANK))
                // Entity rules and obstruction are checked by Mob at spawn time;
                // omitting Monster's darkness test permits daytime infected.
                e.register(type.get(),SpawnPlacementTypes.ON_GROUND,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,(mob,level,reason,pos,rng)->Spawns.withinCaps(level,pos),SpawnPlacementRegisterEvent.Operation.REPLACE);
        });
        Combat.register(); Spawns.register(); Survival.register(); WorldProgress.register();
    }
}





