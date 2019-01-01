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
	message:function(text){
		IO.content.append("<div>"+text+"</div>");
		Tools.addPageCss("messages-visible");
		IO.messages.find(".single").focus();
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
