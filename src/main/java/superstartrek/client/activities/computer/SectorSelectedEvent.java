package superstartrek.client.activities.computer;

import com.google.gwt.event.shared.GwtEvent;

public class SectorSelectedEvent extends GwtEvent<SectorSelectedHandler>{

	public static Type<SectorSelectedHandler> TYPE = new Type<SectorSelectedHandler>();
	public final int x;
	public final int y;
	public final int screenX;
	public final int screenY;
	
	public SectorSelectedEvent(int x, int y, int screenX, int screenY) {
		this.x = x;
		this.y = y;
		this.screenX = screenX;
		this.screenY = screenY;
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

