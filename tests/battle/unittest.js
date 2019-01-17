var Controller={};
var Enterprise={};
Test.loadScript("../../js/battle.js");
Test.declare(function(){
	Enterprise.firePhasersAt=function(x,y){console.log(x,y)};
	Enterprise.phaserPower = Constants.ENTERPRISE_MAX_PHASER_POWER;
	Controller.sector={x:1,y:2};
	Controller.firePhasers();
});
