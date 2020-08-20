package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("rawtypes")
public abstract class BaseView<P extends Presenter> extends Composite implements View<P>{

	protected final P presenter;
	
	protected abstract Widget createWidgetImplementation();
	
	@SuppressWarnings("unchecked")
	protected <T> T getWidgetAs() {
		return (T)getWidget();
	}

	protected void decorateWidget() {
	}
	
	protected boolean isAbsolutelyPositioned() {
		return false;
	}

	@SuppressWarnings("unchecked")
	protected BaseView(P presenter) {
		this.presenter = presenter;
		Widget widgetImpl = createWidgetImplementation();
		initWidget(widgetImpl);
		decorateWidget();
		presenter.setView(this);
	}
	

	// aligns screens to the bottom of the screen for easier interactions with the thumb
	//TODO: there should be a feature detection or setting to determine whether bottom alignment is required
	protected void layoutForEasyHandlingOnMobileDevices() {
		if (!isAbsolutelyPositioned()) {
			int contentHeight = getOffsetHeight();
			int windowHeight = Window.getClientHeight();
			int margin=Math.max(0,windowHeight-contentHeight);
			getElement().getStyle().setMarginTop(margin, Unit.PX);
		}
	}
	
	@Override
	public void show() {
		setVisible(true);
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
	
}
