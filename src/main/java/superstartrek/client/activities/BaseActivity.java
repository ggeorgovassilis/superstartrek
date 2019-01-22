package superstartrek.client.activities;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.Application;

public abstract class BaseActivity extends Composite implements Activity{

	protected final Presenter presenter;
	protected HTMLPanel panel;
	
	public void finishUiConstruction() {
	}
	
	protected HTMLPanel createPanel() {
		return new HTMLPanel("");
	}
	
	protected BaseActivity(Presenter presenter) {
		this.presenter = presenter;
		panel = createPanel();
		initWidget(panel);
		presenter.setScreen(this);
	}
	
	public void show() {
		setVisible(true);
	}
	
	public void hide() {
		setVisible(false);
	}
	
}
