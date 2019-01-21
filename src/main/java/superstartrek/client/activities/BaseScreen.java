package superstartrek.client.activities;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.Application;

public abstract class BaseScreen extends Widget implements Screen{

	protected final Presenter presenter;
	
	protected Element createRootElement() {
		return DOM.createDiv();
	}
	
	protected BaseScreen(Presenter presenter) {
		setElement(createRootElement());
		this.presenter = presenter;
		presenter.setScreen(this);
		presenter.getApplication().page.add(this);
		hide();
	}
	
	public void show() {
		setVisible(true);
	}
	
	public void hide() {
		setVisible(false);
	}
	
}
