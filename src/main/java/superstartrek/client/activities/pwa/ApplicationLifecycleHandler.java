package superstartrek.client.activities.pwa;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface ApplicationLifecycleHandler extends EventHandler{

	public static class ApplicationLifecycleEvent extends GwtEvent<ApplicationLifecycleHandler>{

		public enum Status{showInstallPrompt,checkFailed,appIsUpToDate,appIsOutdated, appCacheWasJustRefreshed, informingOfInstalledVersion};
		public final static Type<ApplicationLifecycleHandler> TYPE = new Type<>();
		public final Status status;
		public final String currentVersion;
		public final String latestAvailableVersion;
		public final String versionTimestamp;
		
		public ApplicationLifecycleEvent(Status status, String curentVersion, String versionTimestamp, String latestAvailableVersion) {
			this.status = status;
			this.currentVersion = curentVersion;
			this.latestAvailableVersion = latestAvailableVersion;
			this.versionTimestamp = versionTimestamp;
		}
		
		public ApplicationLifecycleEvent(Status status) {
			this(status, "", "", "");
		}
		
		@Override
		public Type<ApplicationLifecycleHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ApplicationLifecycleHandler handler) {
			switch(status) {
				case appIsOutdated:handler.newVersionAvailable(); break;
				case appIsUpToDate:handler.versionIsCurrent();break;
				case checkFailed:handler.checkFailed();break;
				case appCacheWasJustRefreshed:handler.appMustReload();break;
				case informingOfInstalledVersion:handler.installedAppVersionIs(currentVersion, versionTimestamp);break;
				case showInstallPrompt:handler.showInstallPrompt();break;
			}
		}

	}
	default void newVersionAvailable(){};
	default void versionIsCurrent(){};
	default void checkFailed(){};
	default void appMustReload(){};
	default void installedAppVersionIs(String version, String timestamp) {};
	default void showInstallPrompt() {};
}
