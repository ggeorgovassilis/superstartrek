package superstartrek.client.utils;

import superstartrek.client.Application;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.StarMap;
import superstartrek.client.vessels.Enterprise;

public interface BaseMixin {
	
	default Application getApplication() {
		return Application.get();
	}

	default StarMap getStarMap() {
		return getApplication().starMap;
	}
	
	default Quadrant getActiveQuadrant() {
		return getApplication().getActiveQuadrant();
	}
	
	default Enterprise getEnterprise() {
		return getStarMap().enterprise;
	}
}
