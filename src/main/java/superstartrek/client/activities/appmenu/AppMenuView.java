package superstartrek.client.activities.appmenu;

import superstartrek.client.activities.PopupView;

public interface AppMenuView extends PopupView<AppMenuPresenter> {

	void setMenuEntryEnabled(String cmd, boolean enabled);

}