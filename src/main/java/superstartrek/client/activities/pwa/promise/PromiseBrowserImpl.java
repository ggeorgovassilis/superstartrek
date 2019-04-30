package superstartrek.client.activities.pwa.promise;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import superstartrek.client.activities.pwa.Callback;

public class PromiseBrowserImpl<T> extends JavaScriptObject implements Promise<T>{

	protected PromiseBrowserImpl() {
	}
	
	//@formatter:off
	@Override
	public final native Promise<T> then(Callback<T> callback)/*-{
		return this.then(function(arg){
		    	callback.@superstartrek.client.activities.pwa.Callback::onSuccess(Ljava/lang/Object;)(arg);
		})["catch"](function(e){
		    	callback.@superstartrek.client.activities.pwa.Callback::onFailure(Ljava/lang/Throwable;)(e);
		});
	}-*/;
	//@formatter:on

}
