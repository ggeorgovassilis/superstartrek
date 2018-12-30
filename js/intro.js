var Intro={
	visible:false,
	show:function(){
		Intro.visible = true;
		Tools.gotoScreen("intro");
		var button = $("#cmd_leaveIntro");
	},
	hide:function(){
		Intro.visible = false;
	}
};
