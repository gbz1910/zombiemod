package br.zona;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
final class InfectedArt {
static ModelPart comum(){var mesh=new MeshDefinition();var root=mesh.getRoot();
root.addOrReplaceChild("torso",CubeListBuilder.create().texOffs(64,0).addBox(-4.0f,-12f,-2.0f,8f,12f,4f).texOffs(64,32).addBox(-4.1f,-2f,-2.1f,8.2f,1.3f,4.2f).texOffs(0,64).addBox(-3.0f,-12f,-2.2f,1.6f,10f,0.5f),PartPose.offset(0f,12f,0f));
root.addOrReplaceChild("head",CubeListBuilder.create().texOffs(0,0).addBox(-4.0f,-8f,-4.0f,8f,8f,8f).texOffs(64,96).addBox(-4.12f,-8f,-4.45f,8.24f,1.9f,0.24f).texOffs(0,96).addBox(-3.0f,-4.56f,-4.48f,1.5f,0.8f,0.18f).texOffs(0,96).addBox(1.5f,-4.56f,-4.48f,1.5f,0.8f,0.18f).texOffs(64,64).addBox(-1f,-1.9f,-4.46f,2.8f,0.8f,0.16f),PartPose.offset(0f,0f,0f));
root.addOrReplaceChild("arm_l",CubeListBuilder.create().texOffs(0,0).addBox(-2.0f,0f,-2.0f,4f,12f,4f).texOffs(64,0).addBox(-2.15f,-0.4f,-2.15f,4.3f,4f,4.3f),PartPose.offset(-6.0f,1f,0f));
root.addOrReplaceChild("arm_r",CubeListBuilder.create().texOffs(0,0).addBox(-2.0f,0f,-2.0f,4f,12f,4f).texOffs(64,0).addBox(-2.15f,-0.4f,-2.15f,4.3f,6f,4.3f),PartPose.offset(6.0f,1f,0f));
root.addOrReplaceChild("leg_l",CubeListBuilder.create().texOffs(0,32).addBox(-1.9f,0f,-2f,3.8f,12f,4f).texOffs(64,32).addBox(-2.0f,9f,-2.6f,4.0f,3f,4.8f),PartPose.offset(-2.0f,12f,0f));
root.addOrReplaceChild("leg_r",CubeListBuilder.create().texOffs(0,32).addBox(-1.9f,0f,-2f,3.8f,12f,4f).texOffs(64,32).addBox(-2.0f,9f,-2.6f,4.0f,3f,4.8f),PartPose.offset(2.0f,12f,0f));
return LayerDefinition.create(mesh,128,128).bakeRoot();}
static ModelPart corredor(){var mesh=new MeshDefinition();var root=mesh.getRoot();
root.addOrReplaceChild("torso",CubeListBuilder.create().texOffs(64,0).addBox(-3.0f,-11f,-2.0f,6f,11f,4f).texOffs(64,32).addBox(-3.1f,-2f,-2.1f,6.2f,1.3f,4.2f).texOffs(0,64).addBox(-2.0f,-11f,-2.2f,1.6f,9f,0.5f),PartPose.offset(0f,12f,0f));
root.addOrReplaceChild("head",CubeListBuilder.create().texOffs(0,0).addBox(-3.5f,-7f,-3.5f,7f,7f,7f).texOffs(64,96).addBox(-3.62f,-7f,-3.95f,7.24f,1.9f,0.24f).texOffs(0,96).addBox(-2.5f,-3.99f,-3.98f,1.5f,0.8f,0.18f).texOffs(0,96).addBox(1.0f,-3.99f,-3.98f,1.5f,0.8f,0.18f).texOffs(64,64).addBox(-1f,-1.9f,-3.96f,2.8f,0.8f,0.16f),PartPose.offset(0f,1f,0f));
root.addOrReplaceChild("arm_l",CubeListBuilder.create().texOffs(0,0).addBox(-1.5f,0f,-1.5f,3f,12f,3f).texOffs(64,0).addBox(-1.65f,-0.4f,-1.65f,3.3f,4f,3.3f),PartPose.offset(-4.5f,2f,0f));
root.addOrReplaceChild("arm_r",CubeListBuilder.create().texOffs(0,0).addBox(-1.5f,0f,-1.5f,3f,12f,3f).texOffs(64,0).addBox(-1.65f,-0.4f,-1.65f,3.3f,6f,3.3f),PartPose.offset(4.5f,2f,0f));
root.addOrReplaceChild("leg_l",CubeListBuilder.create().texOffs(0,32).addBox(-1.4f,0f,-2f,2.8f,12f,4f).texOffs(64,32).addBox(-1.5f,9f,-2.6f,3.0f,3f,4.8f),PartPose.offset(-1.5f,12f,0f));
root.addOrReplaceChild("leg_r",CubeListBuilder.create().texOffs(0,32).addBox(-1.4f,0f,-2f,2.8f,12f,4f).texOffs(64,32).addBox(-1.5f,9f,-2.6f,3.0f,3f,4.8f),PartPose.offset(1.5f,12f,0f));
return LayerDefinition.create(mesh,128,128).bakeRoot();}
static ModelPart tanque(){var mesh=new MeshDefinition();var root=mesh.getRoot();
root.addOrReplaceChild("torso",CubeListBuilder.create().texOffs(64,0).addBox(-6.5f,-16f,-3.0f,13f,16f,6f).texOffs(64,32).addBox(-6.6f,-2f,-3.1f,13.2f,1.3f,6.2f).texOffs(0,64).addBox(-5.5f,-16f,-3.2f,1.6f,14f,0.5f),PartPose.offset(0f,9f,0f));
root.addOrReplaceChild("head",CubeListBuilder.create().texOffs(0,0).addBox(-4.5f,-9f,-4.5f,9f,9f,9f).texOffs(64,96).addBox(-4.62f,-9f,-4.95f,9.24f,1.9f,0.24f).texOffs(0,96).addBox(-3.5f,-5.13f,-4.98f,1.5f,0.8f,0.18f).texOffs(0,96).addBox(2.0f,-5.13f,-4.98f,1.5f,0.8f,0.18f).texOffs(64,64).addBox(-1f,-1.9f,-4.96f,2.8f,0.8f,0.16f),PartPose.offset(0f,-7f,0f));
root.addOrReplaceChild("arm_l",CubeListBuilder.create().texOffs(0,0).addBox(-3.0f,0f,-3.0f,6f,15f,6f).texOffs(64,0).addBox(-3.15f,-0.4f,-3.15f,6.3f,4f,6.3f).texOffs(0,64).addBox(-3.2f,8f,-3.2f,6.4f,4f,6.4f),PartPose.offset(-9.5f,-6f,0f));
root.addOrReplaceChild("arm_r",CubeListBuilder.create().texOffs(0,0).addBox(-3.0f,0f,-3.0f,6f,15f,6f).texOffs(64,0).addBox(-3.15f,-0.4f,-3.15f,6.3f,6f,6.3f).texOffs(0,64).addBox(-3.2f,8f,-3.2f,6.4f,4f,6.4f),PartPose.offset(9.5f,-6f,0f));
root.addOrReplaceChild("leg_l",CubeListBuilder.create().texOffs(0,32).addBox(-3.15f,0f,-2f,6.3f,15f,4f).texOffs(64,32).addBox(-3.25f,12f,-2.6f,6.5f,3f,4.8f),PartPose.offset(-3.25f,9f,0f));
root.addOrReplaceChild("leg_r",CubeListBuilder.create().texOffs(0,32).addBox(-3.15f,0f,-2f,6.3f,15f,4f).texOffs(64,32).addBox(-3.25f,12f,-2.6f,6.5f,3f,4.8f),PartPose.offset(3.25f,9f,0f));
return LayerDefinition.create(mesh,128,128).bakeRoot();}
}