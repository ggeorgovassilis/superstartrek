var APP_PREFIX = 'sst'     // Identifier for this app (this needs to be
							// consistent across every cache update)
var VERSION = '1'              // Version of the off-line cache (change this
								// value everytime you want to update cache)
var CACHE_NAME = APP_PREFIX + VERSION;

// inside service worker script
self.addEventListener('error', function(e) {
  console.error(e.filename, e.lineno, e.colno, e.message);
});

self.addEventListener('fetch', function (e) {
  e.respondWith(caches.match(e.request)['catch'](function(){
	  console.log("SW error");
  }).then(function (response) {
	  if (response && !response.ok){
		  var init = { "status" : 500 , "statusText" : "offline" };
		  console.log("SW returning error");
		  return new Response("",init);
	  }
      if (response) { // if cache is available, respond with cache
        console.debug('HIT',e.request.url);
        return response;
      }
      // if there are no cache, try fetching request
      console.debug('MISS',e.request.url);
      return fetch(e.request)['catch'](function(e){
    	  console.log("SW error 2 ", e)
		  var init = { "status" : 500 , "statusText" : "offline" };
		  console.log("SW returning error");
		  return new Response("",init);
      });

      // You can omit if/else for console.debug & put one line below like this
		// too.
      // return request || fetch(e.request)
    }))
});

// Cache resources
self.addEventListener('install', function (e) {
	  console.log("SW install");
  self.skipWaiting();
});

self.addEventListener('activate', function (event) {
	  console.log("SW activated");
});

self.addEventListener('message', function(event){
    console.log("SW Received Message: " + event.data);
});