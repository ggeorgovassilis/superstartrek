package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.bus.Events;

@SuppressWarnings("rawtypes")
public abstract class BaseView<P extends Presenter> extends Composite implements View<P>, ScreenResizeHandler {

	protected final P presenter;

	protected abstract Widget createWidgetImplementation();

	@SuppressWarnings("unchecked")
	protected <T> T getWidgetAs() {
		return (T) getWidget();
	}

	protected void decorateWidget() {
	}

	/**
	 * Indicates whether the component aligns by HTML/CSS or requires "assistance"
	 * by computation
	 * 
	 * @return False if computational assistance (mostly bottom-alignment on mobile)
	 *         is required
	 */
	protected boolean alignsOnItsOwn() {
		return true;
	}

	@SuppressWarnings("unchecked")
	protected BaseView(P presenter) {
		this.presenter = presenter;
		Widget widgetImpl = createWidgetImplementation();
		initWidget(widgetImpl);
		decorateWidget();
		presenter.setView(this);
		if (!alignsOnItsOwn())
			presenter.getApplication().eventBus.addHandler(Events.SCREEN_RESIZES, this);
	}

	// aligns screens to the bottom of the screen for easier interactions with the
	// thumb
	// TODO: there should be a feature detection or setting to determine whether
	// bottom alignment is required
	protected void layoutForEasyHandlingOnMobileDevices() {
		int contentHeight = getOffsetHeight();
		int windowHeight = presenter.getApplication().browserAPI.getWindowHeightPx();
		int margin = Math.max(0, windowHeight - contentHeight);
		getElement().getStyle().setMarginTop(margin, Unit.PX);
	}

	@Override
	public void show() {
		setVisible(true);
		if (!alignsOnItsOwn())
			layoutForEasyHandlingOnMobileDevices();
	}

	@Override
	public void hide() {
		setVisible(false);
	}

	@Override
	public void hide(ScheduledCommand callback) {
		hide();
		callback.execute();
	}

	@Override
	public void onScreenResize() {
		//layouting depends on computing the space existing components take. Because of a previous layout, this would
		//result in a much larger height than actually needed, creating unnecessary scroll.
		//First Reset top margin to 0, then allowing reflow, _then_ computing the required space
		getElement().getStyle().setMarginTop(0, Unit.PX);
	}

	// only called if handler has been registered in constructor
	@Override
	public void onAfterScreenResize(int widthPx, int heightPx) {
		layoutForEasyHandlingOnMobileDevices();
	}

}
