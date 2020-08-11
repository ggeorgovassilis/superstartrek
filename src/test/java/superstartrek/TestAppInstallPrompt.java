package superstartrek;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import superstartrek.client.activities.pwa.AppInstallPromptPresenter;
import superstartrek.client.activities.pwa.IAppInstallPromptView;
import superstartrek.client.activities.pwa.PWA;
import superstartrek.client.utils.BrowserAPI;
import static org.junit.Assert.*;

public class TestAppInstallPrompt extends BaseTest{

	AppInstallPromptPresenter presenter;
	IAppInstallPromptView view;
	
	@Before
	public void setup() {
		presenter = new AppInstallPromptPresenter(application);
		view = mock(IAppInstallPromptView.class);
		application.browserAPI = mock(BrowserAPI.class);
		presenter.setView(view);
	}
	
	@Test
	public void test_didUserForbidInstallation_false() {
		when(application.browserAPI.getCookie("neverinstall")).thenReturn("");
		assertFalse(presenter.didUserForbidInstallation());
	}

	@Test
	public void test_didUserForbidInstallation_true() {
		when(application.browserAPI.getCookie("neverinstall")).thenReturn("never");
		assertTrue(presenter.didUserForbidInstallation());
	}
	
	@Test
	public void test_rememberThatUserForbidsInstallation() {
		presenter.rememberThatUserForbidsInstallation();
		verify(application.browserAPI).setCookie("neverinstall", "never");
	}

	@Test
	public void test_showInstallPrompt() {
		when(application.browserAPI.getCookie("neverinstall")).thenReturn("");
		presenter.showInstallPrompt();
		verify(view).show();
	}

	@Test
	public void test_showInstallPrompt_when_user_forbids() {
		when(application.browserAPI.getCookie("neverinstall")).thenReturn("never");
		presenter.showInstallPrompt();
		verify(view, never()).show();
	}

	@Test
	public void test_userWantsDoDismissPopup() {
		presenter.userWantsToDismissPopup();
		verify(view).hide();
	}

	@Test
	public void test_userDoesntWantToInstallAppEver() {
		presenter.userDoesntWantToInstallAppEver();
		verify(view).hide();
		verify(application.browserAPI).setCookie("neverinstall", "never");
	}

	@Test
	public void test_userClickedInstallButton() {
		application.pwa = mock(PWA.class);
		presenter.userClickedInstallButton();
		verify(view).hide();
		verify(application.pwa).installApplication();
	}

}
