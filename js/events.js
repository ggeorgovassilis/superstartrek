var Events={
	START_GAME:"START_GAME",
	GAME_OVER:"GAME_OVER",
	TURN_STARTS:"TURN_STARTS",
	SETTINGS_CHANGED:"SETTINGS_CHANGED",
	WEAPON_FIRED:"WEAPON_FIRED",
	ENTERPRISE_DAMAGED:"ENTERPRISE_DAMAGED",
	KLINGON_MOVED:"KLINGON_MOVED",
	KLINGON_DAMAGED:"KLINGON_DAMAGED",
	KLINGON_DESTROYED:"KLINGON_DESTROYED",
	ENTERPRISE_MOVED:"ENTERPRISE_MOVED",
	ENTERPRISE_WARPED:"ENTERPRISE_WARPED",
	ENTERPRISE_REPAIRED:"ENTERPRISE_REPAIRED",
	ENTERPRISE_ENERGY_CHANGED:"ENTERPRISE_ENERGY_CHANGED",
	SECTOR_SELECTED:"SECTOR_SELECTED",
	QUADRANT_SELECTED:"QUADRANT_SELECTED",
	LRS:"LRS",
	STATUS_REPORT:"STATUS_REPORT",
	trigger:function(event,arg){
		if (!arg)
			arg={};
		if (!Events[event])
			throw "Unknown event "+event;
		$window.trigger(event,[arg]);
	},
	on:function(event,callback){
		if (!Events[event])
			throw "Unknown event "+event;
		if (!callback)
			throw "No callback given";
		if (!isFunction(callback))
			throw "Callback is not a function";
		$(window).on(event,function(e,arg){
			callback(arg);
		});
	},
	hashchange:function(){
		var screen = window.location.hash.substring("#".length);
		Tools.showScreen(screen);
		Controller.onClickedActivityToken(screen);
	}
};

$(window).on('hashchange', Events.hashchange);