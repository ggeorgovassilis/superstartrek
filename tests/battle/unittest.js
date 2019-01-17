var Controller={};
var Enterprise={};
Test.loadScript("../../js/battle.js");
Test.declare(function(){
	Enterprise.firePhasersAt=function(){console.log(1)};
	Enterprise.phaserPower = Constants.ENTERPRISE_MAX_PHASER_POWER/4 - 1;
	Controller.firePhasers();
});
