"""Start an isolated production Forge server twice; verify persistent infected entities."""
import hashlib,queue,subprocess,threading,time
from pathlib import Path
from shutil import copy2
folder=Path('dedicated').resolve();folder.mkdir(exist_ok=True)
mod=next(Path('build/libs').glob('zona-*.jar'));(folder/'mods').mkdir(exist_ok=True)
copy2(mod,folder/'mods'/mod.name)
print('Validated production jar SHA-256',hashlib.sha256(mod.read_bytes()).hexdigest(),flush=True)
(folder/'eula.txt').write_text('eula=true\n')
(folder/'server.properties').write_text('server-ip=127.0.0.1\nserver-port=25577\nonline-mode=false\nspawn-protection=0\nview-distance=3\nsimulation-distance=3\ndifficulty=normal\nlevel-seed=42\n')
def test(reload):
    proc=subprocess.Popen(['bash','run.sh','nogui'],cwd=folder,stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True,bufsize=1)
    records=queue.Queue()
    def read():
        for line in proc.stdout:
            print(line,end='',flush=True);records.put(line)
        records.put(None)
    threading.Thread(target=read,daemon=True).start()
    def waitfor(text,timeout=180):
        deadline=time.monotonic()+timeout
        while time.monotonic()<deadline:
            try:line=records.get(timeout=1)
            except queue.Empty:continue
            if line is None:raise RuntimeError('Server stopped before '+text)
            if text in line:return
        raise TimeoutError(text)
    def cmd(c):proc.stdin.write(c+'\n');proc.stdin.flush()
    try:
        waitfor('Done (')
        cmd('forceload add 0 0')
        if not reload:
            for kind in ('comum','corredor','tanque'):
                cmd('summon zona:'+kind+' 4 80 4 {PersistenceRequired:1b,NoAI:1b,Invulnerable:1b,Tags:["zona_qa"]}')
                waitfor('Summoned new')
        for kind in ('comum','corredor','tanque'):
            # Retry while the spawn chunk finishes loading after restart.
            for attempt in range(10):
                cmd('execute if entity @e[type=zona:'+kind+',tag=zona_qa] run say ZONA_PRESENT_'+kind)
                try:waitfor('[Server] ZONA_PRESENT_'+kind,3);break
                except TimeoutError:pass
            else:raise RuntimeError('Missing entity '+kind)
        cmd('save-all flush');waitfor('Saved the game')
        cmd('stop');assert proc.wait(timeout=90)==0
    finally:
        if proc.poll() is None:proc.terminate();proc.wait(timeout=30)
test(False);test(True)
print('ZONA_DEDICATED_PERSISTENCE_PASS')


