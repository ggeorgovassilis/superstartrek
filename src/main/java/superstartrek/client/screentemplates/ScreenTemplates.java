package superstartrek.client.screentemplates;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface ScreenTemplates extends ClientBundle {

	@Source("install-app-prompt.html")
	public TextResource appInstallPrompt();

	@Source("update-app-prompt.html")
	public TextResource updateAppPrompt();

	@Source("intro.html")
	public TextResource introScreen();

	@Source("computer.html")
	public TextResource computerScreen();

	@Source("manual.html")
	public TextResource manualScreen();

	@Source("sector-context-menu.html")
	public TextResource sectorContextMenu();

	@Source("lrs.html")
	public TextResource lrsScreen();

	@Source("status-report.html")
	public TextResource statusReport();

	@Source("messages.html")
	public TextResource messages();

	@Source("app-menu.html")
	public TextResource appMenu();

	@Source("settings.html")
	public TextResource settingsScreen();

	@Source("credits.html")
	public TextResource creditsScreen();

}