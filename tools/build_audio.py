"""Synthesize original mono Ogg effects with Blender's audio library."""
import aud, wave, array, random, math, json, tempfile
from pathlib import Path
assets=Path(__file__).resolve().parents[1]/'src/main/resources/assets/zona'
dest=assets/'sounds';dest.mkdir(exist_ok=True,parents=True)
events={}
for index,name in enumerate(('pistol_fire','rifle_fire','mag_out','mag_in','dry','common_call','runner_call','tank_call')):
    rng=random.Random(index);rate=24000
    length=(.30,.25,.27,.24,.11,.74,.48,1.08)[index]
    values=array.array('h');last=0
    for n in range(int(rate*length)):
        t=n/rate;noise=rng.uniform(-1,1);last=.82*last+.18*noise
        if index<2:
            value=.75*noise*math.exp(-t*(64+index*18))+.6*math.sin(2*math.pi*(95+35*index)*t+1.7*(1-math.exp(-40*t)))*math.exp(-t*22)+last*math.exp(-t*12)
        elif index<5:
            value=0
            for beat in ([0,.13] if index==2 else [0,.06,.14] if index==3 else [0]):
                if t>=beat:value+=(.72*noise+.18*math.sin(1800*t))*math.exp(-(t-beat)*120)
        else:
            hz=[91,156,52][index-5];envelope=math.sin(math.pi*t/length)**1.5
            ph=2*math.pi*hz*t+1.5*math.sin(19*t);value=envelope*(.27*math.sin(ph)+.22*math.sin(2*ph)+.58*last)*(1+.22*math.sin(35*t))
        values.append(int(max(-.96,min(.96,value))*32767))
    temp=Path(__file__).resolve().parents[1]/'art/audio-source'
    temp.mkdir(parents=True,exist_ok=True)
    if temp.is_dir():
        wav=temp/(name+'.wav')
        with wave.open(str(wav),'wb') as f:f.setparams((1,2,rate,0,'NONE',''));f.writeframes(values.tobytes())
        aud.Sound(str(wav)).write(str(dest/(name+'.ogg')),rate,aud.CHANNELS_MONO,aud.FORMAT_S16,aud.CONTAINER_OGG,aud.CODEC_VORBIS,80000,2048)
    events[name]={'sounds':['zona:'+name]}
(assets/'sounds.json').write_text(json.dumps(events,indent=2))


