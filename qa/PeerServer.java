package com.example.examplemod;
import java.nio.file.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid="examplemod")
public final class PeerServer {
    private static int phase,elapsed;private static ServerPlayer shooter,observer;private static Mob target,tank;private static float health;
    private static final Path signals=Path.of(System.getenv().getOrDefault("ZONA_SIGNALS","."));
    private static Identifier id(String path){return Identifier.fromNamespaceAndPath("zona",path);}
    private static Mob mob(String kind,double x,double z,boolean ai){
        var level=shooter.level();Mob m=(Mob)BuiltInRegistries.ENTITY_TYPE.getValue(id(kind)).create(level,EntitySpawnReason.COMMAND);
        m.snapTo(x,85,z);m.setNoAi(!ai);m.setPersistenceRequired();level.addFreshEntity(m);return m;
    }
    private static void require(boolean pass,String message){
        if(!pass)throw new IllegalStateException("ZONA_PEER_FAILED "+message);
        System.out.println("ZONA_PEER_SERVER_PASS "+message);
    }
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent.Post e){
        var server=e.server();if(!server.isDedicatedServer() || phase==99)return;
        if(phase==0){
            for(var p:server.getPlayerList().getPlayers()){
                if(p.getScoreboardName().equalsIgnoreCase("ZonaShooter"))shooter=p;
                if(p.getScoreboardName().equalsIgnoreCase("ZonaObserver"))observer=p;
            }
            if(shooter==null || observer==null)return;
            var level=shooter.level();
            for(int x=-4;x<13;x++)for(int z=-4;z<15;z++){
                level.setBlockAndUpdate(new BlockPos(x,84,z),Blocks.STONE.defaultBlockState());
                for(int y=85;y<89;y++)level.setBlockAndUpdate(new BlockPos(x,y,z),Blocks.AIR.defaultBlockState());
                level.setBlockAndUpdate(new BlockPos(x,89,z),Blocks.STONE.defaultBlockState());
            }
            shooter.teleportTo(level,.5,85,.5,java.util.Set.of(),0,0,true);
            observer.teleportTo(level,3.5,85,2.5,java.util.Set.of(),90,0,true);
            shooter.getInventory().setItem(0,new ItemStack(BuiltInRegistries.ITEM.getValue(id("pistola"))));
            shooter.getInventory().setItem(1,new ItemStack(BuiltInRegistries.ITEM.getValue(id("municao_9mm")),12));
            target=mob("tanque",.5,5.5,false);health=target.getHealth();phase=1;return;
        }
        if(phase==1 && Files.exists(signals.resolve("observer.ok")) && Files.exists(signals.resolve("shooter.ok"))){
            float damage=health-target.getHealth();require(damage>4 && damage<=7.01,"one server damage application");
            require(shooter.getInventory().getItem(1).isEmpty(),"one ammunition debit");
            tank=mob("tanque",1.55,.5,false);health=shooter.getHealth();
            tank.doHurtTarget(shooter.level(),shooter);phase=2;elapsed=0;return;
        }
        if(phase==2){
            elapsed++;
            if(elapsed==8)require(shooter.getHealth()==health,"tank telegraphs before damage");
            if(elapsed==22){
                require(shooter.getHealth()<health,"tank applies delayed hit");
                for(int i=0;i<24;i++)mob(new String[]{"comum","corredor","tanque"}[i%3],4.5+i%6,6.5+i/6,true);
                phase=3;elapsed=0;
            }
        } else if(phase==3 && ++elapsed==120){
            System.out.println("ZONA_GROUP_SAMPLE_24_MOBS_AVG_TICK_MS "+server.getAverageTickTimeNanos()/1000000.0);
            try{Files.writeString(signals.resolve("server.ok"),"complete");}catch(Exception ex){throw new IllegalStateException(ex);}
            phase=99;
        }
    }
}

