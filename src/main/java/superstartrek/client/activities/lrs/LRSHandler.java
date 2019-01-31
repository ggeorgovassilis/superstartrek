package superstartrek.client.activities.lrs;

import com.google.gwt.event.shared.EventHandler;

public interface LRSHandler extends EventHandler{

	void lrsShown();
	void lrsHidden();
	void quadrantSelected();

}
