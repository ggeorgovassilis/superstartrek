package superstartrek;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import superstartrek.client.Application;
import superstartrek.client.activities.settings.SettingsScreen;
import superstartrek.client.activities.settings.SettingsPresenter;

public class TestSettingsPresenter extends BaseTest{

	SettingsPresenter presenter;
	SettingsScreen view;
	
	@Before
	public void setup() {
		application = mock(Application.class);
		view = mock(SettingsScreen.class);
		presenter = new SettingsPresenter();
		presenter.setView(view);
	}
	
	@Test
	public void test_change_scale() {
		when(browser.getLocallyStoredValue(Application.UI_SCALE_KEY)).thenReturn("small");
		presenter.onActivityChanged("settings");
		verify(view).show();
		verify(view).selectUIScale("small");
	}

	@Test
	public void test_change_theme() {
		when(browser.getLocallyStoredValue(Application.UI_THEME_KEY)).thenReturn("high-contrast");
		presenter.onActivityChanged("settings");
		verify(view).show();
		verify(view).selectTheme("high-contrast");
	}

	@Test
	public void test_change_navigation_element_alignment() {
		when(browser.getLocallyStoredValue(Application.NAV_ELEMENT_ALIGNMENT_PREFERENCE)).thenReturn("default");
		presenter.onActivityChanged("settings");
		verify(view).show();
		verify(view).selectNavigationAlignment("default");
	}

}
