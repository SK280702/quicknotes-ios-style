import os,subprocess,shutil
from pathlib import Path
sdk=Path(os.environ.get('ANDROID_SDK_ROOT',os.environ.get('ANDROID_HOME','.android-sdk')));bt=sdk/'build-tools/35.0.0';jar=sdk/'platforms/android-35/android.jar'
def run(args):subprocess.run([str(x) for x in args],check=True)
for folder in ['build/classes','build/generated','build/dex']:
    shutil.rmtree(folder,ignore_errors=True);Path(folder).mkdir(parents=True)
run(['python3','scripts/google_deps.py']);base=Path('build/google');jars=base.joinpath('jars.txt').read_text().splitlines();resources=base.joinpath('resources.txt').read_text().splitlines();packages=base.joinpath('packages.txt').read_text()
cmd=[bt/'aapt','package','-f','-m','--auto-add-overlay','--extra-packages',packages,'-0','wav','-M','app/src/main/AndroidManifest.xml','-I',jar,'-A','app/src/main/assets','-S','app/src/main/res']
for res in resources:cmd+=['-S',res]
cmd+=['-J','build/generated','-F','build/unsigned.apk'];run(cmd)
source=list(Path('app/src/main/java').rglob('*.java'))+list(Path('build/generated').rglob('*.java'))
run(['java','com.sun.tools.javac.Main','-source','8','-target','8','-bootclasspath',str(jar)+':'+str(bt/'core-lambda-stubs.jar'),'-classpath',':'.join(jars),'-d','build/classes',*source])
classes=list(Path('build/classes').rglob('*.class'));run([bt/'d8','--lib',jar,'--min-api','26','--output','build/dex',*classes,*jars])
