package superstartrek.client.activities.messages;

import superstartrek.client.Application;

public interface MessagesMixin {
	
	private Application getApplication() {
		return Application.get();
	}

	default void message(String message, String category) {
		getApplication().message(message, category);
	}

	default void message(String message) {
		getApplication().message(message);
	}

}
