package br.zona;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
final class Treatment extends Item {
    final int duration,hearts;
    Treatment(Properties props,int ticks,int health){super(props.stacksTo(8));duration=ticks;hearts=health;}
    @Override public InteractionResult use(Level level,Player p,InteractionHand hand){
        if(p.getHealth()>=p.getMaxHealth())return InteractionResult.FAIL;
        p.startUsingItem(hand);return InteractionResult.CONSUME;
    }
    @Override public int getUseDuration(ItemStack stack,LivingEntity p){return duration;}
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack){return ItemUseAnimation.BRUSH;}
    @Override public void onUseTick(Level level,LivingEntity p,ItemStack s,int left){
        if(!level.isClientSide() && left%12==0)level.playSound(null,p.blockPosition(),SoundEvents.WOOL_HIT,SoundSource.PLAYERS,.45f,1.0f);
    }
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity p){
        if(!level.isClientSide() && p.getHealth()<p.getMaxHealth()){
            p.heal(hearts);if(!(p instanceof Player player && player.isCreative()))stack.shrink(1);
        }
        return stack;
    }
}

