package superstartrek.client.activities;

@SuppressWarnings("rawtypes")
public interface PopupViewPresenter<V extends IPopupView<? extends PopupViewPresenter>> extends Presenter<V>{

	void userWantsToDismissView();
}
