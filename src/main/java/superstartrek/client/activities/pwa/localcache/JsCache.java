package superstartrek.client.activities.pwa.localcache;

import com.google.gwt.core.client.JavaScriptObject;

public class JsCache extends JavaScriptObject{

	protected JsCache() {
	}
	
	public final native Void addAll(String[] files)/*-{
		this.addAll(files);
	}-*/;
	
	
}
