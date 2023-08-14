package superstartrek.client.activities.manual;

import superstartrek.client.Application;
import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.bus.Events;

public class ManualPresenter extends BasePresenter<ManualScreen> implements ActivityChangedHandler{

	public ManualPresenter(Application application) {
		super(application);
		addHandler(Events.ACTIVITY_CHANGED, this);
	}

	@Override
	public void onActivityChanged(String activity) {
		if ("manual".equals(activity)) {
			view.show();
		} else
			view.hide();
	}

}
