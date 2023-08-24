package superstartrek.client.activities.manual;

import superstartrek.client.activities.ActivityChangedHandler;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.eventbus.Events;

public class ManualPresenter extends BasePresenter<ManualScreen> implements ActivityChangedHandler{

	public ManualPresenter() {
		addHandler(Events.ACTIVITY_CHANGED);
	}

	@Override
	public void onActivityChanged(String activity) {
		if ("manual".equals(activity)) {
			view.show();
		} else
			view.hide();
	}

}
