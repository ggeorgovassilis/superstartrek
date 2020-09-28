package superstartrek.client.utils;

import superstartrek.client.Application;
import superstartrek.client.bus.EventHandler;
import superstartrek.client.bus.Event;
import superstartrek.client.bus.EventBus;
import superstartrek.client.bus.EventCallback;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

public interface BaseMixin {
	
	default Application getApplication() {
		return Application.get();
	}

	default EventBus getEvents() {
		return getApplication().eventBus;
	}

	default void message(String message, String category) {
		getApplication().message(message, category);
	}

	default void message(String message) {
		getApplication().message(message);
	}

	default <T extends EventHandler> void postponeEvent(Event<T> type, EventCallback<T> callback) {
		getEvents().postponeEvent(type, callback);
	}

	default <T extends EventHandler> void fireEvent(Event<T> type, EventCallback<T> callback) {
		getEvents().fireEvent(type, callback);
	}

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
