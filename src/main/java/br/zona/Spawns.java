package br.zona;
import net.minecraft.world.entity.*;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.level.LevelEvent;
final class Spawns {
    static void register(){
        LevelEvent.PotentialSpawns.BUS.addListener(e->{
            if(e.getMobCategory()!=MobCategory.MONSTER || e.getSpawnerDataList().stream().noneMatch(w->net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(w.value().type()).toString().equals("minecraft:zombie")))return false;
            add(e,Zona.COMMON.get(),Settings.COMMON_WEIGHT.get());add(e,Zona.RUNNER.get(),Settings.RUNNER_WEIGHT.get());add(e,Zona.TANK.get(),Settings.TANK_WEIGHT.get());return false;
        });
    }
    private static void add(LevelEvent.PotentialSpawns event,EntityType<?> type,int weight){
        if(weight>0)event.addSpawnerData(new MobSpawnSettings.SpawnerData(type,ConstantInt.of(1)),weight);
    }
}


