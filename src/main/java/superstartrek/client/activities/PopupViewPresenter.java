package superstartrek.client.activities;

@SuppressWarnings("rawtypes")
public interface PopupViewPresenter<V extends PopupView<? extends PopupViewPresenter>> extends Presenter<V>{

	void cancelButtonClicked();
}
