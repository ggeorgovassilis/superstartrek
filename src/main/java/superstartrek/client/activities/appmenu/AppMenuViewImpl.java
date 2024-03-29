package superstartrek.client.activities.appmenu;

import com.google.gwt.dom.client.Element;

import superstartrek.client.activities.popup.PopupViewImpl;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.CSS;

public class AppMenuViewImpl extends PopupViewImpl<AppMenuPresenter> implements AppMenuView{

	public AppMenuViewImpl(AppMenuPresenter presenter) {
		super(presenter);
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
