package superstartrek.client.screentemplates;

import com.georgovassilis.gwthtmlresource.client.HtmlResource;
import com.google.gwt.resources.client.ClientBundle;

public interface ScreenTemplates extends ClientBundle {

	@Source("install-app-prompt.html")
	public HtmlResource appInstallPrompt();

	@Source("update-app-prompt.html")
	public HtmlResource updateAppPrompt();

	@Source("intro.html")
	public HtmlResource introScreen();

	@Source("computer.html")
	public HtmlResource computerScreen();

	@Source("manual.html")
	public HtmlResource manualScreen();

	@Source("sector-context-menu.html")
	public HtmlResource sectorContextMenu();

	@Source("lrs.html")
	public HtmlResource lrsScreen();

	@Source("status-report.html")
	public HtmlResource statusReport();

	@Source("messages.html")
	public HtmlResource messages();

	@Source("app-menu.html")
	public HtmlResource appMenu();

	@Source("settings.html")
	public HtmlResource settingsScreen();

	@Source("credits.html")
	public HtmlResource creditsScreen();

}