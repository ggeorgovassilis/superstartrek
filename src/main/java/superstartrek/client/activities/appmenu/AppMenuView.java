package superstartrek.client.activities.appmenu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;

import superstartrek.client.activities.CSS;
import superstartrek.client.activities.PopupView;
import superstartrek.client.utils.Strings;

public class AppMenuView extends PopupView<AppMenuPresenter> implements ClickHandler, IAppMenuView{

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
		return presenter.getApplication().getScreenTemplates().appMenu().getText();
	}
	
	@Override
	public void setMenuEntryEnabled(String cmd, boolean enabled) {
		Element e = DOM.getElementById(cmd);
		CSS.setEnabled(e, enabled);
	}
	
	@Override
	public void hide() {
		super.hide();
		presenter.onMenuHidden();
	}

	@Override
	public void onClick(ClickEvent event) {
		EventTarget target = event.getNativeEvent().getEventTarget();
		Element e = target.cast();
		String id = e.getId();
		if (Strings.isEmpty(id))
			return;
		presenter.onMenuItemClicked(id);
	}

}
