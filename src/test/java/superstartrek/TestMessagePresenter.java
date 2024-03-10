package superstartrek;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.activities.messages.MessagesPresenter;
import superstartrek.client.activities.messages.MessagesView;

public class TestMessagePresenter extends BaseTest{

	MessagesPresenter presenter;
	MessagesView view;
	
	@Before
	public void setup() {
		presenter = new MessagesPresenter();
		view = mock(MessagesView.class);
		presenter.setView(view);
	}
	
	@Test
	public void test_messagePosted() {
		presenter.messagePosted("test message", "info");
		verify(view).showMessage("test message", "info");
		verify(view).show();
	}
	
	public void test_hideMessages() {
		presenter.hideMessages();
		verify(view).hide(any());
	}
}
