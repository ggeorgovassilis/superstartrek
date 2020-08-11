package superstartrek.client.activities.appmenu;

import superstartrek.client.activities.IPopupView;

public interface IAppMenuView extends IPopupView<AppMenuPresenter> {

	void setMenuEntryEnabled(String cmd, boolean enabled);

}