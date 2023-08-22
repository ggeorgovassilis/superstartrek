package superstartrek.client.activities.appmenu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import superstartrek.client.activities.PopupView;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.CSS;
import superstartrek.client.utils.Strings;

public class AppMenuView extends PopupView<AppMenuPresenter> implements ClickHandler, IAppMenuView{

	public AppMenuView(AppMenuPresenter presenter) {
		super(presenter);
	}

	@Override
	protected void decorateWidget(ScreenTemplates templates, Element element) {
		super.decorateWidget(templates, element);
		addDomHandler(this, ClickEvent.getType());
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
