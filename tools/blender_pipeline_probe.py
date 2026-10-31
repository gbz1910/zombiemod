import bpy, json, os
from mathutils import Vector
root=os.path.dirname(os.path.abspath(__file__))
bpy.ops.object.select_all(action='SELECT'); bpy.ops.object.delete(use_global=False)
bpy.ops.mesh.primitive_cube_add(size=1, location=(8,8,8))
obj=bpy.context.object; obj.name='validation_slide'; obj.dimensions=(4,4,10)
bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
for frame,z in [(1,8),(4,6),(9,8)]:
 obj.location.z=z; obj.keyframe_insert(data_path='location',frame=frame)
bpy.context.scene.frame_end=9
frames=[]
for frame in range(1,10):
 bpy.context.scene.frame_set(frame)
 corners=[obj.matrix_world @ Vector(c) for c in obj.bound_box]
 low=[min(v[i] for v in corners) for i in range(3)]
 high=[max(v[i] for v in corners) for i in range(3)]
 frames.append({'frame':frame,'elements':[{'from':low,'to':high,'faces':{side:{'texture':'#all'} for side in ['north','south','east','west','up','down']}}]})
bpy.ops.wm.save_as_mainfile(filepath=os.path.join(root,'pipeline_probe.blend'))
with open(os.path.join(root,'pipeline_probe_frames.json'),'w') as f: json.dump(frames,f,indent=2)
print('PROBE_EXPORTED',len(frames))

