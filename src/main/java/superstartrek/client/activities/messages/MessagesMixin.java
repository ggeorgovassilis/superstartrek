package superstartrek.client.activities.messages;

import superstartrek.client.Application;

public interface MessagesMixin {

	default void message(String message, String category) {
		Application.get().message(message, category);
	}

	default void message(String message) {
		Application.get().message(message);
	}

}
