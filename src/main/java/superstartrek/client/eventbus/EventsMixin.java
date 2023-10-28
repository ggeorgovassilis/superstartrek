package superstartrek.client.eventbus;

import superstartrek.client.Application;

public interface EventsMixin {

	default EventBus getEvents() {
		return Application.get().eventBus;
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

}
