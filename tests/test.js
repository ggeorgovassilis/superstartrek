var Test={
	errors:[],
	tests:[],
	outstandingScripts:0,
	waypoints:[],
	logerror:function(e){
		console.error("test error",e);
		Test.errors.push(e);
	},
	previousOnError:window.onerror,
	loadScript:function(src){
		var script = document.createElement('script');
		Test.outstandingScripts++;
		script.onload=function(){
			window.setTimeout(function(){
				Test.outstandingScripts--;
				if (!Test.outstandingScripts)
					Test.executeTests();
			},100);
		};
		document.getElementsByTagName("head")[0].append(script);
		script.src = src;
	},
	setupLogger:function(){
		$("head").append($("<style type='text/css'>#logger{font-family:sans-serif};\n.running-test{float:left;}\n.test-ok{color:green}\n.test-failure{color:red}</style>"));
		return $("<div id=logger></div>");
	},
	executeTests(){
		var logger = Test.setupLogger();
		$(document.body).append(logger);
		console.log("Running tests");
		for (var i=0;i<Test.tests.length;i++) try{
			var test = Test.tests[i];
			Test.errors=[];
			IO.messages=[];
			Test.waypoints=[];
			logger.append($("<div class='running-test'>Running test "+test.name+"</div>"));
			console.log("Running test",test.name);
			test.code();
			if (Test.errors.length>0)
				throw "Test failed";
			logger.append($("<div class=test-ok>OK</div>"));
		}catch(e){
			logger.append($("<div class=test-failure>FAIL</div>"));
			console.error(e);
		}
	},
	logWaypoint:function(waypoint){
		Test.waypoints.push(waypoint);
	},
	assertWaypoint:function(waypoint){
		var found = false;
		for (var i=0;i<Test.waypoints.length;i++)
			found|=Test.waypoints[i]==waypoint;
		if (!found)
			throw "Waypoing "+waypoint+" not encountered";
		console.log("Assert waypoint",waypoint);
	},
	assertNoWaypoint:function(waypoint){
		var found = false;
		for (var i=0;i<Test.waypoints.length;i++)
			found|=Test.waypoints[i]==waypoint;
		if (found)
			throw "Waypoing "+waypoint+" not encountered";
	},
	declare:function(name,f){
		if (!(typeof name === "string"))
			throw "Name is not a string";
		if (!(typeof f === "function"))
			throw "F is not a function";
		Test.tests.push({name:name, code:f});
		try{
		}catch(e){
			console.error(e);
		}
	},
	assertEquals:function(a,b,message){
		if (a!=b)
			throw "message";
	}
};

window.onerror = function (msg, url, line) {
    Test.logerror("Caught[via window.onerror]: '" + msg + "' from " + url + ":" + line);
    if (Test.previousOnError)
    	try{
    		Test.previousOnError(msg,url,line);
    	}catch(e){
    	}
    return true;
};

window.addEventListener('error', function (evt) {
    Test.logerror("Caught[via 'error' event]:  '" + evt.message + "' from " + evt.filename + ":" + evt.lineno);
    console.error(evt); // has srcElement / target / etc
    evt.preventDefault();
});

var IO={
		messages:[],
		assertMessage:function(message){
			if (!IO.messages.includes(message))
				throw "Message "+message+" was not logged";
		},
		message:function(m){
			IO.messages.push(m);
			return IO;
		},
		nothing:function(){}
};

IO.then = IO;

Test.loadScript("../../js/jquery.1.9.2.js");
Test.loadScript("../../js/constants.js");
Test.loadScript("unittest.js");
