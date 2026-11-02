package br.zona;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
final class InfectedRenderer extends MobRenderer<Infected,InfectedRenderer.State,InfectedRenderer.Model> {
    static final class State extends LivingEntityRenderState {int windup,variant;float swing;}
    private final Identifier[] textures=new Identifier[3];
    InfectedRenderer(EntityRendererProvider.Context ctx,int kind){
        super(ctx,new Model(kind),kind==2?.8f:.48f);
        String key=new String[]{"comum","corredor","tanque"}[kind];
        for(int i=0;i<textures.length;i++)textures[i]=Zona.id("textures/entity/"+key+"_"+i+".png");
    }
    @Override public State createRenderState(){return new State();}
    @Override public Identifier getTextureLocation(State s){return textures[Math.floorMod(s.variant,textures.length)];}
    @Override public void extractRenderState(Infected z,State s,float partial){
        super.extractRenderState(z,s,partial);s.windup=z.windup();s.variant=z.variant();s.swing=z.getSwingAnimation(partial);
    }
    static final class Model extends EntityModel<State> {
        final ModelPart[] bones;final float[][][] clips;final int kind;
        Model(int kind){
            super(kind==2?InfectedArt.tanque():kind==1?InfectedArt.corredor():InfectedArt.comum());this.kind=kind;
            clips=kind==2?InfectedMotion.TANQUE:kind==1?InfectedMotion.CORREDOR:InfectedMotion.COMUM;
            bones=java.util.stream.Stream.of("torso","head","arm_l","arm_r","leg_l","leg_r").map(root()::getChild).toArray(ModelPart[]::new);
        }
        @Override public void setupAnim(State s){
            super.setupAnim(s);int clip=0;float phase=(s.ageInTicks%48)/48;
            if(s.walkAnimationSpeed>.03f){clip=kind==1?2:1;phase=(s.walkAnimationPos*(kind==1?.24f:.15f))%1;}
            if(s.swing>0){clip=3;phase=s.swing;}
            if(s.hasRedOverlay){clip=4;phase=(s.ageInTicks%7)/7;}
            if(s.windup>0){clip=6;phase=1-s.windup/17f;}
            if(s.deathTime>0){clip=5;phase=Math.min(1,s.deathTime/20f);}
            float frame=Math.clamp(phase,0f,1f)*4;int a=(int)frame,b=Math.min(4,a+1);float t=frame-a;
            for(int i=0;i<bones.length;i++){
                float[] p=clips[clip][a],q=clips[clip][b];int j=2*i;
                bones[i].xRot=p[j]+t*(q[j]-p[j]);bones[i].zRot=p[j+1]+t*(q[j+1]-p[j+1]);
            }
            bones[1].yRot=(float)Math.toRadians(s.yRot);bones[1].xRot+=(float)Math.toRadians(s.xRot);
        }
    }
}


