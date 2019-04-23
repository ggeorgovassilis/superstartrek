package superstartrek.client.activities.manual;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;

public class ManualPresenter extends BasePresenter<ManualScreen> implements ValueChangeHandler<String>{

	public ManualPresenter(Application application) {
		super(application);
		History.addValueChangeHandler(this);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if ("manual".equals(event.getValue())) {
			getView().show();
		} else
			getView().hide();
	}

}
