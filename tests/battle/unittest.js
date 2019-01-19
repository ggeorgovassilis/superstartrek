var Controller={};
var Enterprise={};
Test.loadScript("../../js/battle.js");

Test.declare("Fire full phasers", function(){
	Enterprise.firePhasersAt=function(x,y){
		Test.logWaypoint("Enterprise.firePhasersAt");
		Test.assertEquals(x,1,"x");
		Test.assertEquals(y,2,"x");
	};
	
	Enterprise.phaserPower = Constants.ENTERPRISE_MAX_PHASER_POWER;
	Controller.sector={x:1,y:2};
	Controller.firePhasers();

	Test.assertWaypoint("Enterprise.firePhasersAt");
});

Test.declare("Fire degraded phasers", function(){
	Enterprise.firePhasersAt=function(x,y){
		Test.logWaypoint("Enterprise.firePhasersAt");
		Test.assertEquals(x,1,"x");
		Test.assertEquals(y,2,"x");
	};
	
	Enterprise.phaserPower = Constants.ENTERPRISE_MAX_PHASER_POWER*0.1;
	Controller.sector={x:1,y:2};
	Controller.firePhasers();
	
	IO.assertMessage("Phasers array is offline");
});
