package superstartrek.client.activities.appmenu;

import superstartrek.client.activities.IPopupView;
import superstartrek.client.activities.messages.MessagesPresenter;

public interface IAppMenuView extends IPopupView<AppMenuPresenter> {

	void setMenuEntryEnabled(String cmd, boolean enabled);

}