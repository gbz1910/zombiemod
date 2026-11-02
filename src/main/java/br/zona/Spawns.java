package br.zona;
import net.minecraft.world.entity.*;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
final class Spawns {
    static void register(){
        LevelEvent.PotentialSpawns.BUS.addListener(e->{
            // Replace, rather than append to, each natural-spawn candidate list.
            // This preserves normal night/light/ground rules while making the world
            // an apocalypse with no passive, aquatic, ambient or vanilla hostile mobs.
            for(var data:java.util.List.copyOf(e.getSpawnerDataList()))e.removeSpawnerData(data.value());
            if(e.getMobCategory()==MobCategory.MONSTER){
                add(e,Zona.COMMON.get(),Settings.COMMON_WEIGHT.get(),Settings.HORDE_MAX.get());
                add(e,Zona.RUNNER.get(),Settings.RUNNER_WEIGHT.get(),Settings.HORDE_MAX.get());
                add(e,Zona.TANK.get(),Settings.TANK_WEIGHT.get(),1);
            }
            return false;
        });
        // In EventBus 7 a predicate returning true cancels a cancellable record event.
        EntityJoinLevelEvent.BUS.addListener(e->e.getLevel() instanceof net.minecraft.server.level.ServerLevel && e.getEntity() instanceof Mob && !(e.getEntity() instanceof Infected));
        LivingDropsEvent.BUS.addListener(e->{
            if(e.getEntity() instanceof Infected infected){
                var random=infected.getRandom();
                if(random.nextFloat()<.06f)e.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(infected.level(),infected.getX(),infected.getY(),infected.getZ(),new net.minecraft.world.item.ItemStack(Zona.SCRAP.get())));
                if(random.nextFloat()<.02f)e.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(infected.level(),infected.getX(),infected.getY(),infected.getZ(),new net.minecraft.world.item.ItemStack(Zona.FOOD_CAN.get())));
                if(random.nextFloat()<.02f)e.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(infected.level(),infected.getX(),infected.getY(),infected.getZ(),new net.minecraft.world.item.ItemStack(Zona.CLEAN_WATER.get())));
            }
            return false;
        });
    }
    static boolean withinCaps(net.minecraft.world.level.ServerLevelAccessor level,net.minecraft.core.BlockPos pos){
        var server=level.getLevel();
        if(server.getEntitiesOfClass(Infected.class,new net.minecraft.world.phys.AABB(pos).inflate(32)).size()>=Settings.LOCAL_LIMIT.get())return false;
        for(var player:server.players()){
            if(player.distanceToSqr(pos.getCenter())>48*48)continue;
            if(server.getEntitiesOfClass(Infected.class,player.getBoundingBox().inflate(48)).size()>=Settings.PLAYER_LIMIT.get())return false;
        }
        return true;
    }
    private static void add(LevelEvent.PotentialSpawns event,EntityType<?> type,int weight,int pack){
        if(weight>0)event.addSpawnerData(new MobSpawnSettings.SpawnerData(type,pack==1?ConstantInt.of(1):UniformInt.of(1,pack)),weight);
    }
}


