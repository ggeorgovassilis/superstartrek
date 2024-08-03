package superstartrek.client.activities.pwa.promise;

import superstartrek.client.activities.pwa.Callback;

public class GatePromiseImpl<T> implements Promise<T>, Callback<T>{

	int outstandingPromises = 0;
	Callback<T> callback;
	
	@Override
	public Promise<T> then(Callback<T> callback) {
		this.callback = callback;
		return this;
	}

	@Override
	public Promise<T> all(Promise<T>[] promises) {
		outstandingPromises = promises.length;
		for (Promise<T> p:promises) {
			p.then(this);
		}
		return this;
	}

	@Override
	public void onSuccess(T result) {
		outstandingPromises--;
		if (outstandingPromises==0)
			callback.onSuccess(result);
	}

}
