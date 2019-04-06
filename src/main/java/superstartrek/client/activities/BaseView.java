package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class BaseView<A extends Activity> extends Composite implements View<A>, IBaseView<A>{

	protected final Presenter<A> presenter;
	protected Widget widgetImpl;
	
	protected Widget getWidgetImplementation() {
		return widgetImpl;
	}
	
	@Override
	public <T extends Presenter<A>> T getPresenter() {
		return (T)presenter;
	}
	
	@Override
	public void finishUiConstruction() {
	}
	
	protected Widget createWidgetImplementation() {
		return new HTMLPanel("");
	}
	
	protected BaseView(Presenter<A> presenter) {
		this.presenter = presenter;
		widgetImpl = createWidgetImplementation();
		initWidget(widgetImpl);
		presenter.setView(this);
		finishUiConstruction();
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
	
	@Override
	public boolean isVisible() {
		return super.isVisible();
	}
	
}
