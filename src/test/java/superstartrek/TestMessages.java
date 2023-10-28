package superstartrek;

import org.junit.Test;

import superstartrek.client.eventbus.Events;
import superstartrek.client.activities.messages.MessageHandler;
import static org.mockito.Mockito.*;

public class TestMessages extends BaseTest {

	@Test
	public void test() {
		MessageHandler mock = mock(MessageHandler.class);
		application.eventBus.addHandler(Events.MESSAGE_POSTED, mock);
		application.message("test message", "test category");
		verify(mock).messagePosted("test message", "test category");
	}
}
