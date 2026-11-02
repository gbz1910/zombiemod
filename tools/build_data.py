import json
from pathlib import Path

root=Path(__file__).resolve().parents[1]/'src/main/resources'
def write(path,data):
    p=root/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(data,indent=2,ensure_ascii=False),encoding='utf8')

labels={
 'pistola':'Pistola de serviço','m4':'Carabina M4','bandagem':'Bandagem de campanha','kit':'Kit de primeiros socorros',
 'municao_9mm':'Munição de 9 mm','municao_556':'Munição de 5,56 mm','machete':'Machete de campanha',
 'sucata_metalica':'Sucata metálica','mola_arma':'Mola de arma','gatilho':'Gatilho','cano_arma':'Cano de arma',
 'corpo_pistola':'Corpo da pistola','receptor_m4':'Receptor da M4','lanterna':'Lanterna','lata_comida':'Lata de comida',
 'garrafa_agua_limpa':'Garrafa de água limpa'
}
lang={'tab.zona':'Zona Morta • Suprimentos','key.zona.reload':'Recarregar arma'}
for k,v in labels.items():lang['item.zona.'+k]=v
for key,label,egg in [('comum','Zumbi comum','zombie'),('corredor','Zumbi corredor','husk'),('tanque','Zumbi tanque','ravager')]:
    lang['entity.zona.'+key]=label;lang['item.zona.ovo_'+key]='Invocar '+label.lower()
    write('assets/zona/items/ovo_'+key+'.json',{'model':{'type':'minecraft:model','model':'minecraft:item/'+egg+'_spawn_egg'}})

lore={
 'pistola':'Semiautomática • 9 mm • recarregue com R','m4':'Automática • 5,56 mm • segure o gatilho',
 'municao_9mm':'Cartucho para a pistola de serviço.','municao_556':'Cartucho de fuzil para a M4.',
 'bandagem':'Mantenha o uso para tratar ferimentos leves.','kit':'Tratamento prolongado. Sofrer dano interrompe o uso.',
 'machete':'Lâmina larga e pesada; o golpe em arco atinge vários infectados.',
 'sucata_metalica':'Metal reaproveitável para fabricar peças de arma.',
 'mola_arma':'Peça intermediária usada para montar armas de fogo.',
 'gatilho':'Peça intermediária usada para montar armas de fogo.',
 'cano_arma':'Cano reforçado para pistola ou M4.',
 'corpo_pistola':'Estrutura principal, usada somente na pistola.',
 'receptor_m4':'Estrutura principal, usada somente na M4.',
 'lanterna':'Segure em qualquer mão para enxergar melhor no escuro.',
 'lata_comida':'Uma refeição pequena e durável.',
 'garrafa_agua_limpa':'Recupera pouca fome e devolve a garrafa vazia.'
}
lang.update({'lore.zona.'+k:v for k,v in lore.items()})
lang.update({'death.attack.zona.bala':'%1$s foi atingido por %2$s'})
for locale in ['pt_br','en_us']:write('assets/zona/lang/'+locale+'.json',lang)

def recipe(item,count,pattern,keys):
    write('data/zona/recipe/'+item+'.json',{'type':'minecraft:crafting_shaped','category':'equipment','pattern':pattern,'key':keys,'result':{'id':'zona:'+item,'count':count}})
def shapeless(item,count,ingredients):
    write('data/zona/recipe/'+item+'.json',{'type':'minecraft:crafting_shapeless','category':'equipment','ingredients':ingredients,'result':{'id':'zona:'+item,'count':count}})

recipe('bandagem',2,['SS','WW'],{'S':'minecraft:string','W':'minecraft:white_wool'})
recipe('kit',1,['ILI','BGB','ILI'],{'I':'minecraft:iron_nugget','L':'minecraft:leather','B':'zona:bandagem','G':'minecraft:golden_apple'})
recipe('municao_9mm',12,['CN','PG'],{'C':'minecraft:copper_ingot','N':'minecraft:iron_nugget','P':'minecraft:paper','G':'minecraft:gunpowder'})
recipe('municao_556',16,['IN','CG','PG'],{'I':'minecraft:iron_ingot','N':'minecraft:iron_nugget','C':'minecraft:copper_ingot','G':'minecraft:gunpowder','P':'minecraft:paper'})

# Intermediates make the two firearms a real survival progression.
recipe('sucata_metalica',3,['III'],{'I':'minecraft:iron_ingot'})
recipe('mola_arma',1,['I','I','I'],{'I':'minecraft:iron_ingot'})
shapeless('gatilho',1,[{'item':'minecraft:iron_nugget'},{'item':'zona:mola_arma'}])
recipe('cano_arma',1,['III'],{'I':'zona:sucata_metalica'})
recipe('corpo_pistola',1,['III','S S','III'],{'I':'zona:sucata_metalica','S':'minecraft:iron_ingot'})
recipe('receptor_m4',1,['III','ISI','III'],{'I':'zona:sucata_metalica','S':'minecraft:iron_ingot'})
recipe('pistola',1,['BCT',' SI',' I '],{'B':'zona:corpo_pistola','C':'zona:cano_arma','T':'zona:gatilho','S':'zona:mola_arma','I':'minecraft:iron_ingot'})
recipe('m4',1,['RCT',' S ','III'],{'R':'zona:receptor_m4','C':'zona:cano_arma','T':'zona:gatilho','S':'zona:mola_arma','I':'minecraft:iron_ingot'})
recipe('machete',1,['  I',' SI','G  '],{'I':'minecraft:iron_ingot','S':'zona:sucata_metalica','G':'minecraft:stick'})
recipe('lanterna',1,[' I ','IGI',' S '],{'I':'minecraft:iron_nugget','G':'minecraft:glass_pane','S':'minecraft:torch'})
shapeless('garrafa_agua_limpa',1,[{'item':'minecraft:glass_bottle'},{'item':'minecraft:water_bucket'}])

write('data/zona/damage_type/bala.json',{'exhaustion':.1,'message_id':'zona.bala','scaling':'never'})
for tag in ('bypasses_cooldown','is_projectile'):
    write('data/minecraft/tags/damage_type/'+tag+'.json',{'replace':False,'values':['zona:bala']})
