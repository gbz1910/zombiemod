package br.zona;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;

final class Lantern extends Item { Lantern(Properties props){super(props.stacksTo(1));} }
final class CleanWater extends Item {
    CleanWater(Properties props){super(props.food(new FoodProperties.Builder().nutrition(1).saturationModifier(.15f).build()));}
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity entity){
        ItemStack result=super.finishUsingItem(stack,level,entity);
        if(!level.isClientSide() && entity instanceof Player player && !player.getAbilities().instabuild){
            ItemStack empty=new ItemStack(Items.GLASS_BOTTLE);
            if(result.isEmpty())return empty;
            if(!player.getInventory().add(empty))player.drop(empty,false);
        }
        return result;
    }
}
final class Survival {
    static void register(){
        TickEvent.PlayerTickEvent.Post.BUS.addListener(e->{
            if(e.player() instanceof ServerPlayer player && (player.getMainHandItem().is(Zona.LANTERN.get()) || player.getOffhandItem().is(Zona.LANTERN.get())))
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,240,0,false,false,false));
        });
    }
}
