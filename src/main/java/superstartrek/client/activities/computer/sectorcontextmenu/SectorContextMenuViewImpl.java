package superstartrek.client.activities.computer.sectorcontextmenu;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import superstartrek.client.activities.BaseView;
import superstartrek.client.eventbus.Events;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.CSS;
import superstartrek.client.utils.Strings;
import superstartrek.client.utils.Timer;

public class SectorContextMenuViewImpl extends BaseView<SectorContextMenuPresenter>
		implements SectorContextMenuView{

	boolean viewInTransition = false;

	public SectorContextMenuViewImpl(SectorContextMenuPresenter presenter) {
		super(presenter);
	}
	

	@Override
	protected boolean alignsOnItsOwn() {
		return true;
	}
	
	@Override
	protected void decorateWidget(ScreenTemplates templates, Element element) {
		hide();
		element.setInnerHTML(templates.sectorContextMenu().getText());
		addStyleName("sector-context-menu");
		presenter.getApplication().eventBus.addHandler(Events.INTERACTION, tag->handleButtonInteraction(tag));
		presenter.getApplication().browserAPI.addToPage(this);
	}

	@Override
	public void setLocation(int x, int y) {
		Style s = getElement().getStyle();
		s.setLeft(x, Unit.PX);
		s.setTop(y, Unit.PX);
	}

	@Override
	public void hide(ScheduledCommand cmd) {
		if (viewInTransition)
			return;
		removeStyleName("expanded");
		if (isVisible()) {
			viewInTransition = true;
			Timer.postpone(() -> {
				viewInTransition = false;
				SectorContextMenuViewImpl.super.hide();
				if (cmd != null)
					cmd.execute();
			}, 250);
		} else if (cmd != null)
			cmd.execute();
	}

	@Override
	public void enableButton(String id, boolean status) {
		//button's parent is the cell.
		//TODO: should we keep a reference to the cell elements instead of looking them up?
		Element e = getElementById(id);
		Element parent = e.getParentElement();
		CSS.setEnabledCSS(parent , status);
	}

	@Override
	public void show() {
		super.show();
		//TODO: document the delay of 10ms
		Timer.postpone(() -> addStyleName("expanded"), 10);
	}

	public void handleButtonInteraction(String tag) {
		if (!Strings.isEmpty(tag))
			presenter.onCommandClicked(tag);
	}


	@Override
	public void enableDockWithStarbaseButton(boolean status) {
		getElement().removeClassName("can-dock-with-starbase");
		if (status)
			getElement().addClassName("can-dock-with-starbase");
	}
}
