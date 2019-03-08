package superstartrek.client.activities;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

public abstract class BaseView<A extends Activity> extends Composite implements View<A>, IBaseView<A>{

	protected final Presenter<A> presenter;
	protected HTMLPanel panel;
	
	@Override
	public <T extends Presenter<A>> T getPresenter() {
		return (T)presenter;
	}
	
	@Override
	public void finishUiConstruction() {
	}
	
	protected HTMLPanel createPanel() {
		return new HTMLPanel("");
	}
	
	protected BaseView(Presenter<A> presenter) {
		this.presenter = presenter;
		panel = createPanel();
		initWidget(panel);
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
	public boolean isVisible() {
		return super.isVisible();
	}
	
}
