function superstartrek_superstartrek(){var O='bootstrap',P='begin',Q='gwt.codesvr.superstartrek.superstartrek=',R='gwt.codesvr=',S='superstartrek.superstartrek',T='startup',U='DUMMY',V=0,W=1,X='iframe',Y='position:absolute; width:0; height:0; border:none; left: -1000px;',Z=' top: -1000px;',$='CSS1Compat',_='<!doctype html>',ab='',bb='<html><head><\/head><body><\/body><\/html>',cb='undefined',db='readystatechange',eb=10,fb='Chrome',gb='eval("',hb='");',ib='script',jb='javascript',kb='moduleStartup',lb='moduleRequested',mb='superstartrek_superstartrek',nb='Failed to load ',ob='head',pb='meta',qb='name',rb='superstartrek.superstartrek::',sb='::',tb='gwt:property',ub='content',vb='=',wb='gwt:onPropertyErrorFn',xb='Bad handler "',yb='" for "gwt:onPropertyErrorFn"',zb='gwt:onLoadErrorFn',Ab='" for "gwt:onLoadErrorFn"',Bb='#',Cb='?',Db='/',Eb='img',Fb='clear.cache.gif',Gb='baseUrl',Hb='superstartrek.superstartrek.nocache.js',Ib='base',Jb='//',Kb='user.agent',Lb='webkit',Mb='safari',Nb='msie',Ob=11,Pb='ie10',Qb=9,Rb='ie9',Sb=8,Tb='ie8',Ub='gecko',Vb='gecko1_8',Wb=2,Xb=3,Yb=4,Zb='selectingPermutation',$b='superstartrek.superstartrek.devmode.js',_b='916318F51FA9A23C9C59A16301D6466B',ac='D5BC8BD4EB05E312ACA0FC5EF8DC311B',bc='D75D960E93A9675D7BB9EB9A47305E5A',cc='E93508C0F8CBC0FBF10ED2FE9696E43E',dc='F81C1C8677075927C435DB89341CB023',ec=':',fc='.cache.js',gc='loadExternalRefs',hc='end',ic='http:',jc='file:',kc='_gwt_dummy_',lc='__gwtDevModeHook:superstartrek.superstartrek',mc='Ignoring non-whitelisted Dev Mode URL: ',nc=':moduleBase';var o=window;var p=document;r(O,P);function q(){var a=o.location.search;return a.indexOf(Q)!=-1||a.indexOf(R)!=-1}
function r(a,b){if(o.__gwtStatsEvent){o.__gwtStatsEvent({moduleName:S,sessionId:o.__gwtStatsSessionId,subSystem:T,evtGroup:a,millis:(new Date).getTime(),type:b})}}
superstartrek_superstartrek.__sendStats=r;superstartrek_superstartrek.__moduleName=S;superstartrek_superstartrek.__errFn=null;superstartrek_superstartrek.__moduleBase=U;superstartrek_superstartrek.__softPermutationId=V;superstartrek_superstartrek.__computePropValue=null;superstartrek_superstartrek.__getPropMap=null;superstartrek_superstartrek.__installRunAsyncCode=function(){};superstartrek_superstartrek.__gwtStartLoadingFragment=function(){return null};superstartrek_superstartrek.__gwt_isKnownPropertyValue=function(){return false};superstartrek_superstartrek.__gwt_getMetaProperty=function(){return null};var s=null;var t=o.__gwt_activeModules=o.__gwt_activeModules||{};t[S]={moduleName:S};superstartrek_superstartrek.__moduleStartupDone=function(e){var f=t[S].bindings;t[S].bindings=function(){var a=f?f():{};var b=e[superstartrek_superstartrek.__softPermutationId];for(var c=V;c<b.length;c++){var d=b[c];a[d[V]]=d[W]}return a}};var u;function v(){w();return u}
function w(){if(u){return}var a=p.createElement(X);a.id=S;a.style.cssText=Y+Z;a.tabIndex=-1;p.body.appendChild(a);u=a.contentWindow.document;u.open();var b=document.compatMode==$?_:ab;u.write(b+bb);u.close()}
function A(k){function l(a){function b(){if(typeof p.readyState==cb){return typeof p.body!=cb&&p.body!=null}return /loaded|complete/.test(p.readyState)}
var c=b();if(c){a();return}function d(){if(!c){if(!b()){return}c=true;a();if(p.removeEventListener){p.removeEventListener(db,d,false)}if(e){clearInterval(e)}}}
if(p.addEventListener){p.addEventListener(db,d,false)}var e=setInterval(function(){d()},eb)}
function m(c){function d(a,b){a.removeChild(b)}
var e=v();var f=e.body;var g;if(navigator.userAgent.indexOf(fb)>-1&&window.JSON){var h=e.createDocumentFragment();h.appendChild(e.createTextNode(gb));for(var i=V;i<c.length;i++){var j=window.JSON.stringify(c[i]);h.appendChild(e.createTextNode(j.substring(W,j.length-W)))}h.appendChild(e.createTextNode(hb));g=e.createElement(ib);g.language=jb;g.appendChild(h);f.appendChild(g);d(f,g)}else{for(var i=V;i<c.length;i++){g=e.createElement(ib);g.language=jb;g.text=c[i];f.appendChild(g);d(f,g)}}}
superstartrek_superstartrek.onScriptDownloaded=function(a){l(function(){m(a)})};r(kb,lb);var n=p.createElement(ib);n.src=k;if(superstartrek_superstartrek.__errFn){n.onerror=function(){superstartrek_superstartrek.__errFn(mb,new Error(nb+code))}}p.getElementsByTagName(ob)[V].appendChild(n)}
superstartrek_superstartrek.__startLoadingFragment=function(a){return D(a)};superstartrek_superstartrek.__installRunAsyncCode=function(a){var b=v();var c=b.body;var d=b.createElement(ib);d.language=jb;d.text=a;c.appendChild(d);c.removeChild(d)};function B(){var c={};var d;var e;var f=p.getElementsByTagName(pb);for(var g=V,h=f.length;g<h;++g){var i=f[g],j=i.getAttribute(qb),k;if(j){j=j.replace(rb,ab);if(j.indexOf(sb)>=V){continue}if(j==tb){k=i.getAttribute(ub);if(k){var l,m=k.indexOf(vb);if(m>=V){j=k.substring(V,m);l=k.substring(m+W)}else{j=k;l=ab}c[j]=l}}else if(j==wb){k=i.getAttribute(ub);if(k){try{d=eval(k)}catch(a){alert(xb+k+yb)}}}else if(j==zb){k=i.getAttribute(ub);if(k){try{e=eval(k)}catch(a){alert(xb+k+Ab)}}}}}__gwt_getMetaProperty=function(a){var b=c[a];return b==null?null:b};s=d;superstartrek_superstartrek.__errFn=e}
function C(){function e(a){var b=a.lastIndexOf(Bb);if(b==-1){b=a.length}var c=a.indexOf(Cb);if(c==-1){c=a.length}var d=a.lastIndexOf(Db,Math.min(c,b));return d>=V?a.substring(V,d+W):ab}
function f(a){if(a.match(/^\w+:\/\//)){}else{var b=p.createElement(Eb);b.src=a+Fb;a=e(b.src)}return a}
function g(){var a=__gwt_getMetaProperty(Gb);if(a!=null){return a}return ab}
function h(){var a=p.getElementsByTagName(ib);for(var b=V;b<a.length;++b){if(a[b].src.indexOf(Hb)!=-1){return e(a[b].src)}}return ab}
function i(){var a=p.getElementsByTagName(Ib);if(a.length>V){return a[a.length-W].href}return ab}
function j(){var a=p.location;return a.href==a.protocol+Jb+a.host+a.pathname+a.search+a.hash}
var k=g();if(k==ab){k=h()}if(k==ab){k=i()}if(k==ab&&j()){k=e(p.location.href)}k=f(k);return k}
function D(a){if(a.match(/^\//)){return a}if(a.match(/^[a-zA-Z]+:\/\//)){return a}return superstartrek_superstartrek.__moduleBase+a}
function F(){var f=[];var g=V;function h(a,b){var c=f;for(var d=V,e=a.length-W;d<e;++d){c=c[a[d]]||(c[a[d]]=[])}c[a[e]]=b}
var i=[];var j=[];function k(a){var b=j[a](),c=i[a];if(b in c){return b}var d=[];for(var e in c){d[c[e]]=e}if(s){s(a,d,b)}throw null}
j[Kb]=function(){var a=navigator.userAgent.toLowerCase();var b=p.documentMode;if(function(){return a.indexOf(Lb)!=-1}())return Mb;if(function(){return a.indexOf(Nb)!=-1&&(b>=eb&&b<Ob)}())return Pb;if(function(){return a.indexOf(Nb)!=-1&&(b>=Qb&&b<Ob)}())return Rb;if(function(){return a.indexOf(Nb)!=-1&&(b>=Sb&&b<Ob)}())return Tb;if(function(){return a.indexOf(Ub)!=-1||b>=Ob}())return Vb;return ab};i[Kb]={'gecko1_8':V,'ie10':W,'ie8':Wb,'ie9':Xb,'safari':Yb};__gwt_isKnownPropertyValue=function(a,b){return b in i[a]};superstartrek_superstartrek.__getPropMap=function(){var a={};for(var b in i){if(i.hasOwnProperty(b)){a[b]=k(b)}}return a};superstartrek_superstartrek.__computePropValue=k;o.__gwt_activeModules[S].bindings=superstartrek_superstartrek.__getPropMap;r(O,Zb);if(q()){return D($b)}var l;try{h([Pb],_b);h([Vb],ac);h([Mb],bc);h([Tb],cc);h([Rb],dc);l=f[k(Kb)];var m=l.indexOf(ec);if(m!=-1){g=parseInt(l.substring(m+W),eb);l=l.substring(V,m)}}catch(a){}superstartrek_superstartrek.__softPermutationId=g;return D(l+fc)}
function G(){if(!o.__gwt_stylesLoaded){o.__gwt_stylesLoaded={}}r(gc,P);r(gc,hc)}
B();superstartrek_superstartrek.__moduleBase=C();t[S].moduleBase=superstartrek_superstartrek.__moduleBase;var H=F();if(o){var I=!!(o.location.protocol==ic||o.location.protocol==jc);o.__gwt_activeModules[S].canRedirect=I;function J(){var b=kc;try{o.sessionStorage.setItem(b,b);o.sessionStorage.removeItem(b);return true}catch(a){return false}}
if(I&&J()){var K=lc;var L=o.sessionStorage[K];if(!/^http:\/\/(localhost|127\.0\.0\.1)(:\d+)?\/.*$/.test(L)){if(L&&(window.console&&console.log)){console.log(mc+L)}L=ab}if(L&&!o[K]){o[K]=true;o[K+nc]=C();var M=p.createElement(ib);M.src=L;var N=p.getElementsByTagName(ob)[V];N.insertBefore(M,N.firstElementChild||N.children[V]);return false}}}G();r(O,hc);A(H);return true}
superstartrek_superstartrek.succeeded=superstartrek_superstartrek();