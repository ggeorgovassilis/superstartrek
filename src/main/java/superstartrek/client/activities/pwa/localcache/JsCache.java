package superstartrek.client.activities.pwa.localcache;

import com.google.gwt.core.client.JavaScriptObject;

import superstartrek.client.activities.pwa.promise.Promise;

public class JsCache extends JavaScriptObject{

	protected JsCache() {
	}
	
	public final native Promise<Void> addAll(String[] files)/*-{
		return this.addAll(files);
	}-*/;
	
	public final native Promise<Void> add(String file)/*-{
		return this.add(file);
	}-*/;

	
}
