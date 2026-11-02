package br.zona;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.*;
import net.minecraft.world.phys.*;
public final class Infected extends Zombie {
    static final EntityDataAccessor<Integer> WINDUP=SynchedEntityData.defineId(Infected.class,EntityDataSerializers.INT);
    static final EntityDataAccessor<Integer> VARIANT=SynchedEntityData.defineId(Infected.class,EntityDataSerializers.INT);
    private int recovery;
    private LivingEntity intended;
    private long appliedEvolutionDay=-1;
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
    @Override protected void defineSynchedData(SynchedEntityData.Builder b){super.defineSynchedData(b);b.define(WINDUP,0);b.define(VARIANT,0);}
    int windup(){return getEntityData().get(WINDUP);}
    int variant(){return getEntityData().get(VARIANT);}
    @Override protected boolean isSunSensitive(){return false;}
    @Override public boolean checkSpawnRules(LevelAccessor level,EntitySpawnReason reason){
        // Daytime spawns are part of the apocalypse. Keep peaceful mode blocked;
        // Mob.checkSpawnObstruction still enforces solid ground and collision.
        return reason==EntitySpawnReason.NATURAL?level.getDifficulty()!=net.minecraft.world.Difficulty.PEACEFUL:super.checkSpawnRules(level,reason);
    }
    AABB headBox(){
        double h=getBbHeight(),w=getBbWidth();
        double y=getY()+h*(getType()==Zona.TANK.get()?.835:getType()==Zona.RUNNER.get()?.84:.82);
        double hx=getType()==Zona.TANK.get()?.28:w*.30,hz=getType()==Zona.TANK.get()?.28:w*.30;
        double hy=getType()==Zona.TANK.get()?.27:h*.115;
        float[] pose=headPose();
        double pitch=Math.toRadians(getXRot())+pose[0],roll=pose[1];
        Vec3 forward=getLookAngle().normalize();
        double shift=Math.sin(pitch)*hy;
        double cx=getX()+Math.sin(roll)*hy+forward.x*shift,cz=getZ()+forward.z*shift;
        y+=hy*(1-Math.cos(pitch));
        return new AABB(cx-hx,y-hy,cz-hz,cx+hx,y+hy,cz+hz);
    }
    private float[] headPose(){
        int clip=0;float phase=(tickCount%48)/48f;
        if(walkAnimation.speed()>.03f){clip=getType()==Zona.RUNNER.get()?2:1;phase=(walkAnimation.position()*(getType()==Zona.RUNNER.get()?.24f:.15f))%1;}
        float swing=getSwingAnimation(1f);if(swing>0){clip=3;phase=swing;}
        if(hurtTime>0){clip=4;phase=(tickCount%7)/7f;}
        if(windup()>0){clip=6;phase=1-windup()/17f;}
        if(deathTime>0){clip=5;phase=Math.min(1,deathTime/20f);}
        float frame=Math.clamp(phase,0f,1f)*4;int a=(int)frame,b=Math.min(4,a+1);float t=frame-a;
        float[][][] clips=getType()==Zona.TANK.get()?InfectedMotion.TANQUE:getType()==Zona.RUNNER.get()?InfectedMotion.CORREDOR:InfectedMotion.COMUM;
        return new float[]{clips[clip][a][2]+t*(clips[clip][b][2]-clips[clip][a][2]),clips[clip][a][3]+t*(clips[clip][b][3]-clips[clip][a][3])};
    }
    AABB torsoBox(){return getBoundingBox().inflate(.01,-getBbHeight()*.24,.01);}
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,DifficultyInstance difficulty,EntitySpawnReason reason,SpawnGroupData data){
        var result=super.finalizeSpawn(level,difficulty,reason,data);
        getEntityData().set(VARIANT,getRandom().nextInt(3));
        return result;
    }
    @Override protected void addAdditionalSaveData(ValueOutput output){super.addAdditionalSaveData(output);output.putInt("Variant",variant());}
    @Override protected void readAdditionalSaveData(ValueInput input){super.readAdditionalSaveData(input);getEntityData().set(VARIANT,Math.floorMod(input.getIntOr("Variant",0),3));}
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
        applyEvolution(server);
        if(recovery>0){recovery--;getNavigation().stop();}
        int t=windup();if(t<=0)return;
        getNavigation().stop();getEntityData().set(WINDUP,t-1);
        if(t==1){if(intended!=null && intended.isAlive() && isWithinMeleeAttackRange(intended) && hasLineOfSight(intended))super.doHurtTarget(server,intended);
            intended=null;recovery=24;}
    }
    private void applyEvolution(ServerLevel level){
        long clockTicks=level.clockManager().getInstance(level.dimensionType().defaultClock()).totalTicks();
        long day=Math.max(0,Math.min(Settings.EVOLUTION_DAY_CAP.get(),clockTicks/24000));
        if(day==appliedEvolutionDay)return;
        var profile=getType()==Zona.TANK.get()?Settings.HEAVY:getType()==Zona.RUNNER.get()?Settings.FAST:Settings.NORMAL;
        double oldMax=getMaxHealth(),healthRatio=oldMax<=0?1:getHealth()/oldMax;
        double hp=profile.health.get()*(1+.02*day),damage=profile.damage.get()*(1+.01*day),speed=profile.speed.get()*(1+.01*day);
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
        setHealth((float)Math.min(hp,Math.max(1,hp*healthRatio)));
        appliedEvolutionDay=day;
    }
}



