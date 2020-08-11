package superstartrek;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.activities.messages.IMessagesView;
import superstartrek.client.activities.messages.MessagesPresenter;
import static org.mockito.Mockito.*;

public class TestMessagesPresenter extends BaseTest{

	MessagesPresenter presenter;
	IMessagesView view;
	
	@Before
	public void setup() {
		presenter = new MessagesPresenter(application);
		view = mock(IMessagesView.class);
		presenter.setView(view);
	}
	
	@Test
	public void test() {
		presenter.message("test message", "test category");
		verify(view).showMessage("test message", "test category");
	}
}
