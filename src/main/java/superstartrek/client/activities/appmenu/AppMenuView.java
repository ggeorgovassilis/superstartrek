package superstartrek.client.activities.appmenu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;

import superstartrek.client.activities.PopupView;
import superstartrek.client.utils.Strings;

public class AppMenuView extends PopupView<AppMenuPresenter> implements ClickHandler{

	public AppMenuView(AppMenuPresenter presenter) {
		super(presenter);
	}

	@Override
	public void decorateWidget() {
		super.decorateWidget();
		addDomHandler(this, ClickEvent.getType());
	}

	@Override
	protected String getContentForHtmlPanel() {
		return getPresenter().getApplication().getResources().appMenu().getText();
	}
	
	public void setMenuEntryEnabled(String cmd, boolean enabled) {
		Element e = DOM.getElementById(cmd);
		e.removeClassName("disabled");
		if (!enabled)
			e.addClassName("disabled");
	}
	
	@Override
	public void hide() {
		super.hide();
		getPresenter().onMenuHidden();
	}

	@Override
	public void onClick(ClickEvent event) {
		EventTarget target = event.getNativeEvent().getEventTarget();
		Element e = target.cast();
		if (Strings.isEmpty(e.getId()))
			return;
		String id = e.getId();
		presenter.onMenuItemClicked(id);
	}

}
