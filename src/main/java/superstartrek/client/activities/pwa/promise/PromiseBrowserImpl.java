package superstartrek.client.activities.pwa.promise;

import com.google.gwt.core.client.JavaScriptObject;
import superstartrek.client.activities.pwa.Callback;

public class PromiseBrowserImpl<T> extends JavaScriptObject implements Promise<T>{

	protected PromiseBrowserImpl() {
	}
	
	//@formatter:off
	@Override
	public final native Promise<T> then(Callback<T> callback)/*-{
		return this.then(function(value){
		    	callback.@superstartrek.client.activities.pwa.Callback::onSuccess(Ljava/lang/Object;)(value);
		})["catch"](function(e){
				console.error(e);
		    	callback.@superstartrek.client.activities.pwa.Callback::onFailure(Ljava/lang/Throwable;)(e);
		});
	}-*/;

	@Override
	public final native Promise<T>[] all(Promise<T>[] promises)/*-{
		function reflect(promise){
    		return promise.then(function(v){ return {v:v, status: "resolved" }},
                   function(e){ return {e:e, status: "rejected" }});
		}
		return Promise.all(promises.map(reflect));
	}-*/;
	//@formatter:on

}
