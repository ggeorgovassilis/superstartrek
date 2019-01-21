package superstartrek.client.activities.computer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.Presenter;

public class ComputerScreen extends BaseScreen{

	public ComputerScreen(Presenter presenter) {
		super(presenter);
		getElement().setInnerHTML(Resources.INSTANCE.computerScreen().getText());
	}

}
