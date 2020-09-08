package superstartrek;

import org.junit.Before;
import org.junit.Test;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

import static org.mockito.Mockito.*;

import superstartrek.client.Application;
import superstartrek.client.activities.settings.ISettingsScreen;
import superstartrek.client.activities.settings.SettingsPresenter;

public class TestSettingsPresenter extends BaseTest{

	SettingsPresenter presenter;
	ISettingsScreen view;
	
	@Before
	public void setup() {
		view = mock(ISettingsScreen.class);
		presenter = new SettingsPresenter(application);
		presenter.setView(view);
	}
	
	@Test
	public void test_change_scale() {
		when(browser.getLocallyStoredValue(Application.UI_SCALE_KEY)).thenReturn("small");
		ValueChangeEvent<String> event = new ValueChangeEvent<String>("settings") {
			
		};
		presenter.onValueChange(event);
		verify(view).show();
		verify(view).selectUIScale("small");
	}

	@Test
	public void test_change_theme() {
		when(browser.getLocallyStoredValue(Application.UI_THEME_KEY)).thenReturn("high-contrast");
		ValueChangeEvent<String> event = new ValueChangeEvent<String>("settings") {
			
		};
		presenter.onValueChange(event);
		verify(view).show();
		verify(view).selectTheme("high-contrast");
	}

}
