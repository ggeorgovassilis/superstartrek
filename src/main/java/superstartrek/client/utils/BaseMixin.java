package superstartrek.client.utils;

import superstartrek.client.Application;
import superstartrek.client.eventbus.Event;
import superstartrek.client.eventbus.EventBus;
import superstartrek.client.eventbus.EventCallback;
import superstartrek.client.eventbus.EventHandler;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.StarMap;
import superstartrek.client.vessels.Enterprise;

public interface BaseMixin {
	
	default Application getApplication() {
		return Application.get();
	}

	default EventBus getEvents() {
		return getApplication().eventBus;
	}

	default <T extends EventHandler> void fireEvent(Event<T> type, EventCallback<T> callback) {
		getEvents().fireEvent(type, callback);
	}

	//TODO: all invocations I could find are addHandler(Event, this). Can the "handler" parameter
	//be omitted?
	default <T extends EventHandler> void addHandler(Event<T> type, T handler) {
		getEvents().addHandler(type, handler);
	}

	default <T extends EventHandler> void removeHandler(Event<T> type, T handler) {
		getEvents().removeHandler(type, handler);
	}

	default <T extends EventHandler> void removeHandler(T handler) {
		getEvents().removeHandler(handler);
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
