package superstartrek.client.activities.glasspanel;

import com.google.gwt.event.shared.GwtEvent;

public class GlassPanelEvent extends GwtEvent<GlassPanelHandler> {

	public static Type<GlassPanelHandler> TYPE = new Type<GlassPanelHandler>();

	public enum Action {
		show, hide, click
	};

	protected final Action action;

	public GlassPanelEvent(Action action) {
		this.action = action;
	}

	@Override
	public Type<GlassPanelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GlassPanelHandler handler) {
		if (action == Action.show)
			handler.glassPanelShown();
		else if (action == Action.hide)
			handler.glassPanelHidden();
		else if (action == Action.click)
			handler.glassPanelClicked();
	}

}
