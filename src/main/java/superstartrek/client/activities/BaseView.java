package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("rawtypes")
public abstract class BaseView<P extends Presenter> extends Composite implements IBaseView<P>{

	protected final P presenter;
	
	protected abstract Widget createWidgetImplementation();

	@Override
	public P getPresenter() {
		return presenter;
	}
	
	protected void decorateWidget() {
	}

	@SuppressWarnings("unchecked")
	protected BaseView(P presenter) {
		this.presenter = presenter;
		Widget widgetImpl = createWidgetImplementation();
		initWidget(widgetImpl);
		decorateWidget();
		presenter.setView(this);
	}
	
	@Override
	public void show() {
		setVisible(true);
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
