package superstartrek.client.activities.pwa;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface ApplicationLifecycleHandler extends EventHandler{

	public static class ApplicationLifecycleEvent extends GwtEvent<ApplicationLifecycleHandler>{

		public enum Status{showInstallPrompt,checkFailed,appIsUpToDate,appIsOutdated, appCacheWasJustRefreshed, informingOfInstalledVersion, filesCached};
		public final static Type<ApplicationLifecycleHandler> TYPE = new Type<>();
		public final Status status;
		public final String currentVersion;
		public final String latestAvailableVersion;
		
		public ApplicationLifecycleEvent(Status status, String curentVersion, String latestAvailableVersion) {
			this.status = status;
			this.currentVersion = curentVersion;
			this.latestAvailableVersion = latestAvailableVersion;
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
				case informingOfInstalledVersion:handler.installedAppVersionIs(currentVersion);break;
				case filesCached:handler.filesAreCached();break;
				case showInstallPrompt:handler.showInstallPrompt();break;
			}
		}

	}
	default void newVersionAvailable(){};
	default void versionIsCurrent(){};
	default void checkFailed(){};
	default void appMustReload(){};
	default void installedAppVersionIs(String version) {};
	default void filesAreCached() {};
	default void showInstallPrompt() {};
}
