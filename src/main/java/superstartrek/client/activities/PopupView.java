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

import superstartrek.client.utils.Timer;

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
		glassPanel.removeClassName("fadeout");
		CSS.addClassDeferred(glassPanel, "fadein");
		glassPanel.getStyle().setDisplay(Display.INITIAL);
	}
	
	protected void hideGlassPanel() {
		Element glassPanel = DOM.getElementById("glasspanel");
		Event.setEventListener(glassPanel, null);
		Event.sinkEvents(glassPanel, 0);
		glassPanel.removeClassName("fadein");
		glassPanel.addClassName("fadeout");
		Timer.postpone(new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				glassPanel.getStyle().setDisplay(Display.NONE);
				return false;
			}
		}, 300);
	}
	
	@Override
	public boolean isVisible() {
		return htmlPanel.isVisible();
	}
	
	@Override
	public void show() {
		if (isVisible())
			return;
		htmlPanel.removeStyleName("slideout");
		htmlPanel.setVisible(true);
		CSS.addClassDeferred(htmlPanel.getElement(), "slidein");
		showGlassPanel();
	}
	
	@Override
	public void hide() {
		GWT.log("hide");
		hide(null);
	}
	
	@Override
	public void hide(ScheduledCommand callback) {
		if (!htmlPanel.isVisible())
			return;
		CSS.removeClassDeferred(htmlPanel.getElement(), "slidein");
		CSS.addClassDeferred(htmlPanel.getElement(), "slideout");
		hideGlassPanel();
		Timer.postpone(new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				htmlPanel.removeStyleName("slidein");
				htmlPanel.setVisible(false);
				if (callback!=null)
					callback.execute();
				return false;
			}
		}, 300);
	}

}
