package superstartrek.client.activities;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
	protected Element glassPanel;
	
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
		htmlPanel.addHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				event.preventDefault();
				event.stopPropagation();
				hide();
			}
		}, KeyDownEvent.getType());
		return new FlowPanel();
	}
	
	@Override
	public void show() {
		if (htmlPanel.isAttached())
			return;
		RootPanel.get().add(htmlPanel);
		glassPanel = DOM.createDiv();
		glassPanel.addClassName("glasspanel");
		RootPanel.get().getElement().appendChild(glassPanel);
		Element keySink = glassPanel;
		Event.setEventListener(keySink, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				event.preventDefault();
				event.stopPropagation();
				hide();
			}
		});
		Event.sinkEvents(keySink, Event.ONCLICK | Event.ONMOUSEDOWN | Event.ONTOUCHSTART | Event.ONKEYDOWN | Event.ONKEYPRESS);
		htmlPanel.getElement().focus();
	}
	
	@Override
	public void hide() {
		if (!htmlPanel.isAttached())
			return;
		glassPanel.removeFromParent();
		glassPanel = null;
		RootPanel.get().remove(htmlPanel);
	}

}
