var IO={
	currentCallback:null,
	onKeyPressed:function(e){
		IO.hide();
	},
	messages:$("#messages"),
	content:$("#messages .content"),
	endTurn:function(){
		IO.currentCallback = Controller.endTurn;
		return this;
	},
	call:function(callback){
		console.log("call",callback);
		if (!callback)
			throw "Callback not defined";
		if (!isFunction(callback))
			throw "Callback not a function";
		IO.currentCallback = callback;
		return IO;
	},
	SRS:function(){
		return IO.call(Controller.showComputerScreen);
	},
	nothing:function(){
		IO.currentCallback = function(){};
		return IO;
	},
	message:function(text){
		console.log("message",text);
		IO.content.append("<div>"+text+"</div>");
		Tools.removePageCss("messages-visible");
		Tools.addPageCss("messages-visible");
		IO.messages.find(".single").focus();
		repositionWindowScroll();
		return IO;
	},
	hide:function(){
		IO.content.empty();
		Tools.removePageCss("messages-visible");
		return IO;
	},
	isMessageShown:function(){
		return Tools.hasPageCss("messages-visible");
	},
	onOkClicked:function(){
		IO.hide();
		console.log("IO.onOkClicked",IO.currentCallback);
		if (IO.currentCallback)
			IO.currentCallback();
	}
};
IO.then = IO;
$("#messages .single").fastClick(IO.onOkClicked);
