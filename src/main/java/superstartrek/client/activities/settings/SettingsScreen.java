package superstartrek.client.activities.settings;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.utils.HtmlWidget;

public class SettingsScreen extends BaseScreen<SettingsPresenter> implements ISettingsScreen {

	Element eSmall;
	Element eMedium;
	Element eLarge;

	public SettingsScreen(SettingsPresenter p) {
		super(p);
		addStyleName("settings-screen");
		eSmall = DOM.getElementById("ui-scaling-small");
		eMedium = DOM.getElementById("ui-scaling-medium");
		eLarge = DOM.getElementById("ui-scaling-large");
		addDomHandler((event) -> handleClick(event), ChangeEvent.getType());
	}

	protected void handleClick(DomEvent<?> event) {
		NativeEvent ne = event.getNativeEvent();
		Element e = ne.getEventTarget().cast();
		String value = "medium";
		if (eSmall == e)
			value = "small";
		if (eMedium == e)
			value = "medium";
		if (eLarge == e)
			value = "large";
		presenter.onUIScaleSettingClicked(value);
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
		Element e = null;
		switch (scale) {
		case "small":
			e = eSmall; break;
		case "large":
			e = eLarge; break;
		case "medium":
		default:
			e = eMedium; break;
		}
		e.setPropertyBoolean("checked", true);
	}
}
