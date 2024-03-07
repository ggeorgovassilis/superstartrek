package superstartrek.client.activities.settings;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.eventbus.Events;
import superstartrek.client.screentemplates.ScreenTemplates;

public class SettingsScreenImpl extends BaseScreen<SettingsPresenter> implements SettingsScreen {

	Element eSmall;
	Element eMedium;
	Element eLarge;
	Element eXL;
	Element eCheckForUpdates;
	Element eDefaultTheme;
	Element eHighContrastTheme;
	Element eNavDefault;
	Element eNavBottom;
	Set<Element> uiScales = new HashSet<Element>();
	Set<Element> uiThemes = new HashSet<Element>();
	Set<Element> uiNav = new HashSet<Element>();

	public SettingsScreenImpl(SettingsPresenter p) {
		super(p);
		addStyleName("settings-screen");
		eSmall = getElementById("ui-scaling-small");
		eMedium = getElementById("ui-scaling-medium");
		eLarge = getElementById("ui-scaling-large");
		eXL = getElementById("ui-scaling-xl");
		eCheckForUpdates = getElementById("cmd_check_for_updates_2");
		eDefaultTheme = getElementById("ui-theme-default");
		eHighContrastTheme = getElementById("ui-theme-highcontrast");
		eNavDefault = getElementById("nav-default");
		eNavBottom = getElementById("nav-bottom");
		addDomHandler((event) -> handleChange(event), ChangeEvent.getType());
		uiScales.add(eSmall);
		uiScales.add(eMedium);
		uiScales.add(eLarge);
		uiScales.add(eXL);
		uiThemes.add(eDefaultTheme);
		uiThemes.add(eHighContrastTheme);
		uiNav.add(eNavDefault);
		uiNav.add(eNavBottom);
	}

	protected void handleChange(DomEvent<?> event) {
		NativeEvent ne = event.getNativeEvent();
		Element e = ne.getEventTarget().cast();
		String value = e.getAttribute("value");

		if (uiScales.contains(e)) {
			presenter.onUIScaleSettingClicked(value);
		} else
		if (uiThemes.contains(e)) {
			presenter.onUIThemeSettingClicked(value);
		} else
		if (uiNav.contains(e)) {
			presenter.onNavigationAlignmentChanged(value);
		}
	}

	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		element.setInnerHTML(templates.settingsScreen().getText());
		presenter.getApplication().eventBus.addHandler(Events.INTERACTION, tag->{
			if ("cmd_check_for_updates_2".equals(tag)) 
				presenter.onCheckForUpdatesButtonClicked();
		});
	}
	
	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}
	
	void selectElementInSet(Set<Element> set, String value) {
			set.forEach(e->e.setPropertyString("checked", value.equals(e.getAttribute("value")) ? "true" : null));
	}

	@Override
	public void selectUIScale(String scale) {
		selectElementInSet(uiScales, scale);
	}

	@Override
	public void selectTheme(String theme) {
		selectElementInSet(uiThemes, theme);
	}

	@Override
	public void selectNavigationAlignment(String alignment) {
		selectElementInSet(uiNav, alignment);
	}

	public void showAppVersion(String version) {
		getElementById("app-version").setInnerText("app version " + version);
	}

	@Override
	public void disableUpdateCheckButton() {
		eCheckForUpdates.removeClassName("disabled");
		eCheckForUpdates.addClassName("disabled");
	}

	@Override
	public void enableUpdateCheckButton() {
		eCheckForUpdates.removeClassName("disabled");
	}

}
