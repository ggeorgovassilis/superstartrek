var Test={
	errors:[],
	tests:[],
	messages:[],
	outstandingScripts:0,
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
	executeTests(){
		console.log("Running tests");
		for (var i=0;i<Test.tests.length;i++) try{
			Test.errors=[];
			Test.messages=[];
			Test.tests[i]();
			if (Test.errors.length>0)
				throw "Test failed";
		}catch(e){
			console.error(e);
		}
	},
	declare:function(f){
		Test.tests.push(f);
		try{
		}catch(e){
			console.error(e);
		}
	},
	assertEuals:function(a,b,message){
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
		message:function(m){
			Test.messages.push(m);
			return IO;
		},
		nothing:function(){}
};

IO.then = IO;

Test.loadScript("../../js/jquery.1.9.2.js");
Test.loadScript("../../js/constants.js");
Test.loadScript("unittest.js");
