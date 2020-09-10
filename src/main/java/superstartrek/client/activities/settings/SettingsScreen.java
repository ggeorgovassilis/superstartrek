package superstartrek.client.activities.settings;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.utils.HtmlWidget;


public class SettingsScreen extends BaseScreen<SettingsPresenter> implements ISettingsScreen {

	Element eSmall;
	Element eMedium;
	Element eLarge;
	Element eXL;
	Element eCheckForUpdates;
	Element eDefaultTheme;
	Element eHighContrastTheme;
	Set<Element> uiScales = new HashSet<Element>();
	Set<Element> uiThemes = new HashSet<Element>();

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
		addDomHandler((event) -> handleChange(event), ChangeEvent.getType());
		addDomHandler((event) -> handleClick(event), ClickEvent.getType());
		uiScales.add(eSmall);
		uiScales.add(eMedium);
		uiScales.add(eLarge);
		uiScales.add(eXL);
		uiThemes.add(eDefaultTheme);
		uiThemes.add(eHighContrastTheme);
	}

	protected void handleChange(DomEvent<?> event) {
		NativeEvent ne = event.getNativeEvent();
		Element e = ne.getEventTarget().cast();
		String value = e.getAttribute("value");
		
		if (uiScales.contains(e)) {
			presenter.onUIScaleSettingClicked(value);
		};
		if (uiThemes.contains(e)) {
			presenter.onUIThemeSettingClicked(value);
		}
	}

	protected void handleClick(DomEvent<?> event) {
		NativeEvent ne = event.getNativeEvent();
		Element e = ne.getEventTarget().cast();
		
		if (e == eCheckForUpdates) {
			presenter.onCheckForUpdatesButtonClicked();
		}
	}

	
	@Override
	protected Widget createWidgetImplementation() {
		return new HtmlWidget(DOM.createDiv(), presenter.getApplication().getResources().settingsScreen().getText());
	}

	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}

	@Override
	public void selectUIScale(String scale) {
		for (Element e:uiScales)
			e.setPropertyString("checked", scale.equals(e.getAttribute("value"))?"true":null);
	}

	@Override
	public void selectTheme(String theme) {
		for (Element e:uiThemes)
			e.setPropertyString("checked", theme.equals(e.getAttribute("value"))?"true":null);
	}
}
