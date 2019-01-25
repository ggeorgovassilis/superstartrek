package superstartrek.client.activities.computer.srs;

import com.google.gwt.core.client.GWT;

import superstartrek.client.Application;
import superstartrek.client.activities.BasePresenter;
import superstartrek.client.activities.computer.TurnStartedEvent;
import superstartrek.client.activities.computer.TurnStartedHandler;
import superstartrek.client.activities.loading.GameStartedEvent;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

public class SRSPresenter extends BasePresenter<SRSActivity> implements TurnStartedHandler {

	public SRSPresenter(Application application) {
		super(application);
		application.events.addHandler(TurnStartedEvent.TYPE, this);
	}

	@Override
	public void onTurnStarted(TurnStartedEvent evt) {
		SRSView view = (SRSView) getView();
		StarMap map = application.starMap;
		Quadrant q0 = map.enterprise.getQuadrant();
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++) {
				String symbol = "";
				String css = "";
				int qx = q0.getX()+x-1;
				int qy = q0.getY()+y-1;
				if (qx>=0 && qy>=0 && qx<8 && qy<8) {
					Quadrant q = map.getQuadrant(x, y);
					if (!q.getKlingons().isEmpty()) {
						symbol += "K";
						css += "has-klingon ";
					} else
						symbol += " ";
					if (!q.getStarBases().isEmpty()) {
						symbol += "!";
						css += "has-starbase ";
					} else
						symbol += " ";
					if (!q.getStars().isEmpty()) {
						symbol += q.getStars().size();
					}
				} else symbol = "0";
				view.updateCell(x, y, symbol, css);
			}
	}

}
