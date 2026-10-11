"""Resolve pinned Google authorization AARs for the standalone Android build."""
import urllib.request,xml.etree.ElementTree as ET,zipfile,json,subprocess
from pathlib import Path
root=Path('build/google');root.mkdir(parents=True,exist_ok=True)
ns={'m':'http://maven.apache.org/POM/4.0.0'};seen={}
def get(path):
    f=root/Path(path).name
    if not f.exists():
        for base in ['https://dl.google.com/dl/android/maven2/','https://repo.maven.apache.org/maven2/']:
            try:
                subprocess.run(['curl','-fsSL','--retry','2','--max-time','30',base+path,'-o',str(f)],check=True,stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL);break
            except Exception: pass
        else: raise RuntimeError('Could not download '+path)
    return f

def resolve(g,a,v):
    v=v.strip('[]').split(',')[0];key=g+':'+a
    if key in seen and tuple(map(int,seen[key].split('.')))>=tuple(map(int,v.split('.'))):return
    seen[key]=v;stem=g.replace('.','/')+'/'+a+'/'+v+'/'+a+'-'+v
    pom=ET.parse(get(stem+'.pom')).getroot();pack=pom.findtext('m:packaging','jar',ns)
    f=get(stem+'.'+pack)
    if pack=='aar':
        dest=root/(a+'-'+v);dest.mkdir(exist_ok=True)
        with zipfile.ZipFile(f) as z:z.extractall(dest)
        # AAPT1 accepts ID declarations as item elements (AARs use AAPT2's id shorthand).
        for values in (dest/'res').glob('values*/*.xml'):
            tree=ET.parse(values);changed=False
            for element in tree.getroot():
                if element.tag=='id':element.tag='item';element.set('type','id');changed=True
            if changed:tree.write(values,encoding='utf-8',xml_declaration=True)
    for d in pom.findall('m:dependencies/m:dependency',ns):
        if d.findtext('m:scope','compile',ns) in ['test','provided'] or d.findtext('m:optional','false',ns)=='true':continue
        resolve(*(d.findtext('m:'+k,namespaces=ns) for k in ['groupId','artifactId','version']))
resolve('com.google.android.gms','play-services-auth','22.0.0')
(root/'resolved.json').write_text(json.dumps(seen,indent=2))
print('Google authorization dependencies: '+str(len(seen)))

jars=[];resources=[];packages=[]
for key,v in seen.items():
    a=key.split(':')[1];d=root/(a+'-'+v)
    if d.is_dir():
        jars.extend(str(p) for p in d.rglob('*.jar') if p.name=='classes.jar' or 'libs' in p.parts)
        if (d/'res').exists():resources.append(str(d/'res'))
        manifest=d/'AndroidManifest.xml'
        if manifest.exists():packages.append(ET.parse(manifest).getroot().get('package'))
    elif (root/(a+'-'+v+'.jar')).exists():jars.append(str(root/(a+'-'+v+'.jar')))
jars=[p for p in jars if not any(n in p for n in ['kotlin-stdlib-jdk7','kotlin-stdlib-jdk8'])]
(root/'jars.txt').write_text('\n'.join(jars))
(root/'resources.txt').write_text('\n'.join(resources))
(root/'packages.txt').write_text(':'.join(p for p in packages if p))
