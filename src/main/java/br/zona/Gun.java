package br.zona;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
final class Gun extends Item {
    final boolean automatic;
    Gun(Properties props,boolean rifle){super(props.stacksTo(1));automatic=rifle;}
    int capacity(){return (automatic?Settings.R_CAP:Settings.P_CAP).get();}
    int interval(){return (automatic?Settings.R_DELAY:Settings.P_DELAY).get();}
    int damage(){return (automatic?Settings.R_HIT:Settings.P_HIT).get();}
    int reloadTime(){return (automatic?Settings.R_RELOAD:Settings.P_RELOAD).get();}
    Item ammo(){return (automatic?Zona.NATO:Zona.NINE).get();}
    int rounds(ItemStack s){return Math.clamp(s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getIntOr("rounds",0),0,capacity());}
    void load(ItemStack s,int amount){CustomData.update(DataComponents.CUSTOM_DATA,s,t->t.putInt("rounds",amount));}
    static void pose(ItemStack s,int frame){
        var value=new CustomModelData(List.of((float)frame),List.of(),List.of(),List.of());
        if(!value.equals(s.get(DataComponents.CUSTOM_MODEL_DATA))) s.set(DataComponents.CUSTOM_MODEL_DATA,value);
    }
    @Override public InteractionResult use(Level level,Player p,InteractionHand hand){return InteractionResult.CONSUME;}
    @Override public boolean isBarVisible(ItemStack s){return true;}
    @Override public int getBarWidth(ItemStack s){return Math.round(13f*rounds(s)/capacity());}
    @Override public int getBarColor(ItemStack s){return rounds(s)==0?0xb34638:0xcab168;}
}

