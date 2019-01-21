package superstartrek.client.activities;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.Application;

public abstract class BaseScreen extends Widget implements Screen{

	protected final Presenter presenter;
	
	protected BaseScreen(Presenter presenter, Element e) {
		setElement(e);
		this.presenter = presenter;
		presenter.setScreen(this);
	}
	
	public void show() {
		setVisible(true);
	}
	
	public void hide() {
		setVisible(false);
	}
	
}
