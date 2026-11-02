"""Generate the editable Blender scenes and their Minecraft resources. Run with blender -b -P."""
import bpy, math, json, random
from pathlib import Path
from mathutils import Vector
root=Path(__file__).resolve().parents[1]
assets=root/'src/main/resources/assets/zona'
studio=root/'art/blender'
studio.mkdir(parents=True,exist_ok=True)
def write(path, data):
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(json.dumps(data,ensure_ascii=False,indent=2),encoding='utf8')
bpy.context.preferences.filepaths.save_version=0
palette={'steel':'38434b','wear':'707575','black':'22282b','grip':'424344','bronze':'ae8044','brass':'d7af62','cloth':'d1c7ac','red':'a44637','kit':'526255','label':'e8d6b0','blue':'3f6473','orange':'cb7739','skin':'879276','bruise':'6b775e','pants':'3d454c','strap':'64543f'}
mats={}
for name,h in palette.items():
    col=tuple(int(h[i:i+2],16)/255 for i in (0,2,4))
    m=bpy.data.materials.new(name);m.diffuse_color=(*col,1);m.use_nodes=True
    next(n for n in m.node_tree.nodes if n.type=='BSDF_PRINCIPLED').inputs['Base Color'].default_value=(*col,1)
    mats[name]=m
    img=bpy.data.images.new(name,16,16,alpha=True)
    rng=random.Random(name); pixels=[]
    for row in range(16):
        for x in range(16):
            k=rng.choice([0.94,1,1,1.04,1.07]) + (0.08 if row in [2,11] and x in [6,7,8] else 0)
            pixels.extend([min(1,c*k) for c in col]+[1])
    img.pixels[:]=pixels
    out=assets/'textures/item'/f'{name}.png';out.parent.mkdir(parents=True,exist_ok=True)
    img.filepath_raw=str(out);img.file_format='PNG';img.save()
def clear():
    bpy.ops.object.select_all(action='SELECT');bpy.ops.object.delete(use_global=False)
    bpy.context.scene.frame_set(1)
def box(name,lo,hi,material):
    # Minecraft X,Y,Z to Blender X,-Z,Y.
    x,y,z=[(a+b)/2 for a,b in zip(lo,hi)]
    bpy.ops.mesh.primitive_cube_add(size=1,location=(x,-z,y))
    ob=bpy.context.object;ob.name=name;ob.dimensions=(hi[0]-lo[0],hi[2]-lo[2],hi[1]-lo[1])
    bpy.ops.object.transform_apply(location=False,rotation=False,scale=True)
    ob.data.materials.append(mats[material]);ob['texture']=material
    return ob
def export_item(name,parts,gun=False,sequence=None):
    clear();objs=[]
    for n,lo,hi,mat in parts: objs.append(box(n,lo,hi,mat))
    frames=sequence or [1]
    animated=gun or sequence is not None
    if gun:
        scene=bpy.context.scene
        # Idle, draw, recoil and magazine extraction poses; marker names are editable in the timeline.
        frames=[1,5,9,13,17,21,25,29,33,37]
        for f,label in zip(frames,['idle','equip','ready','fire','recover','unlock','magazine out','insert','seat','breathe']):
            scene.timeline_markers.new(label,frame=f)
        for ob in objs:
            start=ob.location.copy()
            for f in frames:
                ob.location=start.copy()
                if f==5: ob.location.z-=2.5
                if f==37: ob.location.z+=.12
                if f==13: ob.location.y-=1.4 if ob.name.startswith('slide') else .55
                if ob.name.startswith('mag'):
                    ob.location.z-= {21:1.5,25:5,29:2}.get(f,0)
                ob.keyframe_insert(data_path='location',frame=f)
        scene.frame_end=37
    elif sequence is not None:
        scene=bpy.context.scene
        for f,label in zip(frames,['rest','equip','attack','impact']):scene.timeline_markers.new(label,frame=f)
        for ob in objs:
            start=ob.location.copy()
            for index,f in enumerate(frames):
                ob.location=start.copy();ob.rotation_euler=(0,0,0)
                if index==1:ob.rotation_euler[1]=math.radians(-18)
                elif index==2:ob.rotation_euler[1]=math.radians(62);ob.location.z+=1.5
                elif index==3:ob.rotation_euler[1]=math.radians(35);ob.location.z+=.6
                ob.keyframe_insert(data_path='location',frame=f);ob.keyframe_insert(data_path='rotation_euler',frame=f)
        scene.frame_end=frames[-1]
    entries=[]
    for i,f in enumerate(frames):
        bpy.context.scene.frame_set(f); elements=[]
        for ob in objs:
            corners=[ob.matrix_world @ Vector(c) for c in ob.bound_box]
            xyz=[(v.x,v.z,-v.y) for v in corners]
            elements.append({'from':[round(min(v[a] for v in xyz),3) for a in range(3)],'to':[round(max(v[a] for v in xyz),3) for a in range(3)],'faces':{side:{'texture':'#'+ob['texture']} for side in ('up','down','north','south','west','east')}})
        tex={ob['texture']:'zona:item/'+ob['texture'] for ob in objs};tex['particle']=next(iter(tex.values()))
        display={'gui':{'rotation':[15,110,-12],'scale':[.75]*3},'ground':{'scale':[.45]*3,'translation':[0,3,0]},'fixed':{'rotation':[0,90,0]},'thirdperson_righthand':{'rotation':[0,0,0],'translation':[0,2,0],'scale':[.85]*3},'firstperson_righthand':{'rotation':[0,0,0],'translation':[0,2,4],'scale':[.75]*3}}
        for view in ('firstperson','thirdperson'): display[view+'_lefthand']=display[view+'_righthand']
        model=name+('_'+str(i) if animated else '')
        write(assets/'models/item'/f'{model}.json',{'textures':tex,'elements':elements,'display':display})
        entries.append({'threshold':i,'model':{'type':'minecraft:model','model':'zona:item/'+model}})
    if gun:
        import copy
        for i in range(10):
            regular=assets/'models/item'/f'{name}_{i}.json'
            aimed=json.loads(regular.read_text())
            for view in ('firstperson_righthand','firstperson_lefthand'):
                aimed['display'][view]['translation']=[-7.8,5.2,3.0]
                aimed['display'][view]['scale']=[.72,.72,.72]
            variant=f'{name}_aim_{i}'
            write(assets/'models/item'/f'{variant}.json',aimed)
            entries.append({'threshold':10+i,'model':{'type':'minecraft:model','model':'zona:item/'+variant}})
    write(assets/'items'/f'{name}.json',{'model':{'type':'minecraft:range_dispatch','property':'minecraft:custom_model_data','index':0,'entries':entries,'fallback':entries[0]['model']} if animated else entries[0]['model']})
    bpy.context.scene.frame_set(1);bpy.ops.wm.save_as_mainfile(filepath=str(studio/(name+'.blend')),compress=True)
P=[]
def part(n,x,y,z,X,Y,Z,m):P.append((n,(x,y,z),(X,Y,Z),m))
part('frame',6.8,8,1,9.2,10.2,14,'steel');part('slide',6.7,10,0,9.3,12.4,14.3,'wear');part('muzzle',7.4,10.4,-.1,8.6,11.6,.1,'black')
part('grip',6.6,2,10,9.4,8.5,14,'grip');part('magazine',6.5,1.4,9.9,9.5,2.2,14.2,'black')
part('guard',7.2,5,5.5,8.8,5.7,10.3,'steel');part('guard front',7.2,5,5.5,8.8,8,6.2,'steel');part('trigger',7.7,6.4,8.4,8.3,8,9,'black')
part('sight',7.7,12.4,.8,8.3,13,1.5,'brass')
for z in (10,11,12,13):part('slide serration',6.55,10.3,z,9.45,11.7,z+.25,'steel')
export_item('pistola',P,True);P.clear()
part('upper',6.8,9,3,9.2,11.5,14,'steel');part('lower',6.9,7.5,6,9.1,9.5,14,'grip');part('barrel',7.45,9.5,-8,8.55,10.7,4,'wear');part('flash hider',7.2,9.3,-9,8.8,10.9,-7,'black')
part('handguard',6.6,8.9,-4,9.4,11.4,3,'grip')
for z in (-3,-1,1):part('guard ribs',6.5,9,z,9.5,11.6,z+.45,'black')
part('buffer',7.3,9,14,8.7,10.7,21,'wear');part('stock',6.8,8.1,18,9.2,11.4,24,'grip');part('butt',6.6,5.9,23,9.4,11.6,24.4,'black')
part('grip',7,2.7,11,9,7.6,13.6,'grip');part('mag body',6.85,2,6.1,9.15,8.2,9.6,'wear');part('mag heel',6.7,1.5,5.6,9.3,2.7,9.6,'black');part('mag stripe',6.75,4,6,9.25,4.7,9.7,'orange')
part('front sight',7.65,11,-4,8.35,13,-3,'steel');part('rear sight',7.25,11.5,12,8.75,12.7,13,'black')
export_item('m4',P,True);P.clear()
part('case',2,3,4,14,11,12,'kit');part('lid',1.8,10,3.9,14.2,11.5,12.1,'kit');part('handle',5,12.4,7,11,13.3,9,'black')
for x in (5,10):part('handle post',x,11,7,x+1,12.6,9,'black')
part('patch',5,4.6,3.8,11,9.3,4.1,'cloth');part('cross upright',7.4,5.1,3.65,8.6,8.8,3.85,'red');part('cross horizontal',6.2,6.3,3.6,9.8,7.5,3.85,'red')
export_item('kit',P);P.clear()
part('roll',3,4,4,13,10,12,'cloth');part('roll top',4,10,4,12,11,12,'label');part('fold',4,3,4,12,4,12,'cloth');part('wrapper',6.5,3.8,3.8,9.5,10.3,12.2,'strap');part('tail',4,3,1.8,8,3.8,5,'label')
export_item('bandagem',P);P.clear()
for item,hue,height in [('municao_9mm','blue',3.2),('municao_556','orange',5.2)]:
    part('packet',2,3,4,14,7,12,'strap');part('label stripe',2,4,3.8,14,5.5,4.1,hue)
    for x in (4,7,10):
        part('case',x,7,6,x+1.8,7+height,8,'brass');part('bullet',x+.25,7+height,6.25,x+1.55,8.4+height,7.75,'bronze')
    export_item(item,P);P.clear()

# Machete with a chipped steel edge and wrapped grip. Four Blender-authored poses
# are exported as item model frames and selected by the server-synced model data.
part('wrapped handle',6.7,1,10.5,9.3,3.2,15,'grip')
for z in (11.2,12.1,13.0,13.9):part('wrap band',6.55,1.25,z,9.45,2.8,z+.38,'strap')
part('blade tang',7.2,2.9,14,8.8,4.2,20.3,'steel')
part('blade spine',6.9,3.8,18.3,9.1,5.1,26.6,'wear')
part('worn cutting edge',6.75,3.2,19.3,7.1,4.1,26.4,'steel')
part('tip',7.1,3.7,26.1,8.9,4.8,28,'wear')
part('guard',6.1,2.8,13.7,9.9,4.4,14.6,'black')
export_item('machete',P,sequence=[1,5,9,13]);P.clear()

# Crafting components, survival supplies, and their distinct readable icons.
part('scrap plate',3,3,5,13,6,11,'steel');part('bright scratch',4,6,4.8,11,7,5.2,'wear');part('folded edge',3,6,10,13,8,11.4,'black')
export_item('sucata_metalica',P);P.clear()
for z in (4.5,6.5,8.5,10.5):
    part('spring coil',5,6.5,z,11,9.5,z+1.2,'bronze')
export_item('mola_arma',P);P.clear()
part('trigger body',6,4,6,10,8,12,'steel');part('finger loop',7,3.7,5.2,9,4.5,9,'black');part('trigger tip',7.3,2.9,5.1,8.7,4.1,6,'wear')
export_item('gatilho',P);P.clear()
part('barrel tube',5,6,3,11,10,13,'steel');part('bore',6.4,9.9,2.7,9.6,10.2,5,'black');part('muzzle ring',4.7,6.1,3,11.3,10.1,4.2,'wear')
export_item('cano_arma',P);P.clear()
part('pistol frame',5,3,4,11,9,13,'steel');part('grip scales',5.2,2.8,10,10.8,4.8,14,'grip');part('slide rails',4.5,9,6,11.5,10.2,13,'wear')
export_item('corpo_pistola',P);P.clear()
part('receiver upper',4,8,3,12,11,15,'steel');part('receiver lower',5,5,6,11,8.2,15,'grip');part('rail',4,11,4,12,12,15,'wear')
export_item('receptor_m4',P);P.clear()
part('lantern body',5,4,5,11,11,12,'black');part('glass',5.8,7,4.6,10.2,10.2,5.2,'label');part('cage',4.4,6.5,4.2,5.5,10.8,5.4,'steel');part('cap',4,10.8,4.4,12,12,12.6,'wear');part('handle',6.5,2,4,9.5,3.2,12,'grip')
export_item('lanterna',P);P.clear()
part('tin',4,3,4,12,10,12,'steel');part('lid',3.8,10,3.9,12.2,10.6,12.1,'wear');part('label',5,5,3.8,11,8.5,4.1,'orange');part('label text',6,6,3.6,10,7,3.9,'label')
export_item('lata_comida',P);P.clear()
part('bottle glass',5,2,4,11,10,12,'blue');part('neck',6,10,5,10,12.8,11,'blue');part('cap',6,12.8,5,10,14,11,'steel');part('water highlight',5.3,4,3.8,6,9,4,'label')
export_item('garrafa_agua_limpa',P);P.clear()


