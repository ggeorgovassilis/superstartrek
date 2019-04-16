package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class BaseView<A extends Activity> extends Composite implements View<A>, IBaseView<A>{

	protected final Presenter<A> presenter;
	
	protected abstract Widget createWidgetImplementation();

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Presenter<A>> T getPresenter() {
		return (T)presenter;
	}
	
	protected void decorateWidget() {
	}
	
	protected BaseView(Presenter<A> presenter) {
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
