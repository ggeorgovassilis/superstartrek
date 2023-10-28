package superstartrek.client.activities;

@SuppressWarnings("rawtypes")
public interface Presenter<V extends View> {

	void setView(V view);
	
}
