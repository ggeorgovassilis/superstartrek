package superstartrek.client.activities.computer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseActivity;
import superstartrek.client.activities.Presenter;

public class SectorMenuActivity extends BaseActivity{

	public SectorMenuActivity(Presenter presenter) {
		super(presenter);
		GWT.log("SectorMenuActivity()");
	}

	@Override
	protected HTMLPanel createPanel() {
		return new HTMLPanel(Resources.INSTANCE.sectorSelectionMenu().getText());
	}
	
	@Override
	public void finishUiConstruction() {
		GWT.log("SectorMenuActivity.finsihUiCosntruction");
		addStyleName("sectorselectionbar");
		presenter.getApplication().page.add(this);
		hide();
	}
	
	public void setLocation(int x, int y) {
		getElement().getStyle().setTop(y, Unit.PX);
		getElement().getStyle().setLeft(x, Unit.PX);
	}

}
