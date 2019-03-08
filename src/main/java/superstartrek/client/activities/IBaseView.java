package superstartrek.client.activities;

public interface IBaseView<A extends Activity> extends View<A>{

	<T extends Presenter<A>> T getPresenter();

	void finishUiConstruction();

	void show();

	void hide();

	boolean isVisible();

}