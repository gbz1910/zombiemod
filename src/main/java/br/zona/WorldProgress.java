package br.zona;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;

final class WorldProgress {
    private static long announcedDay=-1;
    static void register(){
        TickEvent.LevelTickEvent.Post.BUS.addListener(event->{
            if(!(event.level() instanceof ServerLevel level) || level!=level.getServer().overworld())return;
            long time=level.clockManager().getInstance(level.dimensionType().defaultClock()).totalTicks();
            long day=time/24000;
            if(day>0 && day!=announcedDay){
                announcedDay=day;
                for(var player:level.players())player.displayClientMessage(Component.literal("Os zumbis estão mais perigosos."),true);
            }
        });
    }
}
