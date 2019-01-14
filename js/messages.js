var IO={
	currentCallback:null,
	gameIsOver:false,
	onKeyPressed:function(e){
		IO.onOkClicked();
	},
	messages:$("#messages"),
	content:$("#messages .content"),
	endTurn:function(){
		IO.currentCallback = Controller.endTurn;
		return this;
	},
	gameOver:function(message){
		IO.message(message.message);
		IO.call(Controller.gameOver);
		IO.gameIsOver=true;
	},
	call:function(callback){
		if (IO.gameIsOver) //not accepting callbacks if game is over
			return;
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
	message:function(text,type){
		var css = "entry "+(type?type:"");
		if (Array.isArray(text))
			text.foreach(function(t){IO.content.append("<li class=\""+css+"\">"+t+"</li>");});
		else
			IO.content.append("<li class=\""+css+"\">"+text+"</li>");
		Tools.addPageCss("messages-visible");
		var height = $("#messages").height();
		var wheight = $(window).height();
		console.log(height,wheight);
		var offsetTop = Math.max((wheight-height)/2,0);
		$("#messages").offset({top:offsetTop,left:0});
		$("#hidemessagesbutton")[0].focus(); //native js faster than jquery
		Tools.centerScreen();
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
		if (IO.currentCallback)
			IO.currentCallback();
	}
};
IO.then = IO;
Events.on(Events.GAME_OVER,IO.gameOver);
