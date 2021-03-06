package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import superstartrek.client.model.Constants;
import superstartrek.client.utils.Timer;

@SuppressWarnings("rawtypes")
public abstract class PopupView<P extends PopupViewPresenter> extends BaseView<P> implements IPopupView<P> {

	//overriding default visibility handling because we implemented it with CSS transitions which are not reflected
	//in DOM properties
	boolean visible = false;
	
	protected PopupView(P presenter) {
		super(presenter);
	}

	protected abstract String getContentForHtmlPanel();

	@Override
	public void decorateWidget() {
		getElement().setInnerHTML(getContentForHtmlPanel());
		addStyleName("PopupView");
		addDomHandler((event) -> {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
				event.preventDefault();
				event.stopPropagation();
				presenter.userWantsToDismissPopup();
			}
		}, KeyDownEvent.getType());
		Event.sinkEvents(getElement(), Event.ONKEYDOWN | Event.ONCLICK);
		hide();
		presenter.getApplication().browserAPI.addToPage(this);
	}

	protected void showGlassPanel() {
		Element glassPanel = DOM.getElementById("glasspanel");
		Event.setEventListener(glassPanel, (event) -> {
			event.preventDefault();
			event.stopPropagation();
			presenter.userWantsToDismissPopup();
		});
		Event.sinkEvents(glassPanel, Event.ONCLICK | Event.ONMOUSEDOWN | Event.ONKEYDOWN | Event.ONKEYPRESS);
		glassPanel.addClassName("fadein");
	}

	protected void hideGlassPanel() {
		//Events are registered and unregistered instead of registered once
		//in the constructor because the same glass panel element singleton
		//is shared with all classes which extends PopupView: if each class
		//registered its own handler, they would all receive copies of the
		//same event.
		Element glassPanel = DOM.getElementById("glasspanel");
		Event.setEventListener(glassPanel, null);
		Event.sinkEvents(glassPanel, 0);
		glassPanel.removeClassName("fadein");
	}

	@Override
	public void show() {
		showGlassPanel();
		//super.show();
		// deferred command (0ms) doesn't work reliably with FF.
		addStyleName("slidein");
		// focus popup so that ESC key can hide it (otherwise key handler won't fire).
		// focus needs to be delayed to after animation is done to avoid animation lag
		// focus makes no sense if keyboard not present
		Timer.postpone(() -> animationIsDone(), Constants.POST_ANIMATION_DURATION_MS);
		visible = true;
	}

	protected void animationIsDone() {
	}
	
	@Override
	public void hide(ScheduledCommand callback) {
		hideGlassPanel();
		removeStyleName("slidein");
		visible = false;
		if (callback != null)
			Timer.postpone(() -> callback.execute(), Constants.ANIMATION_DURATION_MS);
	}

	@Override
	public void hide() {
		hide(null);
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}

}
