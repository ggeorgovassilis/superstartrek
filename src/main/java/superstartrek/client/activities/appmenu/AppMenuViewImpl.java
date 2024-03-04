package superstartrek.client.activities.appmenu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import superstartrek.client.activities.PopupViewImpl;
import superstartrek.client.eventbus.Events;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.CSS;
import superstartrek.client.utils.Strings;

public class AppMenuViewImpl extends PopupViewImpl<AppMenuPresenter> implements AppMenuView{

	public AppMenuViewImpl(AppMenuPresenter presenter) {
		super(presenter);
	}

	@Override
	protected void decorateWidget(ScreenTemplates templates, Element element) {
		super.decorateWidget(templates, element);
		presenter.getApplication().eventBus.addHandler(Events.INTERACTION, tag->presenter.onMenuItemClicked(tag));
	}

	@Override
	protected String getContentForHtmlPanel(ScreenTemplates templates) {
		return templates.appMenu().getText();
	}
	
	@Override
	public void setMenuEntryEnabled(String cmd, boolean enabled) {
		Element e = getElementById(cmd);
		CSS.setEnabled(e, enabled);
	}
	
	@Override
	public void hide() {
		super.hide();
		presenter.onMenuHidden();
	}


}
