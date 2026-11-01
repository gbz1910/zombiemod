package br.zona;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.*;
public final class Infected extends Zombie {
    static final EntityDataAccessor<Integer> WINDUP=SynchedEntityData.defineId(Infected.class,EntityDataSerializers.INT);
    private int recovery;
    private LivingEntity intended;
    public Infected(EntityType<? extends Zombie> type,Level level){
        super(type,level);
        boolean runner=type==Zona.RUNNER.get(),tank=type==Zona.TANK.get();
        var profile=tank?Settings.HEAVY:runner?Settings.FAST:Settings.NORMAL;
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(profile.health.get());
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(profile.speed.get());
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(profile.damage.get());
        getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(profile.knockback.get());
        setHealth(getMaxHealth());
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder b){super.defineSynchedData(b);b.define(WINDUP,0);}
    int windup(){return getEntityData().get(WINDUP);}
    @Override protected net.minecraft.sounds.SoundEvent getAmbientSound(){return (getType()==Zona.TANK.get()?Audio.TANK:getType()==Zona.RUNNER.get()?Audio.RUNNER:Audio.COMMON).get();}
    @Override protected boolean convertsInWater(){return false;}
    @Override public boolean doHurtTarget(ServerLevel level,Entity victim){
        if(getType()!=Zona.TANK.get())return super.doHurtTarget(level,victim);
        if(recovery==0 && windup()==0 && victim instanceof LivingEntity target){
            intended=target;getNavigation().stop();getEntityData().set(WINDUP,17);
        }
        return false;
    }
    @Override public void tick(){
        super.tick();if(!(level() instanceof ServerLevel server))return;
        if(recovery>0){recovery--;getNavigation().stop();}
        int t=windup();if(t<=0)return;
        getNavigation().stop();getEntityData().set(WINDUP,t-1);
        if(t==1){if(intended!=null && intended.isAlive() && isWithinMeleeAttackRange(intended) && hasLineOfSight(intended))super.doHurtTarget(server,intended);
            intended=null;recovery=24;}
    }
}



