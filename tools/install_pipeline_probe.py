"""Install the Blender probe into an extracted Forge MDK. Not game content."""
import json
from pathlib import Path
import sys

root = Path(sys.argv[1]) / 'src/main/resources/assets/examplemod'
frames = json.loads(Path(__file__).with_name('pipeline_probe_frames.json').read_text())
models = root / 'models/item'
models.mkdir(parents=True, exist_ok=True)
(root / 'items').mkdir(parents=True, exist_ok=True)
entries = []
for frame in frames:
    name = 'probe_' + str(frame['frame'])
    model = {'textures': {'all': 'minecraft:block/copper_block', 'particle': 'minecraft:block/copper_block'}, 'elements': frame['elements']}
    (models / (name + '.json')).write_text(json.dumps(model, indent=2))
    entries.append({'threshold': frame['frame'] - 1, 'model': {'type': 'minecraft:model', 'model': 'examplemod:item/' + name}})
item = {'model': {'type': 'minecraft:range_dispatch', 'property': 'minecraft:custom_model_data', 'index': 0, 'entries': entries, 'fallback': entries[0]['model']}}
(root / 'items/example_item.json').write_text(json.dumps(item, indent=2))
print('Installed', len(frames), 'Blender-exported frames; visual validation still required.')
