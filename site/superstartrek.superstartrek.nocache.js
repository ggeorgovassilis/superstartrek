function superstartrek_superstartrek(){
  var $intern_0 = 'bootstrap', $intern_1 = 'begin', $intern_2 = 'gwt.codesvr.superstartrek.superstartrek=', $intern_3 = 'gwt.codesvr=', $intern_4 = 'superstartrek.superstartrek', $intern_5 = 'startup', $intern_6 = 'DUMMY', $intern_7 = 0, $intern_8 = 1, $intern_9 = 'iframe', $intern_10 = 'position:absolute; width:0; height:0; border:none; left: -1000px;', $intern_11 = ' top: -1000px;', $intern_12 = 'CSS1Compat', $intern_13 = '<!doctype html>', $intern_14 = '', $intern_15 = '<html><head><\/head><body><\/body><\/html>', $intern_16 = 'undefined', $intern_17 = 'readystatechange', $intern_18 = 10, $intern_19 = 'script', $intern_20 = 'javascript', $intern_21 = 'superstartrek_superstartrek', $intern_22 = 'Failed to load ', $intern_23 = 'moduleStartup', $intern_24 = 'scriptTagAdded', $intern_25 = 'moduleRequested', $intern_26 = 'meta', $intern_27 = 'name', $intern_28 = 'superstartrek.superstartrek::', $intern_29 = '::', $intern_30 = 'gwt:property', $intern_31 = 'content', $intern_32 = '=', $intern_33 = 'gwt:onPropertyErrorFn', $intern_34 = 'Bad handler "', $intern_35 = '" for "gwt:onPropertyErrorFn"', $intern_36 = 'gwt:onLoadErrorFn', $intern_37 = '" for "gwt:onLoadErrorFn"', $intern_38 = '#', $intern_39 = '?', $intern_40 = '/', $intern_41 = 'img', $intern_42 = 'clear.cache.gif', $intern_43 = 'baseUrl', $intern_44 = 'superstartrek.superstartrek.nocache.js', $intern_45 = 'base', $intern_46 = '//', $intern_47 = 'user.agent', $intern_48 = 'webkit', $intern_49 = 'safari', $intern_50 = 'msie', $intern_51 = 11, $intern_52 = 'ie10', $intern_53 = 9, $intern_54 = 'ie9', $intern_55 = 8, $intern_56 = 'ie8', $intern_57 = 'gecko', $intern_58 = 'gecko1_8', $intern_59 = 2, $intern_60 = 3, $intern_61 = 4, $intern_62 = 'selectingPermutation', $intern_63 = 'superstartrek.superstartrek.devmode.js', $intern_64 = '173CC886E16CEE85A23B1021A0F262ED', $intern_65 = '439E6A983593554C89DFC9423823F847', $intern_66 = '838F866633F1CB73B1540C9D7AD2C325', $intern_67 = '8D77238A30B4ACF14B13DBE760F58798', $intern_68 = 'A85779344D2C34BA2229ACB27F31BB29', $intern_69 = ':', $intern_70 = '.cache.js', $intern_71 = 'loadExternalRefs', $intern_72 = 'end', $intern_73 = 'http:', $intern_74 = 'file:', $intern_75 = '_gwt_dummy_', $intern_76 = '__gwtDevModeHook:superstartrek.superstartrek', $intern_77 = 'Ignoring non-whitelisted Dev Mode URL: ', $intern_78 = ':moduleBase', $intern_79 = 'head';
  var $wnd = window;
  var $doc = document;
  sendStats($intern_0, $intern_1);
  function isHostedMode(){
    var query = $wnd.location.search;
    return query.indexOf($intern_2) != -1 || query.indexOf($intern_3) != -1;
  }

  function sendStats(evtGroupString, typeString){
    if ($wnd.__gwtStatsEvent) {
      $wnd.__gwtStatsEvent({moduleName:$intern_4, sessionId:$wnd.__gwtStatsSessionId, subSystem:$intern_5, evtGroup:evtGroupString, millis:(new Date).getTime(), type:typeString});
    }
  }

  superstartrek_superstartrek.__sendStats = sendStats;
  superstartrek_superstartrek.__moduleName = $intern_4;
  superstartrek_superstartrek.__errFn = null;
  superstartrek_superstartrek.__moduleBase = $intern_6;
  superstartrek_superstartrek.__softPermutationId = $intern_7;
  superstartrek_superstartrek.__computePropValue = null;
  superstartrek_superstartrek.__getPropMap = null;
  superstartrek_superstartrek.__installRunAsyncCode = function(){
  }
  ;
  superstartrek_superstartrek.__gwtStartLoadingFragment = function(){
    return null;
  }
  ;
  superstartrek_superstartrek.__gwt_isKnownPropertyValue = function(){
    return false;
  }
  ;
  superstartrek_superstartrek.__gwt_getMetaProperty = function(){
    return null;
  }
  ;
  var __propertyErrorFunction = null;
  var activeModules = $wnd.__gwt_activeModules = $wnd.__gwt_activeModules || {};
  activeModules[$intern_4] = {moduleName:$intern_4};
  superstartrek_superstartrek.__moduleStartupDone = function(permProps){
    var oldBindings = activeModules[$intern_4].bindings;
    activeModules[$intern_4].bindings = function(){
      var props = oldBindings?oldBindings():{};
      var embeddedProps = permProps[superstartrek_superstartrek.__softPermutationId];
      for (var i = $intern_7; i < embeddedProps.length; i++) {
        var pair = embeddedProps[i];
        props[pair[$intern_7]] = pair[$intern_8];
      }
      return props;
    }
    ;
  }
  ;
  var frameDoc;
  function getInstallLocationDoc(){
    setupInstallLocation();
    return frameDoc;
  }

  function setupInstallLocation(){
    if (frameDoc) {
      return;
    }
    var scriptFrame = $doc.createElement($intern_9);
    scriptFrame.id = $intern_4;
    scriptFrame.style.cssText = $intern_10 + $intern_11;
    scriptFrame.tabIndex = -1;
    $doc.body.appendChild(scriptFrame);
    frameDoc = scriptFrame.contentWindow.document;
    frameDoc.open();
    var doctype = document.compatMode == $intern_12?$intern_13:$intern_14;
    frameDoc.write(doctype + $intern_15);
    frameDoc.close();
  }

  function installScript(filename){
    function setupWaitForBodyLoad(callback){
      function isBodyLoaded(){
        if (typeof $doc.readyState == $intern_16) {
          return typeof $doc.body != $intern_16 && $doc.body != null;
        }
        return /loaded|complete/.test($doc.readyState);
      }

      var bodyDone = isBodyLoaded();
      if (bodyDone) {
        callback();
        return;
      }
      function checkBodyDone(){
        if (!bodyDone) {
          if (!isBodyLoaded()) {
            return;
          }
          bodyDone = true;
          callback();
          if ($doc.removeEventListener) {
            $doc.removeEventListener($intern_17, checkBodyDone, false);
          }
          if (onBodyDoneTimerId) {
            clearInterval(onBodyDoneTimerId);
          }
        }
      }

      if ($doc.addEventListener) {
        $doc.addEventListener($intern_17, checkBodyDone, false);
      }
      var onBodyDoneTimerId = setInterval(function(){
        checkBodyDone();
      }
      , $intern_18);
    }

    function installCode(code_0){
      var doc = getInstallLocationDoc();
      var docbody = doc.body;
      var script = doc.createElement($intern_19);
      script.language = $intern_20;
      script.src = code_0;
      if (superstartrek_superstartrek.__errFn) {
        script.onerror = function(){
          superstartrek_superstartrek.__errFn($intern_21, new Error($intern_22 + code_0));
        }
        ;
      }
      docbody.appendChild(script);
      sendStats($intern_23, $intern_24);
    }

    sendStats($intern_23, $intern_25);
    setupWaitForBodyLoad(function(){
      installCode(filename);
    }
    );
  }

  superstartrek_superstartrek.__startLoadingFragment = function(fragmentFile){
    return computeUrlForResource(fragmentFile);
  }
  ;
  superstartrek_superstartrek.__installRunAsyncCode = function(code_0){
    var doc = getInstallLocationDoc();
    var docbody = doc.body;
    var script = doc.createElement($intern_19);
    script.language = $intern_20;
    script.text = code_0;
    docbody.appendChild(script);
  }
  ;
  function processMetas(){
    var metaProps = {};
    var propertyErrorFunc;
    var onLoadErrorFunc;
    var metas = $doc.getElementsByTagName($intern_26);
    for (var i = $intern_7, n = metas.length; i < n; ++i) {
      var meta = metas[i], name_0 = meta.getAttribute($intern_27), content;
      if (name_0) {
        name_0 = name_0.replace($intern_28, $intern_14);
        if (name_0.indexOf($intern_29) >= $intern_7) {
          continue;
        }
        if (name_0 == $intern_30) {
          content = meta.getAttribute($intern_31);
          if (content) {
            var value_0, eq = content.indexOf($intern_32);
            if (eq >= $intern_7) {
              name_0 = content.substring($intern_7, eq);
              value_0 = content.substring(eq + $intern_8);
            }
             else {
              name_0 = content;
              value_0 = $intern_14;
            }
            metaProps[name_0] = value_0;
          }
        }
         else if (name_0 == $intern_33) {
          content = meta.getAttribute($intern_31);
          if (content) {
            try {
              propertyErrorFunc = eval(content);
            }
             catch (e) {
              alert($intern_34 + content + $intern_35);
            }
          }
        }
         else if (name_0 == $intern_36) {
          content = meta.getAttribute($intern_31);
          if (content) {
            try {
              onLoadErrorFunc = eval(content);
            }
             catch (e) {
              alert($intern_34 + content + $intern_37);
            }
          }
        }
      }
    }
    __gwt_getMetaProperty = function(name_0){
      var value_0 = metaProps[name_0];
      return value_0 == null?null:value_0;
    }
    ;
    __propertyErrorFunction = propertyErrorFunc;
    superstartrek_superstartrek.__errFn = onLoadErrorFunc;
  }

  function computeScriptBase(){
    function getDirectoryOfFile(path){
      var hashIndex = path.lastIndexOf($intern_38);
      if (hashIndex == -1) {
        hashIndex = path.length;
      }
      var queryIndex = path.indexOf($intern_39);
      if (queryIndex == -1) {
        queryIndex = path.length;
      }
      var slashIndex = path.lastIndexOf($intern_40, Math.min(queryIndex, hashIndex));
      return slashIndex >= $intern_7?path.substring($intern_7, slashIndex + $intern_8):$intern_14;
    }

    function ensureAbsoluteUrl(url_0){
      if (url_0.match(/^\w+:\/\//)) {
      }
       else {
        var img = $doc.createElement($intern_41);
        img.src = url_0 + $intern_42;
        url_0 = getDirectoryOfFile(img.src);
      }
      return url_0;
    }

    function tryMetaTag(){
      var metaVal = __gwt_getMetaProperty($intern_43);
      if (metaVal != null) {
        return metaVal;
      }
      return $intern_14;
    }

    function tryNocacheJsTag(){
      var scriptTags = $doc.getElementsByTagName($intern_19);
      for (var i = $intern_7; i < scriptTags.length; ++i) {
        if (scriptTags[i].src.indexOf($intern_44) != -1) {
          return getDirectoryOfFile(scriptTags[i].src);
        }
      }
      return $intern_14;
    }

    function tryBaseTag(){
      var baseElements = $doc.getElementsByTagName($intern_45);
      if (baseElements.length > $intern_7) {
        return baseElements[baseElements.length - $intern_8].href;
      }
      return $intern_14;
    }

    function isLocationOk(){
      var loc = $doc.location;
      return loc.href == loc.protocol + $intern_46 + loc.host + loc.pathname + loc.search + loc.hash;
    }

    var tempBase = tryMetaTag();
    if (tempBase == $intern_14) {
      tempBase = tryNocacheJsTag();
    }
    if (tempBase == $intern_14) {
      tempBase = tryBaseTag();
    }
    if (tempBase == $intern_14 && isLocationOk()) {
      tempBase = getDirectoryOfFile($doc.location.href);
    }
    tempBase = ensureAbsoluteUrl(tempBase);
    return tempBase;
  }

  function computeUrlForResource(resource){
    if (resource.match(/^\//)) {
      return resource;
    }
    if (resource.match(/^[a-zA-Z]+:\/\//)) {
      return resource;
    }
    return superstartrek_superstartrek.__moduleBase + resource;
  }

  function getCompiledCodeFilename(){
    var answers = [];
    var softPermutationId = $intern_7;
    function unflattenKeylistIntoAnswers(propValArray, value_0){
      var answer = answers;
      for (var i = $intern_7, n = propValArray.length - $intern_8; i < n; ++i) {
        answer = answer[propValArray[i]] || (answer[propValArray[i]] = []);
      }
      answer[propValArray[n]] = value_0;
    }

    var values = [];
    var providers = [];
    function computePropValue(propName){
      var value_0 = providers[propName](), allowedValuesMap = values[propName];
      if (value_0 in allowedValuesMap) {
        return value_0;
      }
      var allowedValuesList = [];
      for (var k in allowedValuesMap) {
        allowedValuesList[allowedValuesMap[k]] = k;
      }
      if (__propertyErrorFunction) {
        __propertyErrorFunction(propName, allowedValuesList, value_0);
      }
      throw null;
    }

    providers[$intern_47] = function(){
      var ua = navigator.userAgent.toLowerCase();
      var docMode = $doc.documentMode;
      if (function(){
        return ua.indexOf($intern_48) != -1;
      }
      ())
        return $intern_49;
      if (function(){
        return ua.indexOf($intern_50) != -1 && (docMode >= $intern_18 && docMode < $intern_51);
      }
      ())
        return $intern_52;
      if (function(){
        return ua.indexOf($intern_50) != -1 && (docMode >= $intern_53 && docMode < $intern_51);
      }
      ())
        return $intern_54;
      if (function(){
        return ua.indexOf($intern_50) != -1 && (docMode >= $intern_55 && docMode < $intern_51);
      }
      ())
        return $intern_56;
      if (function(){
        return ua.indexOf($intern_57) != -1 || docMode >= $intern_51;
      }
      ())
        return $intern_58;
      return $intern_14;
    }
    ;
    values[$intern_47] = {'gecko1_8':$intern_7, 'ie10':$intern_8, 'ie8':$intern_59, 'ie9':$intern_60, 'safari':$intern_61};
    __gwt_isKnownPropertyValue = function(propName, propValue){
      return propValue in values[propName];
    }
    ;
    superstartrek_superstartrek.__getPropMap = function(){
      var result = {};
      for (var key in values) {
        if (values.hasOwnProperty(key)) {
          result[key] = computePropValue(key);
        }
      }
      return result;
    }
    ;
    superstartrek_superstartrek.__computePropValue = computePropValue;
    $wnd.__gwt_activeModules[$intern_4].bindings = superstartrek_superstartrek.__getPropMap;
    sendStats($intern_0, $intern_62);
    if (isHostedMode()) {
      return computeUrlForResource($intern_63);
    }
    var strongName;
    try {
      unflattenKeylistIntoAnswers([$intern_54], $intern_64);
      unflattenKeylistIntoAnswers([$intern_52], $intern_65);
      unflattenKeylistIntoAnswers([$intern_58], $intern_66);
      unflattenKeylistIntoAnswers([$intern_49], $intern_67);
      unflattenKeylistIntoAnswers([$intern_56], $intern_68);
      strongName = answers[computePropValue($intern_47)];
      var idx = strongName.indexOf($intern_69);
      if (idx != -1) {
        softPermutationId = parseInt(strongName.substring(idx + $intern_8), $intern_18);
        strongName = strongName.substring($intern_7, idx);
      }
    }
     catch (e) {
    }
    superstartrek_superstartrek.__softPermutationId = softPermutationId;
    return computeUrlForResource(strongName + $intern_70);
  }

  function loadExternalStylesheets(){
    if (!$wnd.__gwt_stylesLoaded) {
      $wnd.__gwt_stylesLoaded = {};
    }
    sendStats($intern_71, $intern_1);
    sendStats($intern_71, $intern_72);
  }

  processMetas();
  superstartrek_superstartrek.__moduleBase = computeScriptBase();
  activeModules[$intern_4].moduleBase = superstartrek_superstartrek.__moduleBase;
  var filename = getCompiledCodeFilename();
  if ($wnd) {
    var devModePermitted = !!($wnd.location.protocol == $intern_73 || $wnd.location.protocol == $intern_74);
    $wnd.__gwt_activeModules[$intern_4].canRedirect = devModePermitted;
    function supportsSessionStorage(){
      var key = $intern_75;
      try {
        $wnd.sessionStorage.setItem(key, key);
        $wnd.sessionStorage.removeItem(key);
        return true;
      }
       catch (e) {
        return false;
      }
    }

    if (devModePermitted && supportsSessionStorage()) {
      var devModeKey = $intern_76;
      var devModeUrl = $wnd.sessionStorage[devModeKey];
      if (!/^http:\/\/(localhost|127\.0\.0\.1)(:\d+)?\/.*$/.test(devModeUrl)) {
        if (devModeUrl && (window.console && console.log)) {
          console.log($intern_77 + devModeUrl);
        }
        devModeUrl = $intern_14;
      }
      if (devModeUrl && !$wnd[devModeKey]) {
        $wnd[devModeKey] = true;
        $wnd[devModeKey + $intern_78] = computeScriptBase();
        var devModeScript = $doc.createElement($intern_19);
        devModeScript.src = devModeUrl;
        var head = $doc.getElementsByTagName($intern_79)[$intern_7];
        head.insertBefore(devModeScript, head.firstElementChild || head.children[$intern_7]);
        return false;
      }
    }
  }
  loadExternalStylesheets();
  sendStats($intern_0, $intern_72);
  installScript(filename);
  return true;
}

superstartrek_superstartrek.succeeded = superstartrek_superstartrek();
