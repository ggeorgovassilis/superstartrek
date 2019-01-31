package superstartrek.client.activities.lrs;

import com.google.gwt.event.shared.GwtEvent;

import superstartrek.client.model.Quadrant;

public class LRSEvent extends GwtEvent<LRSHandler> {

	public static Type<LRSHandler> TYPE = new Type<LRSHandler>();

	public enum Action {
		show, hide, quadrantSelected
	};

	protected final Action action;
	protected Quadrant quadrant;

	public LRSEvent(Action action) {
		if (action==null)
			throw new IllegalArgumentException("Action cannot be null");
		this.action = action;
		quadrant = null;
	}

	public LRSEvent(Action action, Quadrant quadrant) {
		this(action);
		if (quadrant == null)
			throw new IllegalArgumentException("Quadrant cannot be null");
		this.quadrant = quadrant;
	}


	@Override
	public Type<LRSHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LRSHandler handler) {
		if (action == Action.show)
			handler.lrsShown();
		else if (action == Action.hide)
			handler.lrsHidden();
		else if (action == Action.quadrantSelected)
			handler.quadrantSelected();
	}

}
