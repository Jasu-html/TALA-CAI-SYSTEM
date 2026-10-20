const $=s=>document.querySelector(s);
const TX={en:{play:"Play",gr:"Teacher / Parent",who:"Who is playing?",hi:"Hi, ",ready:"I’m ready!",watch:"Watch how to play",replay:"Replay",lesson:"Lesson",practice:"Practice",again:"Try again!",great:["Great job!","Yay!","You did it!","Wonderful!"],next:"Next level",playagain:"Play again",keep:"Keep playing to earn more!",locked:"Finish the level before this one first!",qlock:"Learn more with my friends first!",hold:"Hold for 3 seconds",pin:"Enter PIN (demo: 1234)",buy:"Buy for ",sound:"Which picture starts with this sound?",count:"How many?",tapq:"Tap the ",what:"What should you do?",earned:"You earned stars!",hint:"Here’s a hint!",lang:"Filipino",tl:"Take a look at the picture. I’ll read it with you.",wal:"Star Wallet"},
fil:{play:"Laro",gr:"Guro / Magulang",who:"Sino ang maglalaro?",hi:"Kumusta, ",ready:"Handa na ako!",watch:"Tingnan kung paano",replay:"Ulitin",lesson:"Aralin",practice:"Pagsasanay",again:"Subukan ulit!",great:["Magaling!","Yehey!","Nagawa mo!","Ang galing!"],next:"Susunod",playagain:"Ulitin ang laro",keep:"Magpatuloy sa paglalaro para makakuha ng higit pa!",locked:"Tapusin muna ang naunang antas!",qlock:"Mag-aral muna kasama ang mga kaibigan ko!",hold:"Pindutin nang 3 segundo",pin:"Ilagay ang PIN (demo: 1234)",buy:"Bilhin sa ",sound:"Aling larawan ang nagsisimula sa tunog na ito?",count:"Ilan ang mga ito?",tapq:"Pindutin ang ",what:"Ano ang dapat mong gawin?",earned:"Nakakuha ka ng mga bituin!",hint:"May pahiwatig ako!",lang:"English",tl:"Tingnan ang larawan. Babasahin ko ito kasama mo.",wal:"Bulsa ng Bituin"}};

const t=k=>(TX[S.lang][k]??TX.en[k]);

const MODS=[
{id:"PHONICS",n:20,g:"titik",ic:"📖",nm:"Phonics",w:"Story Library",col:"var(--p)"},
{id:"NUMBERS",n:15,g:"bilang",ic:"🔢",nm:"Numbers",w:"Number Market",col:"var(--s)"},
{id:"ARTS",n:15,g:"likha",ic:"🎨",nm:"Shapes & Colors",w:"Art Studio",col:"var(--c)"},
{id:"VALUES",n:10,g:"tala",ic:"💛",nm:"Good Values",w:"Starlight Hill",col:"var(--y)"},
{id:"QUEST",n:10,g:"tala",ic:"🏆",nm:"Quest Quiz",w:"Starlight Hill",col:"var(--m)"}
];

const AVS=[["Mia","👧"],["Leo","👦"],["Ayla","👩🏽"],["Jun","🧒"],["Bea","👧🏽"]];
const SHOP=[["👑",20],["🎀",10],["🕶️",15],["🎩",25],["🌸",5],["⭐",30]];
const BADGES=[["Letter Explorer","📖",s=>done(s,0)],["Number Hero","🔢",s=>done(s,1)],["Little Artist","🎨",s=>done(s,2)],["Values Champion","💛",s=>done(s,3)],["Quest Master","🏆",s=>done(s,4)],["Star Collector","⭐",s=>s.life>=25]];
const done=(s,i)=>Object.values(s.prog[MODS[i].id]||{}).filter(v=>v>0).length>=MODS[i].n;

// ---- state
let S={scr:"splash",lang:"en",cur:null,mod:0,tab:"path",stu:{},logs:[],theme:"light"};

try{
  const x=JSON.parse(localStorage.getItem("tala")||"null");
  if(x){
    S=Object.assign(S,x);
    S.scr="splash";
  }
}catch(e){}

S.theme=S.theme==="dark"?"dark":"light";
document.documentElement.dataset.theme=S.theme;

const save=()=>{
  try{
    localStorage.setItem("tala",JSON.stringify({
      lang:S.lang,
      stu:S.stu,
      logs:S.logs,
      theme:S.theme
    }));
  }catch(e){}
};

function toggleTheme(){
  S.theme=document.documentElement.dataset.theme==="dark"?"light":"dark";
  document.documentElement.dataset.theme=S.theme;
  save();
}

function stu(n){
  return S.stu[n]||(S.stu[n]={prog:{},wallet:0,life:0,owned:[],eq:null});
}

const me=()=>stu(S.cur);

function seed(){
  if(S.logs.length)return;
  const r=(a)=>a;
  ["Leo","Ayla","Jun","Bea"].forEach((n,i)=>{
    const s=stu(n);
    MODS.slice(0,3).forEach((m,j)=>{
      s.prog[m.id]={};
      for(let l=1;l<=(i+j)%4+2;l++)s.prog[m.id][l]=3+((i+l)%3);
      for(let k=0;k<8;k++)S.logs.push({
        s:n,
        m:m.id,
        k:"item"+k,
        ft:((i*3+j*5+k)%10)<(4+i*2+(j==0?-2:1))?1:0,
        d:Date.now()-k*864e5/2
      });
    });
    s.life=Object.values(s.prog).reduce((a,p)=>a+Object.values(p).reduce((x,y)=>x+y,0),0);
    s.wallet=s.life;
  });
}

// ---- audio
let AC;

const tone=(f,d=.15,ty="sine",dl=0)=>{
  try{
    AC=AC||new AudioContext();
    const o=AC.createOscillator(),g=AC.createGain();
    o.type=ty;
    o.frequency.value=f;
    g.gain.setValueAtTime(.12,AC.currentTime+dl);
    g.gain.exponentialRampToValueAtTime(.001,AC.currentTime+dl+d);
    o.connect(g).connect(AC.destination);
    o.start(AC.currentTime+dl);
    o.stop(AC.currentTime+dl+d);
  }catch(e){}
};

const sfx={
  tap:()=>tone(520,.08),
  ok:()=>{tone(660,.15);tone(880,.2,"sine",.12)},
  soft:()=>{tone(330,.25,"triangle");tone(294,.3,"triangle",.15)},
  star:()=>{tone(880,.2);tone(1175,.3,"sine",.1)},
  fan:()=>[523,659,784,1047].forEach((f,i)=>tone(f,.25,"triangle",i*.12))
};

const PIT={tala:1.3,likha:1.1,bilang:.8,titik:1.55};

function say(txt,ch="tala"){
  const sb=$("#sub");
  sb.textContent=txt;
  sb.style.display="block";
  try{
    speechSynthesis.cancel();
    const u=new SpeechSynthesisUtterance(txt);
    u.lang=S.lang=="fil"?"fil-PH":"en-US";
    u.rate=.85;
    u.pitch=PIT[ch]||1;
    u.onstart=()=>document.querySelectorAll(".guide svg").forEach(e=>e.classList.add("talk"));
    u.onend=()=>document.querySelectorAll(".guide svg").forEach(e=>e.classList.remove("talk"));
    speechSynthesis.speak(u);
  }catch(e){}
  clearTimeout(say.t);
  say.t=setTimeout(()=>sb.style.display="none",4500);
}

// ---- characters
function ch(id,st="wave",cls=""){
  const C={
    tala:["#FFC93C","⭐"],
    likha:["#FF7A6B","🎨"],
    bilang:["#4DB8FF","🎒"],
    titik:["#8E7CFF","📖"]
  }[id];

  const body=id=="tala"
    ?`<polygon points="50,6 61,36 93,38 68,58 77,90 50,72 23,90 32,58 7,38 39,36" fill="${C[0]}" stroke="#d49a10" stroke-width="3"/>`
    :`<circle cx="50" cy="55" r="38" fill="${C[0]}" stroke="#0003" stroke-width="3"/>`;

  const eyes=st=="sleepy"
    ?`<path d="M34 50h10M56 50h10" stroke="#2B3A67" stroke-width="4" stroke-linecap="round"/>`
    :`<circle cx="39" cy="50" r="5" fill="#2B3A67"/><circle cx="61" cy="50" r="5" fill="#2B3A67"/>`;

  return`<svg class="ch ${st} ${cls}" viewBox="0 0 100 100" role="img" aria-label="${id} (${st})"><g class="b">${body}${eyes}<ellipse class="mo" cx="50" cy="62" rx="7" ry="${st=="cheer"?6:3}" fill="#2B3A67"/><circle cx="30" cy="60" r="4" fill="#ff9aa2" opacity=".7"/><circle cx="70" cy="60" r="4" fill="#ff9aa2" opacity=".7"/><text x="68" y="95" font-size="26">${C[1]}</text>${st=="sleepy"?'<text x="72" y="22" font-size="18">💤</text>':""}${st=="think"?'<text x="72" y="22" font-size="20">💭</text>':""}</g></svg>`;
}

const avHTML=(n,e)=>{
  const s=me();
  return`<div class="av" aria-label="${n}">${e||"🙂"}${s.eq?`<small>${s.eq}</small>`:""}</div>`;
};

const myAv=()=>(AVS.find(a=>a[0]==S.cur)||["","🙂"])[1];

// ---- content
const LET=[
["A","🍎","ah"],["B","⚽","buh"],["C","🐱","kuh"],["D","🐶","duh"],
["E","🥚","eh"],["F","🐟","fuh"],["H","🎩","huh"],["I","🍦","ih"],
["L","🦁","luh"],["M","🌙","mm"],["O","🍊","oh"],["P","🐷","puh"],
["S","☀️","sss"],["T","🐯","tuh"],["U","☂️","uh"],["W","🍉","wuh"],["Z","🦓","zzz"]
];

const SH={
  circle:'<circle cx="30" cy="30" r="26"/>',
  square:'<rect x="6" y="6" width="48" height="48"/>',
  triangle:'<polygon points="30,4 56,54 4,54"/>',
  rectangle:'<rect x="2" y="14" width="56" height="32"/>',
  star:'<polygon points="30,3 38,22 58,23 42,36 48,56 30,44 12,56 18,36 2,23 22,22"/>',
  heart:'<path d="M30 54C6 36 4 14 18 10c8-2 12 4 12 8 0-4 4-10 12-8 14 4 12 26-12 44z"/>'
};

const COL={
  red:"#E53935",blue:"#1E88E5",yellow:"#FDD835",green:"#43A047",
  orange:"#FB8C00",purple:"#8E24AA",pink:"#F48FB1",brown:"#795548"
};

const SC=[
["🤝","You meet your lola.",["Say “mano po”","Walk away 🙈"]],
["🖍️","Your friend has no crayons.",["Share your crayons","Keep them all"]],
["🥤","You spilled juice.",["Say sorry and wipe it","Run away"]],
["🍽️","It’s time to eat.",["Wash your hands","Just start eating"]],
["🛝","Line for the slide.",["Wait your turn","Push ahead"]],
["🎁","Someone helped you.",["Say “salamat”","Say nothing"]]
];

const sh=a=>[...a].sort(()=>Math.random()-.5);
const pick=(a,n)=>sh(a).slice(0,n);

function gen(m,lvl,n){
  const Q=[];
  for(let i=0;i<n;i++){
    let id=MODS[m].id;
    if(id=="QUEST")id=["PHONICS","NUMBERS","ARTS","VALUES"][i%4],lvl=Math.min(lvl,6);

    if(id=="PHONICS"){
      const b=((lvl-1)*2)%16,pair=[LET[b],LET[b+1]],T=pair[i%2],D=pick(LET.filter(x=>x!=T),2);
      Q.push({
        big:T[0]+" "+T[0].toLowerCase(),
        say:`${T[0]} says ${T[2]}. ${t("sound")}`,
        key:"letter_"+T[0],
        ch:"titik",
        c:sh([T,...D]).map(x=>({h:x[1],ok:x==T,l:""}))
      });
    }
    else if(id=="NUMBERS"){
      const mx=Math.min(10,Math.max(2,lvl*2)),n2=1+Math.floor(Math.random()*mx),o=new Set([n2]);
      while(o.size<3)o.add(1+Math.floor(Math.random()*Math.max(mx,3)));
      const fr=["🍎","🍌","🐟","🥚","🌸"][i%5];
      Q.push({
        big:fr.repeat(n2),
        say:t("count"),
        key:"number_"+n2,
        ch:"bilang",
        c:sh([...o]).map(x=>({h:x,ok:x==n2,l:""}))
      });
    }
    else if(id=="ARTS"){
      if(lvl<=6){
        const k=Object.keys(SH),T=k[(lvl-1+i)%6],D=pick(k.filter(x=>x!=T),2);
        Q.push({
          big:"",
          say:t("tapq")+T,
          key:"shape_"+T,
          ch:"likha",
          c:sh([T,...D]).map(x=>({
            h:`<svg width="64" height="64" viewBox="0 0 60 60" fill="#FF7A6B" stroke="#2B3A67" stroke-width="3">${SH[x]}</svg>`,
            ok:x==T,
            l:x
          }))
        });
      }else{
        const k=Object.keys(COL),T=k[(lvl-7+i)%8],D=pick(k.filter(x=>x!=T),2);
        Q.push({
          big:"",
          say:t("tapq")+T,
          key:"color_"+T,
          ch:"likha",
          c:sh([T,...D]).map(x=>({
            h:`<svg width="64" height="64" viewBox="0 0 60 60"><circle cx="30" cy="30" r="26" fill="${COL[x]}" stroke="#2B3A67" stroke-width="3"/></svg>`,
            ok:x==T,
            l:x
          }))
        });
      }
    }
    else{
      const s=SC[(lvl-1+i)%6];
      Q.push({
        big:s[0],
        say:s[1]+" "+t("what"),
        key:"value_"+((lvl+i)%6),
        ch:"tala",
        txt:s[1],
        c:sh(s[2].map((x,j)=>({h:j?"🙅":"💛",ok:!j,l:x})))
      });
    }
  }
  return Q;
}

// ---- progress helpers
const unlocked=(m,l)=>MODS[m].id=="QUEST"
  ?(l==1?questOpen():((me().prog.QUEST||{})[l-1]>0))
  :(l==1||((me().prog[MODS[m].id]||{})[l-1]>0));

const questOpen=()=>MODS.slice(0,4).filter(x=>Object.values(me().prog[x.id]||{}).filter(v=>v>0).length>=3).length>=2;
const starsFor=a=>a>=.9?5:a>=.75?4:a>=.6?3:a>=.4?2:1;
const ST=(n,t="★",e="☆")=>t.repeat(n)+e.repeat(5-n);

// ---- render
function go(s,x){
  S.scr=s;
  Object.assign(S,x||{});
  render();
}

function render(){
  seed();
  const A=$("#app");
  const f=SCR[S.scr];
  A.innerHTML=`<div class="stage">${f()}</div>`;
  window.scrollTo(0,0);
  if(S.scr=="home")nav();
  if(S.scr=="play")arm();
}

function nav(){}

const NAV=()=>`<div class="nav">${[["path","🗺️","Path"],["shop","🛍️","Star Shop"],["sky","🌌","Sky Map"],["gear","⚙️","Teacher"]].map(a=>`<button class="ib" aria-label="${a[2]}" onclick="${a[0]=="gear"?"gate()":`S.tab='${a[0]}';render()`}">${a[1]}</button>`).join("")}<button class="ib" aria-label="Home" onclick="go('login')">🏠</button></div>`;

const TOP=()=>`<div class="top">${avHTML(S.cur,myAv())}<b class="f">${S.cur}</b><div class="pill" id="wal" aria-label="${t("wal")}">⭐ <b>${me().wallet}</b></div><button class="ib" style="min-width:72px;font:700 18px Fredoka" onclick="S.lang=S.lang=='en'?'fil':'en';save();render()" aria-label="Language">🌐 ${t("lang")}</button></div>`;

const SCR={
  splash(){
    return`<div class="ctr" style="margin:auto"><div style="font-size:30px;animation:pl 2s infinite">✨ 🌙 ✨</div><h1 style="font-size:clamp(64px,14vw,120px);color:var(--y);text-shadow:0 5px 0 #d49a10">TALA</h1><p class="f">A Voice-Guided Learning App</p><div class="row">${["tala","likha","bilang","titik"].map((c,i)=>ch(c,["wave","cheer","wave","think"][i])).join("")}</div><br><button class="btn" onclick="sfx.tap();say('${S.lang=="en"?"Hi! I’m Tala. Let’s collect star pieces!":"Kumusta! Ako si Tala. Tipunin natin ang mga bituin!"}');go('login')">▶ ${t("play")} 🔊</button><div style="margin-top:20px"><button class="btn g" style="min-height:56px;font-size:18px" onclick="gate()">🔒 ${t("gr")}</button> <button class="btn g" style="min-height:56px;font-size:18px" onclick="S.lang=S.lang=='en'?'fil':'en';save();render()">🌐 ${t("lang")}</button></div></div>`;
  },

  login(){
    return`<h2 class="ctr">${t("who")}</h2><div class="row">${AVS.map(a=>`<button class="card ctr" style="min-width:150px" aria-label="${a[0]}" onclick="S.cur='${a[0]}';sfx.ok();say('${t("hi")}${a[0]}!');go('home')"><div style="font-size:64px">${a[1]}</div><b class="f">${a[0]}</b><div>⭐ ${stu(a[0]).wallet}</div></button>`).join("")}</div><div class="ctr" style="margin-top:20px"><button class="btn g" onclick="go('splash')">🏠</button></div>`;
  },

  home(){
    const T=S.tab;
    let body="";
    const m=MODS[S.mod];

    if(T=="path"){
      const p=me().prog[m.id]||{};
      let cur=0;
      for(let l=1;l<=m.n;l++)if(unlocked(S.mod,l)&&!(p[l]>0)){cur=l;break}
      const lit=MODS.map((x,i)=>Object.values(me().prog[x.id]||{}).filter(v=>v>0).length);

      body=`<div class="card ctr" style="background:#2B3A67;color:#fff" aria-label="Constellation strip">${MODS.map((x,i)=>`<span style="margin:0 8px">${x.ic} ${"★".repeat(Math.min(lit[i],5))}${"☆".repeat(Math.max(0,5-lit[i]))}</span>`).join("")}</div>
      <div class="tabs" role="tablist">${MODS.map((x,i)=>`<button class="tab" role="tab" aria-selected="${i==S.mod}" aria-label="${x.nm}" onclick="S.mod=${i};render()">${x.ic}</button>`).join("")}</div>
      <div class="card ctr" style="background:${m.col};color:#2B3A67"><h3>${m.nm} · ${m.w}</h3></div>
      <div class="path">${Array.from({length:m.n},(_,i)=>{
        const l=i+1,u=unlocked(S.mod,l),b=p[l]||0,st=b?"":l==cur?"cur":u?"":"lock";
        const off=Math.sin(i*1.2)*Math.min(90,innerWidth/5);
        return`<div style="transform:translateX(${off}px);display:flex;align-items:center;gap:12px">${l==cur?`<div class="guide ctr">${ch(m.g,"wave","sm")}<div class="bubble" style="font-size:16px;padding:6px 12px">${S.lang=="en"?"Let’s learn!":"Tara, mag-aral!"}</div></div>`:""}<button class="node ${st}" aria-label="Level ${l}${b?", "+b+" stars":u?"":", locked"}" onclick="lvPop(${l})">${u?l:"🔒"}<small>${b?ST(b):""}</small></button></div>`;
      }).join("")}</div>`;
    }

    if(T=="shop"){
      const s=me();
      body=`<h2 class="ctr">🛍️ Star Shop & Closet</h2><div class="ctr">${avHTML(S.cur,myAv())}</div><div class="row" style="margin-top:14px">${SHOP.map(i=>{
        const own=s.owned.includes(i[0]);
        return`<button class="card ctr" style="min-width:130px" aria-label="${i[0]} ${i[1]} stars" onclick="shop('${i[0]}',${i[1]})"><div style="font-size:48px">${i[0]}</div>${own?(s.eq==i[0]?"✔ On":"👕 Wear"):"🔒 ⭐"+i[1]}</button>`;
      }).join("")}${s.eq?`<button class="btn g" onclick="me().eq=null;save();render()">❌ 👕</button>`:""}</div>`;
    }

    if(T=="sky"){
      const s=me();
      body=`<h2 class="ctr">🌌 Sky Map & Stickers</h2><div class="grid">${MODS.map((x,i)=>{
        const n=Object.values(s.prog[x.id]||{}).filter(v=>v>0).length;
        return`<div class="card ctr" style="background:#2B3A67;color:#fff"><b>${x.ic} ${x.nm}</b><div style="font-size:26px;word-break:break-all">${"★".repeat(n)}${"☆".repeat(Math.max(0,Math.min(x.n,12)-n))}</div></div>`;
      }).join("")}</div><div class="row" style="margin-top:14px">${BADGES.map(b=>{
        const e=b[2](s);
        return`<button class="card ctr" style="width:150px;${e?"":"filter:grayscale(1);opacity:.6"}" onclick="say('${e?"You earned "+b[0]+"!":"How to earn: "+b[0]}')" aria-label="${b[0]} ${e?"earned":"locked"}"><div style="font-size:44px">${b[1]}</div><small>${b[0]}</small></button>`;
      }).join("")}</div>`;
    }

    return TOP()+body+NAV();
  },

  play(){
    const F=S.F,m=MODS[F.m];
    const seg=`<div class="seg"><i class="${F.ph=="lesson"?"on":"done"}">${t("lesson")}</i><i class="${F.ph=="practice"?"on":F.ph=="done"?"done":""}">${t("practice")}</i></div>`;
    const head=`<div class="top"><button class="ib" aria-label="Home" onclick="speechSynthesis.cancel();go('home')">🏠</button>${seg}<div class="pill">⭐ ${me().wallet}</div></div>`;

    if(F.ph=="lesson"){
      const q=F.qs[0];
      return head+`<div class="card ctr guide">${ch(m.g,"talk")}<h2>${q.big?`<span style="font-size:64px;word-break:break-all">${q.big}</span>`:m.nm}</h2><div class="bubble" style="margin:auto">${q.say}</div><br><button class="ib" aria-label="Read aloud" onclick="say(S.F.qs[0].say,'${m.g}')">🔊</button></div>
      <div class="card"><h3>👋 ${t("watch")}</h3><div class="demo" id="demo"><b>❓</b><b>✅</b><b>❓</b><span class="hand">👆</span></div><div class="row"><button class="btn g" onclick="render()">↻ ${t("replay")}</button><button class="btn m" onclick="startP()">▶ ${t("ready")}</button></div></div>`;
    }

    if(F.ph=="practice"){
      const q=F.qs[F.i];
      return head+`<div class="dots" aria-label="progress">${F.qs.map((_,i)=>`<i class="${i<F.i?"ok":i==F.i?"on":""}"></i>`).join("")}</div><div class="row guide">${ch(m.g,F.mood||"think","sm")}<button class="ib" aria-label="Speak" onclick="say(S.F.qs[S.F.i].say,S.F.qs[S.F.i].ch)">🔊</button><button class="ib" aria-label="Hint" onclick="hint()">💡</button></div><div class="ctr"><div style="font-size:${q.big.length>6?44:70}px;font-family:Fredoka;word-break:break-all">${q.big}</div><p>${q.say}</p></div><div class="choices" id="ch">${q.c.map((c,i)=>`<button class="tile" aria-label="${c.l||c.h}" onclick="ans(${i})"><div>${c.h}</div>${c.l?`<span>${c.l}</span>`:""}</button>`).join("")}</div>`;
    }

    const a=F.ft/F.qs.length,st=starsFor(a),add=Math.max(0,st-F.best);
    return head+`<div class="ctr card guide">${ch(m.g,"cheer")}<h1>🎉 ${F.cf||""}</h1><div class="stars">${Array.from({length:5},(_,i)=>i<st?`<span class="pop fly" style="animation-delay:${i*.35}s,${2.2}s">⭐</span>`:`<span style="opacity:.3">☆</span>`).join("")}</div><p>${t("earned")} +${add}</p>
    <div class="row"><button class="btn m" onclick="startLv(S.F.m,${Math.min(F.l+1,MODS[F.m].n)})">${t("next")} ▶</button><button class="btn s" onclick="startLv(S.F.m,S.F.l)">↻ ${t("playagain")}</button><button class="btn g" aria-label="Home" onclick="go('home')">🏠</button></div></div>`;
  },

  dash(){
    const D=S.D||"ov",names=Object.keys(S.stu);
    const acc=(n,m)=>{
      const l=S.logs.filter(x=>x.s==n&&(!m||x.m==m));
      return l.length?Math.round(100*l.filter(x=>x.ft).length/l.length):null;
    };

    const ovr=()=>{
      const tot=names.reduce((a,n)=>a+stu(n).life,0);
      const need=names.filter(n=>{const a=acc(n);return a!==null&&a<60});
      return`<div class="grid"><div class="card ctr"><h3>👩‍🎓 ${names.length}</h3>Students</div><div class="card ctr"><h3>⭐ ${names.length?(tot/names.length).toFixed(1):0}</h3>Avg lifetime stars</div><div class="card ctr"><h3>📝 ${S.logs.length}</h3>Items answered</div></div><div class="grid" style="margin-top:14px"><div class="card"><h3>🆘 Needs support (&lt;60%)</h3>${need.map(n=>`<p>${n} – ${acc(n)}%</p>`).join("")||"<p>Everyone is on track 🎉</p>"}</div><div class="card"><h3>🏅 Top stars</h3>${[...names].sort((a,b)=>stu(b).life-stu(a).life).slice(0,3).map(n=>`<p>${n} – ⭐ ${stu(n).life}</p>`).join("")}</div></div>`;
    };

    const stus=()=>`<div class="card" style="overflow:auto"><table><tr><th>Name</th><th>⭐</th><th>Levels</th>${MODS.slice(0,4).map(m=>`<th title="${m.nm}">${m.ic}</th>`).join("")}</tr>${names.map(n=>`<tr><td>${n}</td><td>${stu(n).life}</td><td>${Object.values(stu(n).prog).reduce((a,p)=>a+Object.values(p).filter(v=>v>0).length,0)}</td>${MODS.slice(0,4).map(m=>{const a=acc(n,m.id);return`<td><div class="bar" aria-label="${a??0}%"><i style="width:${a??0}%"></i></div>${a===null?"–":a+"%"}</td>`}).join("")}</tr>`).join("")}</table></div>`;

    const rep=()=>`<div class="card ctr"><button class="btn" onclick="report()">📄 Export PTA Report</button> <button class="btn s" onclick="csv()">⬇ CSV preview</button><p>Report numbers are computed live from the play log.</p></div>`;

    const set=()=>`<div class="card"><h3>Settings</h3><p>Daily play limit (parent): <select id="lim" onchange="S.lim=this.value;save()"><option>Off</option><option>15</option><option>30</option><option>45</option><option>60</option></select></p><button class="btn g" onclick="S.logs=[];Object.keys(S.stu).forEach(k=>delete S.stu[k]);save();go('splash')">Reset all data</button> <button class="btn g" onclick="toggleTheme()">🌓 Theme: ${S.theme==="dark"?"Dark":"Light"}</button></div>`;

    return`<div class="top"><h2>👩‍🏫 Teacher / Parent</h2><button class="btn g" onclick="go('splash')">Exit</button></div><div class="side"><nav>${[["ov","📊","Overview"],["st","👩‍🎓","Students"],["rp","📄","Reports"],["se","⚙️","Settings"]].map(a=>`<button class="ib" aria-label="${a[2]}" style="${D==a[0]?"outline:4px solid var(--ink)":""}" onclick="S.D='${a[0]}';render()">${a[1]}</button>`).join("")}</nav><div style="flex:1">${{ov:ovr,st:stus,rp:rep,se:set}[D]()}</div></div>`;
  },

  gate(){
    return`<div class="ctr card" style="max-width:460px;margin:auto"><h2>🔒 ${t("gr")}</h2><button class="btn hold" id="hb" aria-label="Hold for 3 seconds"><i></i><span style="position:relative">${t("hold")}</span></button><div id="pw" style="display:none"><p>${t("pin")}</p><h2 id="pd">○○○○</h2><div class="pad">${[1,2,3,4,5,6,7,8,9,"✖",0,"✔"].map(k=>`<button onclick="pinK('${k}')" aria-label="${k}">${k}</button>`).join("")}</div></div><br><button class="btn g" onclick="go('splash')">🏠</button></div>`;
  }
};

// ---- actions
function gate(){
  S.pin="";
  go("gate");
  const b=$("#hb");
  let tm;

  const st=()=>{
    b.classList.add("go");
    tm=setTimeout(()=>{
      $("#hb").style.display="none";
      $("#pw").style.display="block";
    },3000);
  };

  const en=()=>{
    clearTimeout(tm);
    b.classList.remove("go");
  };

  b.onpointerdown=st;
  b.onpointerup=b.onpointerleave=en;
}

let bad=0,lock=0;

function pinK(k){
  if(Date.now()<lock){
    say("Please wait 30 seconds");
    return;
  }

  if(k=="✖")S.pin="";
  else if(k=="✔"||(S.pin+k).length>=4&&k!="✖"){
    if(k!="✔")S.pin+=k;
    if(S.pin=="1234"){
      bad=0;
      S.D="ov";
      go("dash");
      return;
    }
    bad++;
    S.pin="";
    if(bad>=5){
      lock=Date.now()+3e4;
      bad=0;
    }
  }else S.pin+=k;

  $("#pd").textContent="●".repeat(S.pin.length)+"○".repeat(4-S.pin.length);
}

function lvPop(l){
  const m=S.mod,u=unlocked(m,l),b=(me().prog[MODS[m].id]||{})[l]||0;
  const d=document.createElement("div");
  d.className="modal";
  d.onclick=e=>e.target==d&&d.remove();
  d.innerHTML=`<div class="card ctr">${ch(MODS[m].g,u?"wave":"sleepy")}<h2>${MODS[m].nm} · ${l}</h2><div class="stars" aria-label="best ${b} of 5">${ST(b)}</div>${u?`<button class="btn m" onclick="startLv(${m},${l})">▶ ${t("play")}</button>`:`<p>🔒 ${MODS[m].id=="QUEST"&&l==1?t("qlock"):t("locked")}</p>`}</div>`;
  document.body.appendChild(d);
  say(u?(S.lang=="en"?"Let’s play!":"Tara, laro tayo!"):(MODS[m].id=="QUEST"?t("qlock"):t("locked")),MODS[m].g);
}

function startLv(m,l){
  document.querySelectorAll(".modal").forEach(x=>x.remove());
  const n=MODS[m].id=="QUEST"?8:5;
  S.F={m,l,ph:"lesson",qs:gen(m,l,n),i:0,ft:0,miss:0,hint:0,best:(me().prog[MODS[m].id]||{})[l]||0};
  go("play");
  setTimeout(()=>say(S.F.qs[0].say,MODS[m].g),300);
}

function startP(){
  S.F.ph="practice";
  S.F.qs=gen(S.F.m,S.F.l,S.F.qs.length);
  S.F.i=0;
  S.F.miss=0;
  S.F.t=Date.now();
  render();
  say(S.F.qs[0].say,S.F.qs[0].ch);
}

function arm(){
  clearTimeout(arm.t);
  const F=S.F;
  if(F.ph!="practice")return;
  arm.t=setTimeout(hint,8000);
}

function hint(){
  const F=S.F,q=F.qs[F.i],k=q.c.findIndex(c=>c.ok);
  F.hint++;
  document.querySelectorAll("#ch .tile")[k]?.classList.add("glow");
  say(t("hint")+" "+q.say,q.ch);
  arm.t=null;
}

function ans(i){
  const F=S.F,q=F.qs[F.i],c=q.c[i],tiles=document.querySelectorAll("#ch .tile");
  sfx.tap();
  clearTimeout(arm.t);

  if(c.ok){
    sfx.ok();
    if(!F.miss)F.ft++;
    log(q,!F.miss);
    F.mood="cheer";
    say(pick(t("great"),1)[0],q.ch);
    tiles[i].style.outline="6px solid var(--m)";
    next();
  }else{
    F.miss++;
    sfx.soft();
    const k=q.c.findIndex(x=>x.ok);
    if(F.miss>=3){
      log(q,false);
      tiles[k].classList.add("wig");
      say(q.say.split(".")[0]+"!",q.ch);
      next();
    }else{
      say(t("again"),q.ch);
      tiles[k].classList.remove("glow");
      void tiles[k].offsetWidth;
      tiles[k].classList.add("wig");
      arm();
    }
  }
}

const log=(q,ft)=>{
  S.logs.push({s:S.cur,m:MODS[S.F.m].id,k:q.key,ft:ft?1:0,d:Date.now()});
};

function next(){
  setTimeout(()=>{
    const F=S.F;
    F.i++;
    F.miss=0;
    F.mood="think";
    if(F.i>=F.qs.length)return finish();
    render();
    say(F.qs[F.i].say,F.qs[F.i].ch);
  },1300);
}

function finish(){
  const F=S.F,s=me(),id=MODS[F.m].id,st=starsFor(F.ft/F.qs.length),add=Math.max(0,st-F.best);
  F.ph="done";
  s.prog[id]=s.prog[id]||{};
  s.prog[id][F.l]=Math.max(F.best,st);
  s.wallet+=add;
  s.life+=add;
  F.cf=pick(t("great"),1)[0];
  save();
  render();
  sfx.fan();
  say(`${F.cf} ${t("earned")} ${st}`,MODS[F.m].g);

  for(let i=0;i<30;i++){
    const c=document.createElement("div");
    c.className="cf";
    c.textContent=["⭐","✨","🎉","💛"][i%4];
    c.style.left=Math.random()*100+"vw";
    c.style.animationDelay=Math.random()+"s";
    document.body.appendChild(c);
    setTimeout(()=>c.remove(),4500);
  }
}

function shop(i,p){
  const s=me();
  if(s.owned.includes(i)){
    s.eq=i;
    save();
    return render();
  }

  const d=document.createElement("div");
  d.className="modal";
  d.innerHTML=`<div class="card ctr"><div style="font-size:70px">${i}</div>${avHTML(S.cur,myAv())}<p>${t("buy")}⭐ ${p}? (⭐ ${s.wallet})</p>${s.wallet>=p?`<div class="row"><button class="btn m" aria-label="Yes" id="y">✔</button><button class="btn g" aria-label="No" id="n">✖</button></div>`:`<div class="ctr">${ch("tala","sleepy","sm")}<h3 style="color:var(--s)">${t("keep")}</h3><button class="btn g" id="n">OK</button></div>`}</div>`;

  document.body.appendChild(d);

  if(s.wallet<p)say(t("keep"));
  $("#n").onclick=()=>d.remove();

  const y=$("#y");
  if(y)y.onclick=()=>{
    s.wallet-=p;
    s.owned.push(i);
    s.eq=i;
    save();
    sfx.star();
    d.remove();
    render();
  };
}

function report(){
  const names=Object.keys(S.stu);
  const acc=(n,m)=>{
    const l=S.logs.filter(x=>x.s==n&&x.m==m);
    return l.length?Math.round(100*l.filter(x=>x.ft).length/l.length):null;
  };

  const weak=n=>{
    const w=S.logs.filter(x=>x.s==n&&!x.ft).map(x=>x.k.split("_")[0]);
    return w.length?"Needs practice with "+[...new Set(w)].slice(0,2).join(", "):"On track";
  };

  const d=document.createElement("div");
  d.className="modal";
  d.innerHTML=`<div class="card" id="rp"><h2>📄 PTA Report – ${new Date().toLocaleDateString()}</h2><p>School: Sampaguita Elementary · Grade 1 – Masigasig</p><table><tr><th>Pupil</th><th>⭐</th><th>Phonics</th><th>Numbers</th><th>Arts</th><th>Values</th><th>Notes</th></tr>${names.map(n=>`<tr><td>${n}</td><td>${stu(n).life}</td>${["PHONICS","NUMBERS","ARTS","VALUES"].map(m=>`<td>${acc(n,m)??"–"}${acc(n,m)!==null?"%":""}</td>`).join("")}<td>${weak(n)}</td></tr>`).join("")}</table><br><p>Signature: ______________________</p><div class="row"><button class="btn m" onclick="print()">🖨 Print / Save PDF</button><button class="btn g" onclick="this.closest('.modal').remove()">Close</button></div></div>`;

  document.body.appendChild(d);
}

function csv(){
  const d=document.createElement("div");
  d.className="modal";
  d.onclick=e=>e.target==d&&d.remove();
  d.innerHTML=`<div class="card"><h3>ActivityResult CSV</h3><pre style="overflow:auto;font-size:14px;max-height:50vh">student,module,itemKey,firstTryCorrect\n${S.logs.slice(-40).map(x=>[x.s,x.m,x.k,x.ft].join(",")).join("\n")}</pre></div>`;
  document.body.appendChild(d);
}

render();