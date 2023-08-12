package superstartrek.client.activities.settings;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.DOM;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;

public class SettingsScreen extends BaseScreen<SettingsPresenter> implements ISettingsScreen {

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

	public SettingsScreen(SettingsPresenter p) {
		super(p);
		addStyleName("settings-screen");
		eSmall = DOM.getElementById("ui-scaling-small");
		eMedium = DOM.getElementById("ui-scaling-medium");
		eLarge = DOM.getElementById("ui-scaling-large");
		eXL = DOM.getElementById("ui-scaling-xl");
		eCheckForUpdates = DOM.getElementById("cmd_check_for_updates_2");
		eDefaultTheme = DOM.getElementById("ui-theme-default");
		eHighContrastTheme = DOM.getElementById("ui-theme-highcontrast");
		eNavDefault = DOM.getElementById("nav-default");
		eNavBottom = DOM.getElementById("nav-bottom");
		addDomHandler((event) -> handleChange(event), ChangeEvent.getType());
		addDomHandler((event) -> handleClick(event), ClickEvent.getType());
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

	protected void handleClick(DomEvent<?> event) {
		NativeEvent ne = event.getNativeEvent();
		Element e = ne.getEventTarget().cast();

		if (eCheckForUpdates.isOrHasChild(e)) {
			presenter.onCheckForUpdatesButtonClicked();
		}
	}

	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		element.setInnerHTML(templates.settingsScreen().getText());
	}
	
	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}
	
	void selectElementInSet(Set<Element> set, String value) {
		for (Element e : set)
			e.setPropertyString("checked", value.equals(e.getAttribute("value")) ? "true" : null);
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
		DOM.getElementById("app-version").setInnerText("app version " + version);
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
