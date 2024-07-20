package superstartrek.client.activities.computer.sectorcontextmenu;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import superstartrek.client.activities.BaseView;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.CSS;
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
		element.setInnerHTML(templates.getTemplateFor(ScreenTemplates.TemplateNames.sectorContextMenu));
		addStyleName("sector-context-menu");
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
			//reacting to a CSS animationend event would be cleaner, but there's no guarantee
			//a browser/OS-level accessibility setting wouldn't disable animations and the event would never be cast.
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

	@Override
	public void enableDockWithStarbaseButton(boolean status) {
		getElement().removeClassName("can-dock-with-starbase");
		if (status)
			getElement().addClassName("can-dock-with-starbase");
	}
}
