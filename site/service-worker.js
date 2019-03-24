var APP_PREFIX = 'sst'     // Identifier for this app (this needs to be consistent across every cache update)
var VERSION = '1'              // Version of the off-line cache (change this value everytime you want to update cache)
var CACHE_NAME = APP_PREFIX + VERSION

//Add resources that should be available offline to this list.
let URLS = [
	"/superstartrek/site/index.html",
	"/superstartrek/site/images/icon192x192.png",
	"/superstartrek/site/images/icon512x512.png",
	"/superstartrek/site/images/stars-background.gif",
	"/superstartrek/site/css/sst.css",
	"/superstartrek/site/superstartrek.superstartrek.nocache.js",
	"/superstartrek/site/images/stars-background.gif"
];

// Respond with cached resources
self.addEventListener('fetch', function (e) {
  console.debug('SW_fetch','fetch request : ' + e.request.url)
  if (e.request.url.indexOf("__purge_cache")!=-1){
	  clearCache();
	  return;
  }
  e.respondWith(
    caches.match(e.request).then(function (request) {
      if (request) { // if cache is available, respond with cache
        console.debug('SW','responding with cache : ' + e.request.url)
        return request
      } else {       // if there are no cache, try fetching request
        console.debug('SW','file is not cached, fetching : ' + e.request.url)
        return fetch(e.request)
      }

      // You can omit if/else for console.debug & put one line below like this too.
      // return request || fetch(e.request)
    })
  )
})

// Cache resources
self.addEventListener('install', function (e) {
  console.log("listening for SW install");
  e.waitUntil(
    caches.open(CACHE_NAME).then(function (cache) {
      console.log("SW",'installing cache : ' + CACHE_NAME);
      cache.addAll(URLS);
      return true;
    })
  )
})

self.addEventListener('activate', function (e) {
	  console.log("listening for SW activate");
  e.waitUntil(
    caches.keys().then(function (keyList) {
    	console.log("SW activated");
      // `keyList` contains all cache names under your username.github.io
      // filter out ones that has this app prefix to create white list
      var cacheWhitelist = keyList.filter(function (key) {
        return key.indexOf(APP_PREFIX)
      })
      // add current cache name to white list
      cacheWhitelist.push(CACHE_NAME)

      return Promise.all(keyList.map(function (key, i) {
        if (cacheWhitelist.indexOf(key) === -1) {
          console.debug('deleting cache : ' + keyList[i] )
          return caches.delete(keyList[i])
        }
      }))
    })
  )
})

function clearCache(){
    caches.keys().then(function(cacheNames) {
      return Promise.all(
        cacheNames.filter(function(cacheName) {
          // Return true if you want to remove this cache,
          // but remember that caches are shared across
          // the whole origin
        	console.log("SW","clearing",cacheName);
        	return true;
        }).map(function(cacheName) {
          return caches.delete(cacheName);
        })
      );
    })
}
