import json
from pathlib import Path
root=Path(__file__).resolve().parents[1]/'src/main/resources'
def write(path,data):
    p=root/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(data,indent=2,ensure_ascii=False),encoding='utf8')
labels={'pistola':'Pistola de serviço','m4':'Carabina M4','bandagem':'Bandagem de campanha','kit':'Kit de primeiros socorros','municao_9mm':'Munição de 9 mm','municao_556':'Munição de 5,56 mm'}
lang={'tab.zona':'Zona Morta • Suprimentos','key.zona.reload':'Recarregar arma'}
for k,v in labels.items():lang['item.zona.'+k]=v
for key,label,egg in [('comum','Zumbi comum','zombie'),('corredor','Zumbi corredor','husk'),('tanque','Zumbi tanque','ravager')]:
    lang['entity.zona.'+key]=label;lang['item.zona.ovo_'+key]='Invocar '+label.lower()
    write('assets/zona/items/ovo_'+key+'.json',{'model':{'type':'minecraft:model','model':'minecraft:item/'+egg+'_spawn_egg'}})
for locale in ['pt_br','en_us']:write('assets/zona/lang/'+locale+'.json',lang)
def recipe(item,count,pattern,keys):
    write('data/zona/recipe/'+item+'.json',{'type':'minecraft:crafting_shaped','category':'equipment','pattern':pattern,'key':keys,'result':{'id':'zona:'+item,'count':count}})
recipe('bandagem',2,['SS','WW'],{'S':'minecraft:string','W':'minecraft:white_wool'})
recipe('kit',1,['ILI','BGB','ILI'],{'I':'minecraft:iron_nugget','L':'minecraft:leather','B':'zona:bandagem','G':'minecraft:golden_apple'})
recipe('municao_9mm',12,['CN','PG'],{'C':'minecraft:copper_ingot','N':'minecraft:iron_nugget','P':'minecraft:paper','G':'minecraft:gunpowder'})
recipe('municao_556',16,['IN','CG','PG'],{'I':'minecraft:iron_ingot','N':'minecraft:iron_nugget','C':'minecraft:copper_ingot','G':'minecraft:gunpowder','P':'minecraft:paper'})
recipe('pistola',1,['III',' LR','  L'],{'I':'minecraft:iron_ingot','L':'minecraft:leather','R':'minecraft:redstone'})
recipe('m4',1,['IBI','RPI','L L'],{'I':'minecraft:iron_ingot','B':'minecraft:iron_block','R':'minecraft:redstone','P':'minecraft:piston','L':'minecraft:leather'})

