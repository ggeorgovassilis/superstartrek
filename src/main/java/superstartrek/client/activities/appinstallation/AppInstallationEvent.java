package superstartrek.client.activities.appinstallation;

import com.google.gwt.dom.client.NativeEvent;

public class AppInstallationEvent extends NativeEvent {

	protected AppInstallationEvent() {
	}

	//@formatter:off
	public final native void prompt() /*-{
		this.prompt();
		this.userChoice.then(function(choiceResult) {
			if (choiceResult.outcome === 'accepted') {
				console.log('User accepted the installation prompt');
			} else {
				console.log('User dismissed the installation prompt');
			}
		});
	}-*/;
	//@formatter:on

}
