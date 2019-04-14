package superstartrek.client.activities.pwa;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface ApplicationUpdateCheckHandler extends EventHandler{

	public static class ApplicationUpdateEvent extends GwtEvent<ApplicationUpdateCheckHandler>{

		public enum Status{checkFailed,appIsUpToDate,appIsOutdated, appCacheWasJustRefreshed, informingOfInstalledVersion};
		public final static Type<ApplicationUpdateCheckHandler> TYPE = new Type<>();
		public final Status status;
		public final String currentVersion;
		public final String latestAvailableVersion;
		
		public ApplicationUpdateEvent(Status status, String curentVersion, String latestAvailableVersion) {
			this.status = status;
			this.currentVersion = curentVersion;
			this.latestAvailableVersion = latestAvailableVersion;
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
				case informingOfInstalledVersion:handler.installedAppVersionIs(currentVersion);break;
			}
		}

	}
	default void newVersionAvailable(){};
	default void versionIsCurrent(){};
	default void checkFailed(){};
	default void appMustReload(){};
	default void installedAppVersionIs(String version) {};
}
