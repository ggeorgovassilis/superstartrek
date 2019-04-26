package superstartrek.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {

	@Source("superstartrek/public/screens/install-app-prompt.html")
	public TextResource appInstallPrompt();

	@Source("superstartrek/public/screens/update-app-prompt.html")
	public TextResource updateAppPrompt();

	@Source("superstartrek/public/screens/intro.html")
	public TextResource introScreen();

	@Source("superstartrek/public/screens/computer.html")
	public TextResource computerScreen();

	@Source("superstartrek/public/screens/manual.html")
	public TextResource manualScreen();

	@Source("superstartrek/public/screens/sector-context-menu.html")
	public TextResource sectorContextMenu();

	@Source("superstartrek/public/screens/sector-scan.html")
	public TextResource sectorScanScreen();

	@Source("superstartrek/public/screens/lrs.html")
	public TextResource lrsScreen();

	@Source("superstartrek/public/screens/status-report.html")
	public TextResource statusReport();

	@Source("superstartrek/public/screens/messages.html")
	public TextResource messages();

	@Source("superstartrek/public/screens/app-menu.html")
	public TextResource appMenu();

}