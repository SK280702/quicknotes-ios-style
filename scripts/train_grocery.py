"""Train reproducible local multinomial Naive Bayes from the supplied dictionary."""
import json, re, math, unicodedata, hashlib
from collections import Counter
from pathlib import Path
source=Path('data/expanded_grocery_dictionary.json'); raw=json.loads(source.read_text())
keys=['produce','meat','deli','dairy','bakery','frozen','pantry','canned','baking','condiments','snacks','drinks','alcohol','household','personal','health','baby','pets']
def norm(s):
 s=''.join(c for c in unicodedata.normalize('NFKD',s.lower()) if not unicodedata.combining(c))
 return ' '.join(re.findall(r'[a-z0-9]+',s))
def features(s):
 t=norm(s).split(); return t+[' '.join(t[n:n+2]) for n in range(len(t)-1)]
counts=[]; vocab=set(); phrases={}; cats=[]
supplement=json.loads(Path('data/grocery-supplement.json').read_text())
for key,(title,terms) in zip(keys,raw.items()):
 terms=terms+supplement.get(key,[])
 c=Counter()
 for term in sorted(set(map(norm,terms))):
  c.update(features(term));phrases.setdefault(term,[]).append(key)
 counts.append(c);vocab.update(c);cats.append({'key':key,'title':title})
model={'version':1,'algorithm':'multinomial-naive-bayes','sourceSHA256':hashlib.sha256(source.read_bytes()).hexdigest(),'examples':sum(map(len,raw.values())),'categories':cats,'phrases':phrases,'weights':{key:{t:round(math.log((n+1)/(sum(c.values())+len(vocab))),7) for t,n in c.items()} for key,c in zip(keys,counts)},'unknown':{key:round(math.log(1/(sum(c.values())+len(vocab))),7) for key,c in zip(keys,counts)}}
code='''(function(root){'use strict';const model=MODEL;
const normalize=s=>String(s||'').normalize('NFKD').replace(/[\\u0300-\\u036f]/g,'').toLowerCase().replace(/[^a-z0-9]+/g,' ').trim();
function classify(text){const tokens=normalize(text).split(/\\s+/),features=[...tokens,...tokens.slice(1).map((t,n)=>tokens[n]+' '+t)];let longest=0,candidates=new Set();
for(let n=0;n<tokens.length;n++)for(let end=n+1;end<=Math.min(tokens.length,n+9);end++){const term=tokens.slice(n,end).join(' '),keys=model.phrases[term];if(keys){if(term.length>longest){longest=term.length;candidates=new Set(keys);}else if(term.length===longest)keys.forEach(k=>candidates.add(k));}}
// Abstain without a whole dictionary keyword; partial substrings never qualify.
if(!longest)return '';let winner='',best=-Infinity;for(const key of candidates){let score=0;for(const f of features)score+=model.weights[key][f]??model.unknown[key];if(score>best){best=score;winner=key;}}return winner;}
const api={...model,classify};if(typeof module!=='undefined')module.exports=api;root.QNGrocery=api;
})(typeof globalThis!=='undefined'?globalThis:this);
'''.replace('MODEL',json.dumps(model,separators=(',',':')))
Path('app/src/main/assets/grocery-model.js').write_text(code)
print(f'Trained {model["examples"]} phrases in {len(cats)} categories')
