var APP_PREFIX = 'sst'     // Identifier for this app (this needs to be consistent across every cache update)
var VERSION = '1'              // Version of the off-line cache (change this value everytime you want to update cache)
var CACHE_NAME = APP_PREFIX + VERSION;

//inside service worker script
self.addEventListener('error', function(e) {
  console.log(e.filename, e.lineno, e.colno, e.message);
});

self.addEventListener('fetch', function (e) {
  e.respondWith(caches.match(e.request).then(function (response) {
      if (response) { // if cache is available, respond with cache
        console.debug('HIT',e.request.url);
        return response;
      } else {       // if there are no cache, try fetching request
        console.debug('MISS',e.request.url);
        return fetch(e.request)
      }

      // You can omit if/else for console.debug & put one line below like this too.
      // return request || fetch(e.request)
    }))
});

// Cache resources
self.addEventListener('install', function (e) {
  console.log("listening for SW install");
  self.skipWaiting();
});

self.addEventListener('activate', function (e) {
	  console.log("listening for SW activate");
  e.waitUntil(clients.claim());
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
  );
});