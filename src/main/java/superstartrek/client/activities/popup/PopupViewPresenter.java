package superstartrek.client.activities.popup;

import superstartrek.client.activities.Presenter;

@SuppressWarnings("rawtypes")
public interface PopupViewPresenter<V extends PopupView<? extends PopupViewPresenter>> extends Presenter<V>{

	void cancelButtonClicked();
}
