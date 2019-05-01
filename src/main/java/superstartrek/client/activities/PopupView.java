package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.model.Constants;
import superstartrek.client.utils.HtmlWidget;
import superstartrek.client.utils.Timer;

@SuppressWarnings("rawtypes")
public abstract class PopupView<P extends PopupViewPresenter> extends BaseView<P> implements IPopupView<P> {

	boolean isInTransition = false;
	
	protected PopupView(P presenter) {
		super(presenter);
	}
	
	protected abstract String getContentForHtmlPanel();
	
	@Override
	protected Widget createWidgetImplementation() {
		return new HtmlWidget(DOM.createDiv(), getContentForHtmlPanel());
	}
	
	@Override
	public void decorateWidget() {
		super.decorateWidget();
		getElement().setAttribute("tabindex", "1");
		addStyleName("PopupView");
		addDomHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					event.preventDefault();
					event.stopPropagation();
					presenter.userWantsToDismissPopup();
				}
			}
		}, KeyDownEvent.getType());
		Event.sinkEvents(getElement(), Event.ONKEYDOWN | Event.ONCLICK);
		hide();
		RootPanel.get().add(this);
	}
	
	protected void showGlassPanel() {
		Element glassPanel = DOM.getElementById("glasspanel");
		Event.setEventListener(glassPanel, new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				event.preventDefault();
				event.stopPropagation();
				presenter.userWantsToDismissPopup();
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
		return super.isVisible();
	}
	
	@Override
	public void show() {
		if (isVisible())
			return;
		showGlassPanel();
		super.show();
		//deferred command (0ms) doesn't work reliably with FF.
		Timer.postpone(new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				addStyleName("slidein");
				//focus popup so that ESC key can hide it (otherwise key handler won't fire).
				//focus needs to be delayed to after animation is done to avoid animation lag
				//focus makes no sense if keyboard not present
				if (presenter.getApplication().browserAPI.hasKeyboard())
				Timer.postpone(new RepeatingCommand() {
					
					@Override
					public boolean execute() {
						getElement().focus();
						return false;
					}
				}, Constants.ANIMATION_DURATION_MS);
				return false;
			}
		}, 16);
	}
	
	@Override
	public void hide(ScheduledCommand callback) {
		if (!isVisible())
			return;
		hideGlassPanel();
		removeStyleName("slidein");
		Timer.postpone(new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				PopupView.super.hide();
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
