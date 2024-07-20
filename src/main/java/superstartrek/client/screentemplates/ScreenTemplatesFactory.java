package superstartrek.client.screentemplates;

import java.util.HashMap;
import java.util.Map;

import superstartrek.client.Application;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.activities.pwa.PWA;

public class ScreenTemplatesFactory implements ScreenTemplates {

	Map<TemplateNames, String> templates = new HashMap<TemplateNames, String>();
	int outstandingLoads = 0;

	void loadTemplate(TemplateNames template, Callback<Void> callback) {
		PWA pwa = Application.get().pwa;
		pwa.getFileContent(template.path, (content) -> {
			templates.put(template, content);
			outstandingLoads--;
			if (outstandingLoads == 0)
				callback.onSuccess(null);
		});

	}

	public void initialise(Callback<Void> callback) {
		TemplateNames[] enums = TemplateNames.values();
		outstandingLoads = enums.length;
		for (TemplateNames template:enums)
			loadTemplate(template, callback);
	}

	@Override
	public String getTemplateFor(TemplateNames key) {
		return templates.get(key);
	}

}
