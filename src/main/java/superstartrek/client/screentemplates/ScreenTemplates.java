package superstartrek.client.screentemplates;

public interface ScreenTemplates {
	
	enum TemplateNames{
		
		appMenu("templates/app-menu.html"),
		computer("templates/computer.html"),
		credits("templates/credits.html"),
		highscores("templates/highscores.html"),
		installAppPrompt("templates/install-app-prompt.html"),
		intro("templates/intro.html"),
		lrs("templates/lrs.html"),
		manual("templates/manual.html"),
		messages("templates/messages.html"),
		sectorContextMenu("templates/sector-context-menu.html"),
		settings("templates/settings.html"),
		statusReport("templates/status-report.html"),
		updateAppPrompt("templates/update-app-prompt.html");
		
		public final String path;
		TemplateNames(String path){
			this.path = path;
		}
	}
	//Important: after adding a new template, handle it in ScreenTemplatesFactory#initialise

	String getTemplateFor(TemplateNames key);

}