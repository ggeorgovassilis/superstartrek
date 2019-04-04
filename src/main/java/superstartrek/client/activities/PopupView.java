package superstartrek.client.activities;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.AnimationType;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class PopupView<T extends Activity> extends BaseView<T>{

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
		htmlPanel = new HTMLPanel(getContentForHtmlPanel());
		htmlPanel.addStyleName("PopupView");
		htmlPanel.setVisible(false);
		htmlPanel.addHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				event.preventDefault();
				event.stopPropagation();
				hide();
			}
		}, KeyDownEvent.getType());
		FlowPanel fp = new FlowPanel();
		fp.setVisible(false);
		return fp;
	}
	
	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		RootPanel.get().add(getHtmlPanel());
	}
	
	protected void showGlassPanel() {
		Element glassPanel = DOM.getElementById("glasspanel");
		Event.setEventListener(glassPanel, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				event.preventDefault();
				event.stopPropagation();
				hide();
			}
		});
		Event.sinkEvents(glassPanel, Event.ONCLICK | Event.ONMOUSEDOWN | Event.ONKEYDOWN | Event.ONKEYPRESS);
		glassPanel.getStyle().setDisplay(Display.INITIAL);
	}
	
	protected void hideGlassPanel() {
		Element glassPanel = DOM.getElementById("glasspanel");
		glassPanel.getStyle().setDisplay(Display.NONE);
		Event.setEventListener(glassPanel, null);
		Event.sinkEvents(glassPanel, 0);
	}
	
	@Override
	public boolean isVisible() {
		return htmlPanel.isVisible();
	}
	
	@Override
	public void show() {
		if (isVisible())
			return;
		htmlPanel.setVisible(true);
		showGlassPanel();
	}
	
	@Override
	public void hide() {
		if (!htmlPanel.isVisible())
			return;
		hideGlassPanel();
		htmlPanel.setVisible(false);
	}

}
