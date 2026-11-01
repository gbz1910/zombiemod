package br.zona;
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
@Mod(Zona.ID)
public final class Zona {
    public static final String ID="zona";
    static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,ID);
    static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,ID);
    static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,ID);
    static Identifier id(String path){return Identifier.fromNamespaceAndPath(ID,path);}
    static RegistryObject<Item> item(String name, Function<Item.Properties,Item> factory){
        return ITEMS.register(name,()->factory.apply(new Item.Properties().setId(ITEMS.key(name))));
    }
    static RegistryObject<EntityType<Infected>> infected(String name,float w,float h){
        return ENTITIES.register(name,()->EntityType.Builder.<Infected>of(Infected::new,MobCategory.MONSTER)
            .sized(w,h).clientTrackingRange(8).build(ENTITIES.key(name)));
    }
    public static final RegistryObject<EntityType<Infected>> COMMON=infected("comum",.6f,1.95f), RUNNER=infected("corredor",.54f,1.8f), TANK=infected("tanque",1.15f,2.6f);
    static final RegistryObject<Item> NINE=item("municao_9mm",Item::new), NATO=item("municao_556",Item::new);
    static final RegistryObject<Item> PISTOL=item("pistola",p->new Gun(p,false)), M4=item("m4",p->new Gun(p,true));
    static final RegistryObject<Item> BANDAGE=item("bandagem",p->new Treatment(p,32,4)), KIT=item("kit",p->new Treatment(p,65,12));
    static {
        for(var mob:java.util.List.of(COMMON,RUNNER,TANK))
            item("ovo_"+mob.getId().getPath(),p->new SpawnEggItem(p.spawnEgg(mob.get())));
        TABS.register("suprimentos",()->CreativeModeTab.builder().title(Component.translatable("tab.zona"))
            .icon(()->PISTOL.get().getDefaultInstance()).displayItems((params,out)->ITEMS.getEntries().forEach(e->out.accept(e.get()))).build());
    }
    public Zona(FMLJavaModLoadingContext ctx){
        var bus=ctx.getModBusGroup();
        ENTITIES.register(bus);ITEMS.register(bus);TABS.register(bus);
        ctx.registerConfig(ModConfig.Type.COMMON,Settings.SPEC);
        EntityAttributeCreationEvent.BUS.addListener(e->{
            e.put(COMMON.get(),Zombie.createAttributes().build());
            e.put(RUNNER.get(),Zombie.createAttributes().build());
            e.put(TANK.get(),Zombie.createAttributes().build());
        });
        SpawnPlacementRegisterEvent.BUS.addListener(e->{
            for(var type:java.util.List.of(COMMON,RUNNER,TANK))
                e.register(type.get(),SpawnPlacementTypes.ON_GROUND,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,Monster::checkMonsterSpawnRules,SpawnPlacementRegisterEvent.Operation.REPLACE);
        });
        Combat.register(); Spawns.register();
    }
}



