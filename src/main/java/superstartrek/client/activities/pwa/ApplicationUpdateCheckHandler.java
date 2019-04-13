package superstartrek.client.activities.pwa;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface ApplicationUpdateCheckHandler extends EventHandler{

	public static class ApplicationUpdateEvent extends GwtEvent<ApplicationUpdateCheckHandler>{

		public enum Status{checkFailed,appIsUpToDate,appIsOutdated, appCacheWasJustRefreshed};
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
				case checkFailed:handler.checkFailed();break;
				case appCacheWasJustRefreshed:handler.appMustReload();break;
			}
		}

	}
	default void newVersionAvailable(){};
	default void versionIsCurrent(){};
	default void checkFailed(){};
	default void appMustReload(){};
}
