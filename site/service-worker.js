var APP_PREFIX = 'sst'     // Identifier for this app (this needs to be
							// consistent across every cache update)
var VERSION = '1'              // Version of the off-line cache (change this
								// value everytime you want to update cache)
var CACHE_NAME = APP_PREFIX + VERSION;

// inside service worker script
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
        if ((""+e.request.url).indexOf("refresh_cache")!=-1){
        	populateCache();
        }
        return fetch(e.request)
      }

      // You can omit if/else for console.debug & put one line below like this
		// too.
      // return request || fetch(e.request)
    }))
});

function populateCache(){
	return caches.open(CACHE_NAME).then(function(cache) {
	      return cache.addAll(
	        [
				"/superstartrek/site/", 
				"/superstartrek/site/index.html",
				"/superstartrek/site/sst.webmanifest",
				"/superstartrek/site/images/cancel.svg",
				"/superstartrek/site/images/bookmark.svg",
				"/superstartrek/site/images/communicator.svg", 
				"/superstartrek/site/images/federation_logo.svg",
				"/superstartrek/site/images/fire_at_will.svg", 
				"/superstartrek/site/images/hexagon_filled.svg",
				"/superstartrek/site/images/hexagon.svg", 
				"/superstartrek/site/images/icon192x192.png",
				"/superstartrek/site/images/icon512x512.png", 
				"/superstartrek/site/images/laser.svg",
				"/superstartrek/site/images/navigation.svg", 
				"/superstartrek/site/images/radar.svg",
				"/superstartrek/site/images/torpedo.svg",
				"/superstartrek/site/images/stars-background.gif", 
				"/superstartrek/site/images/hamburger-menu.svg",
				"/superstartrek/site/css/sst.css", 
				"/superstartrek/site/superstartrek.superstartrek.nocache.js",
				"/superstartrek/site/checksum.sha.md5"
	        ]
	      );
	    });
}

// Cache resources
self.addEventListener('install', function (e) {
// self.skipWaiting();
  e.waitUntil(populateCache());
});

self.addEventListener('activate', function (event) {
	  console.log("SW activated");
});