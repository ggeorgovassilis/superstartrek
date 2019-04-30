package superstartrek.client.activities.pwa;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Callback<T> extends AsyncCallback<T> {

	@Override
	default void onFailure(Throwable caught) {
	}
}
