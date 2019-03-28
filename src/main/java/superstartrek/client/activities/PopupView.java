package superstartrek.client.activities;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class PopupView<T extends Activity> extends BaseView<T>{

	protected PopupPanel popupPanel;
	protected HTMLPanel htmlPanel;
	
	protected PopupView(Presenter<T> presenter) {
		super(presenter);
	}
	
	protected HTMLPanel getHtmlPanel() {
		return htmlPanel;
	}

	protected abstract String getContentForHtmlPanel();
	
	@Override
	protected Widget createWidgetImplementation() {
		popupPanel = new PopupPanel(true, true);
		popupPanel.setGlassEnabled(true);
		popupPanel.setGlassStyleName("glasspanel");
		htmlPanel = new HTMLPanel(getContentForHtmlPanel());
		popupPanel.add(htmlPanel);
		return new FlowPanel();
	}
	
	@Override
	public void show() {
		popupPanel.show();
	}
	
	@Override
	public void hide() {
		popupPanel.hide();
	}

}
