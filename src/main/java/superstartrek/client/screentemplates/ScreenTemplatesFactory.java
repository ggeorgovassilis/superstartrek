package superstartrek.client.screentemplates;

import java.util.HashMap;
import java.util.Map;

import superstartrek.client.Application;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.pwa.PWA;

public class ScreenTemplatesFactory implements ScreenTemplates {

	Map<String,String> templates = new HashMap<String, String>();
	int outstandingLoads = 0;
	
	void loadTemplate(String key, Callback<Void> callback) {
		PWA pwa = Application.get().pwa;
		pwa.getFileContent(key, (content) -> {
			templates.put(key, content);
			outstandingLoads--;
			if (outstandingLoads == 0)
				callback.onSuccess(null);
		});
		
	}

	public void initialise(Callback<Void> callback) {
		outstandingLoads = 13; //number of loadTemplate invocations below
		loadTemplate(ScreenTemplates.appMenu, callback);
		loadTemplate(ScreenTemplates.computer, callback);
		loadTemplate(ScreenTemplates.credits, callback);
		loadTemplate(ScreenTemplates.highscores, callback);
		loadTemplate(ScreenTemplates.installAppPrompt, callback);
		loadTemplate(ScreenTemplates.intro, callback);
		loadTemplate(ScreenTemplates.lrs, callback);
		loadTemplate(ScreenTemplates.manual, callback);
		loadTemplate(ScreenTemplates.messages, callback);
		loadTemplate(ScreenTemplates.sectorContextMenu, callback);
		loadTemplate(ScreenTemplates.settings, callback);
		loadTemplate(ScreenTemplates.statusReport, callback);
		loadTemplate(ScreenTemplates.updateAppPrompt, callback);
	}

	@Override
	public String getTemplateFor(String key) {
		return templates.get(key);
	}

}
