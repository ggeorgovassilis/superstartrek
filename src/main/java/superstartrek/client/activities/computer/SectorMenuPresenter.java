package superstartrek.client.activities.computer;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class SectorMenuPresenter extends BasePresenter implements SectorSelectedHandler{

	public SectorMenuPresenter(Application application) {
		super(application);
		GWT.log("SectorMenuPresenter()");
		application.events.addHandler(SectorSelectedEvent.TYPE, this);
	}

	@Override
	public void onSectorSelected(SectorSelectedEvent event) {
		GWT.log("sector selected");
		((SectorMenuActivity)getScreen()).setLocation(event.screenX, event.screenY);
		((SectorMenuActivity)getScreen()).show();
	}

}
