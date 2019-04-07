package superstartrek.client.activities;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.model.Constants;
import superstartrek.client.utils.HtmlWidget;
import superstartrek.client.utils.Timer;

public abstract class PopupView<T extends Activity> extends BaseView<T>{

	protected HtmlWidget htmlPanel;
	
	protected PopupView(Presenter<T> presenter) {
		super(presenter);
	}
	
	protected HtmlWidget getHtmlPanel() {
		return htmlPanel;
	}

	protected abstract String getContentForHtmlPanel();
	
	@Override
	protected Widget createWidgetImplementation() {
		htmlPanel = new HtmlWidget(DOM.createDiv(), getContentForHtmlPanel());
		htmlPanel.addStyleName("PopupView");
		htmlPanel.addHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				event.preventDefault();
				event.stopPropagation();
				hide();
			}
		}, KeyDownEvent.getType());
		return htmlPanel;
	}
	
	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		RootPanel.get().add(this);
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
		CSS.addClassDeferred(glassPanel, "fadein");
		glassPanel.getStyle().setDisplay(Display.INITIAL);
	}
	
	protected void hideGlassPanel() {
		Element glassPanel = DOM.getElementById("glasspanel");
		Event.setEventListener(glassPanel, null);
		Event.sinkEvents(glassPanel, 0);
		glassPanel.removeClassName("fadein");
		Timer.postpone(new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				glassPanel.getStyle().setDisplay(Display.NONE);
				return false;
			}
		}, Constants.ANIMATION_DURATION_MS);
	}
	
	@Override
	public boolean isVisible() {
		return htmlPanel.getStyleName().contains("slidein");
	}
	
	@Override
	public void show() {
		if (isVisible())
			return;
		htmlPanel.addStyleName("slidein");
		showGlassPanel();
	}
	
	@Override
	public void hide(ScheduledCommand callback) {
		if (!isVisible())
			return;
		hideGlassPanel();
		htmlPanel.removeStyleName("slidein");
		Timer.postpone(new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				if (callback!=null)
					callback.execute();
				return false;
			}
		}, Constants.ANIMATION_DURATION_MS);
	}

	@Override
	public void hide() {
		hide(null);
	}

}
