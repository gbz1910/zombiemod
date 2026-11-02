package br.zona;
import net.minecraft.world.entity.*;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
final class Spawns {
    static void register(){
        LevelEvent.PotentialSpawns.BUS.addListener(e->{
            // Replace, rather than append to, each natural-spawn candidate list.
            // This preserves normal night/light/ground rules while making the world
            // an apocalypse with no passive, aquatic, ambient or vanilla hostile mobs.
            for(var data:java.util.List.copyOf(e.getSpawnerDataList()))e.removeSpawnerData(data.value());
            if(e.getMobCategory()==MobCategory.MONSTER){
                add(e,Zona.COMMON.get(),Settings.COMMON_WEIGHT.get());
                add(e,Zona.RUNNER.get(),Settings.RUNNER_WEIGHT.get());
                add(e,Zona.TANK.get(),Settings.TANK_WEIGHT.get());
            }
            return false;
        });
        EntityJoinLevelEvent.BUS.addListener(e->{
            if(e.getLevel() instanceof net.minecraft.server.level.ServerLevel && e.getEntity() instanceof Mob && !(e.getEntity() instanceof Infected))e.cancel();
            return false;
        });
    }
    private static void add(LevelEvent.PotentialSpawns event,EntityType<?> type,int weight){
        if(weight>0)event.addSpawnerData(new MobSpawnSettings.SpawnerData(type,ConstantInt.of(1)),weight);
    }
}


