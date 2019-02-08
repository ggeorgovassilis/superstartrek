package superstartrek.client.activities;

public interface View<A extends Activity> {

	void show();
	void hide();
	boolean isVisible();
}
