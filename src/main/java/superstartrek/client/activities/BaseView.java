package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import superstartrek.client.Application;
import superstartrek.client.eventbus.Events;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.HtmlWidget;

@SuppressWarnings("rawtypes")
public abstract class BaseView<P extends Presenter> extends HtmlWidget implements View<P>, ScreenResizeHandler {

	protected final P presenter;

	protected void createWidgetImplementation() {
		setElement(Document.get().createDivElement());
	}

	protected void decorateWidget(ScreenTemplates templates, Element element) {
	}
	
	protected Application application() {
		return Application.get();
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
		createWidgetImplementation();
		decorateWidget(application().getScreenTemplates(), getElement());
		presenter.setView(this);
		if (!alignsOnItsOwn())
			application().eventBus.addHandler(Events.SCREEN_RESIZES, this);
	}

	// aligns screens to the bottom of the screen for easier interactions with the
	// thumb
	// TODO: there should be a feature detection or setting to determine whether
	// bottom alignment is required
	protected void layoutForEasyHandlingOnMobileDevices() {
		String pref = application().getNavigationElementAlignmentPreference();
		if ("bottom".equals(pref)) {
			int contentHeight = getOffsetHeight();
			int windowHeight = application().browserAPI.getWindowHeightPx();
			int margin = Math.max(0, windowHeight - contentHeight);
			getElement().getStyle().setMarginTop(margin, Unit.PX);
		}
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
		// layouting depends on computing the space existing components occupy. Because of
		// a previous layout, this would
		// result in a much larger height than actually needed, creating unnecessary
		// scroll.
		// First Reset top margin to 0, then allowing reflow, _then_ computing the
		// required space
		getElement().getStyle().setMarginTop(0, Unit.PX);
	}

	// only called if handler has been registered in constructor
	@Override
	public void onAfterScreenResize(int widthPx, int heightPx) {
		layoutForEasyHandlingOnMobileDevices();
	}
	
	protected Element getElementById(String id) {
		return Document.get().getElementById(id);
	}
	
}
