package superstartrek.client.screentemplates;

public interface ScreenTemplates {

	String appMenu = "templates/app-menu.html";
	String computer = "templates/computer.html";
	String credits = "templates/credits.html";
	String highscores = "templates/highscores.html";
	String installAppPrompt = "templates/install-app-prompt.html";
	String intro = "templates/intro.html";
	String lrs = "templates/lrs.html";
	String manual = "templates/manual.html";
	String messages = "templates/messages.html";
	String sectorContextMenu = "templates/sector-context-menu.html";
	String settings = "templates/settings.html";
	String statusReport = "templates/status-report.html";
	String updateAppPrompt = "templates/update-app-prompt.html";

	String getTemplateFor(String key);

}