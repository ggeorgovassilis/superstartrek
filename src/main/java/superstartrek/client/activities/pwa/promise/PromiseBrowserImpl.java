package superstartrek.client.activities.pwa.promise;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PromiseBrowserImpl<T> extends JavaScriptObject implements Promise<T>{

	protected PromiseBrowserImpl() {
	}
	
	//@formatter:off
	@Override
	public final native Promise then(AsyncCallback<T> callback)/*-{
		return this.then(function(arg){
		    	callback.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(arg);
		})["catch"](function(e){
		    	callback.@com.google.gwt.user.client.rpc.AsyncCallback::onFailure(Ljava/lang/Throwable;)(e);
		});
	}-*/;
	//@formatter:on

}
