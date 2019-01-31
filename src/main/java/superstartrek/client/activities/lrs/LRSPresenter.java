package superstartrek.client.activities.lrs;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

public class LRSPresenter extends BasePresenter<LRSActivity> implements LRSHandler{

	public LRSPresenter(Application application) {
		super(application);
		application.events.addHandler(LRSEvent.TYPE, this);
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if ("longrangescan".equals(event.getValue()))
					lrsShown();
				else
					lrsHidden();
			}
		});
	}
	
	public void updateLrsView() {
		StarMap map = application.starMap;
		for (int y = 0; y<8;y++)
		for (int x = 0; x<8;x++) {
			Quadrant q = map.getQuadrant(x, y);
			String text = "";
			String css = "";
			if (!q.getKlingons().isEmpty()) {
				css+=" has-klingons";
				text+="K";
			} else text+=" ";
			if (!q.getStarBases().isEmpty()) {
				css+=" has-starbase";
				text+="!";
			} else text+=" ";
			text+=q.getStars().size();
			
			((LRSScreen)getView()).updateQuadrant(x, y, text, css);
		}	
			
	}

	@Override
	public void lrsShown() {
		updateLrsView();
		getView().show();
	}

	@Override
	public void lrsHidden() {
		getView().hide();
	}

	@Override
	public void quadrantSelected() {
	}

}
