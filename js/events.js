var Events={
	START_GAME:"START_GAME",
	GAME_OVER:"GAME_OVER",
	SETTINGS_CHANGED:"SETTINGS_CHANGED",
	WEAPON_FIRED:"WEAPON_FIRED",
	ENTERPRISE_DAMAGED:"ENTERPRISE_DAMAGED",
	KLINGON_MOVED:"KLINGON_MOVED",
	KLINGON_DAMAGED:"KLINGON_DAMAGED",
	KLINGON_DESTROYED:"KLINGON_DESTROYED",
	ENTERPRISE_MOVED:"ENTERPRISE_MOVED",
	ENTERPRISE_WARPED:"ENTERPRISE_WARPED",
	ENTERPRISE_REPAIRED:"ENTERPRISE_REPAIRED",
	trigger:function(event,arg){
		if (!arg)
			arg={};
		console.log("event",event,arg);
		if (!Events[event])
			throw "Unknown event "+event;
		$(window).trigger(event,[arg]);
	},
	on:function(event,callback){
		if (!Events[event])
			throw "Unknown event "+event;
		if (!callback)
			throw "No callback given";
		if (!isFunction(callback))
			throw "Callback is not a function";
		$(window).on(event,function(e,arg){
			console.log("event callback",e,callback,arg);
			callback(arg);
		});
	},
	hashchange:function(){
		window.scrollTo(0, 0);
		Controller.onClickedActivityToken(window.location.hash.substring("#".length));
	}
};

$(window).on('hashchange', Events.hashchange);