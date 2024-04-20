package superstartrek.client.screentemplates;

import java.util.HashMap;
import java.util.Map;

import superstartrek.client.Application;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.pwa.PWA;

public class ScreenTemplatesFactory implements ScreenTemplates {

	Map<String, String> templates = new HashMap<String, String>();
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
		String[] templatesToLoad = { ScreenTemplates.appMenu, ScreenTemplates.computer, ScreenTemplates.credits,
				ScreenTemplates.highscores, ScreenTemplates.installAppPrompt, ScreenTemplates.intro, ScreenTemplates.lrs,
				ScreenTemplates.manual, ScreenTemplates.messages, ScreenTemplates.sectorContextMenu,
				ScreenTemplates.settings, ScreenTemplates.statusReport, ScreenTemplates.updateAppPrompt };
		outstandingLoads = templatesToLoad.length;
		for (String template:templatesToLoad)
			loadTemplate(template, callback);
	}

	@Override
	public String getTemplateFor(String key) {
		return templates.get(key);
	}

}
