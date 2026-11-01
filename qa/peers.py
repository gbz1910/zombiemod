"""Drive two real dev clients against the same localhost production Forge server."""
import os,subprocess,time,signal,shutil
from pathlib import Path
base=Path.cwd();signals=base/'signals';signals.mkdir(exist_ok=True)
env=dict(os.environ,ZONA_SIGNALS=str(signals),ALSOFT_DRIVERS='null',LIBGL_ALWAYS_SOFTWARE='1')
server=base/'dedicated';(server/'mods').mkdir(exist_ok=True)
mod=next((base/'build/libs').glob('zona-*.jar'));qa=next((base/'host/build/libs').glob('*.jar'))
for jar in [mod,qa]:shutil.copy2(jar,server/'mods')
(server/'eula.txt').write_text('eula=true\n')
(server/'server.properties').write_text('server-ip=127.0.0.1\nserver-port=25577\nonline-mode=false\nspawn-protection=0\ndifficulty=normal\nview-distance=3\nsimulation-distance=3\ngenerate-structures=false\nlevel-seed=42\n')
jobs=[]
def start(name,cmd,cwd,extra):
    dest=base/(name+'.log');f=dest.open('w')
    p=subprocess.Popen(cmd,cwd=cwd,stdin=subprocess.PIPE,stdout=f,stderr=subprocess.STDOUT,env=dict(env,**extra),start_new_session=True)
    jobs.append((p,f,dest));return p
def wait(check,timeout):
    end=time.monotonic()+timeout
    while time.monotonic()<end:
        if check():return
        for p,f,path in jobs:
            if p.poll() is not None:raise RuntimeError(str(path)+' exited '+str(p.returncode))
        time.sleep(2)
    raise TimeoutError('Test deadline')
try:
    start('dedicated',['bash','run.sh','nogui'],server,{})
    wait(lambda:'Done (' in (base/'dedicated.log').read_text(errors='replace'),180)
    for role,user in [('shooter','ZonaShooter'),('observer','ZonaObserver')]:
        host=base/role
        shutil.copytree(base/'host',host,ignore=shutil.ignore_patterns('.gradle','build','run'))
        (host/'run/mods').mkdir(parents=True)
        shutil.copy2(mod,host/'run/mods')
        (host/'run/options.txt').write_text('onboardAccessibility:false\nmaxFps:20\nrenderDistance:2\npauseOnLostFocus:false\ntutorialStep:none\n')
        with (host/'build.gradle').open('a') as f:f.write("\nminecraft.runs.named('client') { args '--username', '"+user+"' }\n")
        start(role,['xvfb-run','-a','-s','-screen 0 960x540x24','gradle','--no-daemon','--max-workers=1','runClient'],host,{'ZONA_ROLE':role})
        if role=='shooter':wait(lambda:'ZonaShooter joined the game' in (base/'dedicated.log').read_text(errors='replace'),300)
    wait(lambda:(signals/'server.ok').exists(),480)
    print('ZONA_MULTIPLAYER_COMPLETE')
finally:
    for p,f,path in reversed(jobs):
        if p.poll() is None:os.killpg(p.pid,signal.SIGTERM)
        f.close()
        print(path.name, path.read_text(errors='replace')[-3600:])


