package superstartrek;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import superstartrek.client.activities.appmenu.AppMenuPresenter;
import superstartrek.client.activities.appmenu.IAppMenuView;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.Setting;

import static org.junit.Assert.*;

public class TestAppMenuPresenter extends BaseTest {

	AppMenuPresenter presenter;
	IAppMenuView view;

	@Before
	public void setup() {
		view = mock(IAppMenuView.class);
		presenter = new AppMenuPresenter();
		presenter.setView(view);
	}

	@Test
	public void test_onMenuItemClicked_autoaim() {

		when(view.isVisible()).thenReturn(true);

		assertTrue(application.starMap.enterprise.getAutoAim().getBooleanValue());
		presenter.onMenuItemClicked("cmd_autoaim");
		assertFalse(application.starMap.enterprise.getAutoAim().isOperational());
		verify(view).hide();
		verify(view).setMenuEntryEnabled("cmd_autoaim", false);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_onMenuItemClicked_restart() {

		when(view.isVisible()).thenReturn(true);
		presenter.onMenuItemClicked("cmd_restart");
		verify(view).hide();
		verify(application.browserAPI).confirm(any(String.class), any(Callback.class));
	}

	@Test
	public void test_onMenuItemClicked_evasiveManeuvers() {

		when(view.isVisible()).thenReturn(true);

		Enterprise enterprise = application.starMap.enterprise;
		Setting evasiveManeuvers = enterprise.getEvasiveManeuvers();
		assertFalse(evasiveManeuvers.getBooleanValue());
		presenter.onMenuItemClicked("cmd_evasive_maneuvers");
		assertTrue(evasiveManeuvers.isOperational());
		verify(view).hide();
		verify(view).setMenuEntryEnabled("cmd_evasive_maneuvers", true);
		
	}

}
