package superstartrek.client.utils;

import superstartrek.client.Application;
import superstartrek.client.bus.BaseHandler;
import superstartrek.client.bus.Event;
import superstartrek.client.bus.EventBus;
import superstartrek.client.bus.EventCallback;
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
	
	default <T extends BaseHandler> void fireEvent(Event<T> type, EventCallback<T> callback) {
		getEvents().fireEvent(type, callback);
	}

	default <T extends BaseHandler> void addHandler(Event<T> type, T handler) {
		getEvents().addHandler(type, handler);
	}

	default <T extends BaseHandler> void removeHandler(Event<T> type, T handler) {
		getEvents().removeHandler(type, handler);
	}

	default <T extends BaseHandler> void removeHandler(T handler) {
		getEvents().removeHandler(handler);
	}
	
	default StarMap getStarMap() {
		return getApplication().starMap;
	}
}
