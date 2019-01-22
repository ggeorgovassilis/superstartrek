package superstartrek.client.activities.computer;

import com.google.gwt.event.shared.GwtEvent;

public class SectorSelectedEvent extends GwtEvent<SectorSelectedHandler>{

	public static Type<SectorSelectedHandler> TYPE = new Type<SectorSelectedHandler>();
	public final int x;
	public final int y;
	
	public SectorSelectedEvent(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public Type<SectorSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SectorSelectedHandler handler) {
		handler.onSectorSelected(this);
	}

}

