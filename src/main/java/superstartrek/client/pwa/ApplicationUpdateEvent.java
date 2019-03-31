package superstartrek.client.pwa;

import com.google.gwt.event.shared.GwtEvent;

public class ApplicationUpdateEvent extends GwtEvent<ApplicationUpdateCheckHandler>{

	public enum Status{checkFailed,appIsUpToDate,appIsOutdated};
	public final static Type<ApplicationUpdateCheckHandler> TYPE = new Type<>();
	public final Status status;
	
	public ApplicationUpdateEvent(Status status) {
		this.status = status;
	}
	
	@Override
	public Type<ApplicationUpdateCheckHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ApplicationUpdateCheckHandler handler) {
		switch(status) {
			case appIsOutdated:handler.newVersionAvailable(); break;
			case appIsUpToDate:handler.versionIsCurrent();break;
			case checkFailed:handler.checkFailed();
		}
	}

}
