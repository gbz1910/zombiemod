"""Author three block rigs and export their cuboids and sampled bone motion to Java."""
import bpy, math, random
from pathlib import Path
from mathutils import Vector
root=Path(__file__).resolve().parents[1]
out=root/'src/main/java/br/zona'
texdir=root/'src/main/resources/assets/zona/textures/entity'
texdir.mkdir(exist_ok=True,parents=True)
java=['package br.zona;','import net.minecraft.client.model.geom.*;','import net.minecraft.client.model.geom.builders.*;','final class InfectedArt {']
bones=['torso','head','arm_l','arm_r','leg_l','leg_r']
clips=['idle','walk','run','attack','hurt','death','windup']
for kind,(key,W,H,arm,bodyColor) in enumerate([('comum',8,12,4,'436978'),('corredor',6,11,3,'ad6a3b'),('tanque',13,16,6,'58644e')]):
    bpy.ops.object.select_all(action='SELECT');bpy.ops.object.delete(use_global=False)
    bpy.context.preferences.filepaths.save_version=0
    hip=24-(12 if kind!=2 else 15)
    pivots=[(0,hip,0),(0,hip-H,0),(-(W+arm)/2,hip-H+1,0),((W+arm)/2,hip-H+1,0),(-W/4,hip,0),(W/4,hip,0)]
    palette=['879077',bodyColor,'3e4651','353d39','a39274','76504a','b3ac81','4d5949']
    img=bpy.data.images.new(key,128,128)
    pix=[];rng=random.Random(key)
    for y in range(128):
        for x in range(128):
            c=palette[min(7,(3-y//32)*2+x//64)];shade=rng.choice([.91,.96,1,1,1.04])
            pix.extend([int(c[i:i+2],16)/255*shade for i in (0,2,4)]+[1])
    img.pixels[:]=pix;img.filepath_raw=str(texdir/(key+'.png'));img.file_format='PNG';img.save()
    ar=bpy.data.armatures.new(key);rig=bpy.data.objects.new(key+'_rig',ar);bpy.context.collection.objects.link(rig);bpy.context.view_layer.objects.active=rig;rig.select_set(True)
    bpy.ops.object.mode_set(mode='EDIT')
    for n,(x,y,z) in zip(bones,pivots):
        b=ar.edit_bones.new(n);b.head=(x,z,24-y);b.tail=(x,z,25-y)
    bpy.ops.object.mode_set(mode='OBJECT')
    pieces=[[] for _ in bones]
    def box(i,offset,size,color):
        px,py,pz=pivots[i];x,y,z=offset;a,b,c=size
        bpy.ops.mesh.primitive_cube_add(size=1,location=(px+x+a/2,pz+z+c/2,24-py-y-b/2))
        ob=bpy.context.object;ob.name=bones[i]+'_'+str(len(pieces[i]));ob.dimensions=(a,c,b)
        bpy.ops.object.transform_apply(location=False,rotation=False,scale=True)
        mat=bpy.data.materials.get(key+str(color)) or bpy.data.materials.new(key+str(color))
        col=palette[color];mat.diffuse_color=tuple(int(col[j:j+2],16)/255 for j in (0,2,4))+(1,)
        ob.data.materials.append(mat)
        world=ob.matrix_world.copy();ob.parent=rig;ob.parent_type='BONE';ob.parent_bone=bones[i];ob.matrix_world=world
        pieces[i].append((offset,size,color))
    thick=6 if kind==2 else 4
    box(0,(-W/2,-H,-thick/2),(W,H,thick),1)
    box(0,(-W/2-.1,-2,-thick/2-.1),(W+.2,1.3,thick+.2),3)
    box(0,(-W/2+1,-H,-thick/2-.2),(1.6,H-2,.5),4)
    head=9 if kind==2 else 7 if kind==1 else 8
    box(1,(-head/2,-head,-head/2),(head,head,head),0)
    box(1,(-head/2,-head,-head/2-.1),(head,1.9,head+.2),7)
    for x in [-head/2+1,head/2-2.5]:box(1,(x,-head*.57,-head/2-.2),(1.5,.8,.35),6)
    box(1,(-1,-1.9,-head/2-.15),(2.8,.8,.2),5)
    for i in (2,3):
        length=15 if kind==2 else 12
        box(i,(-arm/2,0,-arm/2),(arm,length,arm),0)
        box(i,(-arm/2-.15,-.4,-arm/2-.15),(arm+.3,4 if i==2 else 6,arm+.3),1)
        if kind==2:box(i,(-arm/2-.2,8,-arm/2-.2),(arm+.4,4,arm+.4),4)
    for i in (4,5):
        width=W/2-.2
        box(i,(-width/2,0,-2),(width,24-hip,4),2)
        box(i,(-width/2-.1,24-hip-3,-2.6),(width+.2,3,4.8),3)
    java.append('static ModelPart '+key+'(){var mesh=new MeshDefinition();var root=mesh.getRoot();')
    def f(v):return str(round(v,4))+'f'
    for n,p,group in zip(bones,pivots,pieces):
        chain='CubeListBuilder.create()'
        for pos,size,color in group:chain+='.texOffs('+str(color%2*64)+','+str(color//2*32)+').addBox('+','.join(map(f,(*pos,*size)))+')'
        java.append('root.addOrReplaceChild("'+n+'",'+chain+',PartPose.offset('+','.join(map(f,p))+'));')
    java.append('return LayerDefinition.create(mesh,128,128).bakeRoot();}')
    samples=[]
    for ci,clip in enumerate(clips):
        start=ci*40+1;bpy.context.scene.timeline_markers.new(clip,frame=start)
        section=[]
        for j in range(5):
            phase=j*math.pi/2;s=math.sin(phase);bend=[.13,.38,.09][kind]
            pitch=[bend,-.1,-.45,-.55,0,0];roll=[0,-.06,-.08,.1,0,0]
            if clip=='idle':pitch[1]+=.055*s;pitch[2]+=.065*s
            if clip in ('walk','run'):
                amp=.95 if clip=='run' else [.52,.69,.38][kind]
                pitch[2]+=.55*s;pitch[3]-=.55*s;pitch[4]+=amp*s;pitch[5]-=amp*s;roll[0]+=.055*s
            if clip=='attack':pitch[2]-=[0,.7,1.6,.6,0][j];pitch[3]-=[0,.5,1.45,.4,0][j]
            if clip=='hurt':pitch[0]-=[0,.33,.2,.09,0][j];roll[1]+=.16*s
            if clip=='death':pitch[0]+=j*.22;pitch[1]+=j*.13;pitch[2]+=j*.17;pitch[3]+=j*.2
            if clip=='windup':pitch[2]-=j*.46;pitch[3]-=j*.41;pitch[0]-=j*.065
            frame=start+j*5
            for i,n in enumerate(bones):
                pb=rig.pose.bones[n];pb.rotation_mode='XYZ';pb.rotation_euler=(pitch[i],roll[i],0)
                pb.keyframe_insert(data_path='rotation_euler',frame=frame)
            bpy.context.scene.frame_set(frame)
            section.append([v for n in bones for v in (rig.pose.bones[n].rotation_euler.x,rig.pose.bones[n].rotation_euler.y)])
        samples.append(section)
    java.append('static final float[][][] '+key.upper()+'={'+','.join('{'+','.join('{'+','.join(map(f,row))+'}' for row in sec)+'}' for sec in samples)+'};')
    bpy.context.scene.frame_start=1;bpy.context.scene.frame_end=261;bpy.context.scene.frame_set(1)
    bpy.ops.wm.save_as_mainfile(filepath=str(root/'art/blender'/(key+'.blend')),compress=True)
java.append('}')
(out/'InfectedArt.java').write_text('\n'.join(java),encoding='utf8')

