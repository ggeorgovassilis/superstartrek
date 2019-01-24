package superstartrek.client.activities;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

public abstract class BaseView<A extends Activity> extends Composite implements View<A>{

	protected final Presenter<A> presenter;
	protected HTMLPanel panel;
	
	public Presenter<A> getPresenter() {
		return presenter;
	}
	
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
	
}
