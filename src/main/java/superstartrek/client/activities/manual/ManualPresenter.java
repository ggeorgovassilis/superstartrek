package superstartrek.client.activities.manual;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class ManualPresenter extends BasePresenter<ManualScreen> implements ValueChangeHandler<String>{

	public ManualPresenter(Application application) {
		super(application);
		application.addHistoryListener(this);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if ("manual".equals(event.getValue())) {
			view.show();
		} else
			view.hide();
	}

}
