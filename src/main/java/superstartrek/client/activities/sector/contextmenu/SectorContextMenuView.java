package superstartrek.client.activities.sector.contextmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseView;
import superstartrek.client.activities.CSS;
import superstartrek.client.utils.HtmlWidget;
import superstartrek.client.utils.Timer;

public class SectorContextMenuView extends BaseView<SectorContextMenuPresenter>
		implements ISectorContextMenuView, MouseDownHandler, TouchStartHandler, ClickHandler {

	boolean viewInTransition = false;

	public SectorContextMenuView(SectorContextMenuPresenter presenter) {
		super(presenter);
	}

	@Override
	protected Widget createWidgetImplementation() {
		return new HtmlWidget(DOM.createDiv(), presenter.getApplication().getResources().sectorContextMenu().getText());
	}

	@Override
	protected boolean isAbsulutelyPositioned() {
		return true;
	}
	
	@Override
	public void decorateWidget() {
		addStyleName("sector-context-menu");
		super.decorateWidget();
		hide();
		addDomHandler(this, MouseDownEvent.getType());
		addDomHandler(this, ClickEvent.getType());
		addDomHandler(this, TouchStartEvent.getType());
		presenter.getApplication().browserAPI.addToPage(this);
	}

	@Override
	public void setLocation(int x, int y) {
		GWT.log("SectorContxtMenuView.setLocation "+x+","+y );
		Element e = getElement();
		e.getStyle().setLeft(x, Unit.PX);
		e.getStyle().setTop(y, Unit.PX);
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
				SectorContextMenuView.super.hide();
				if (cmd != null)
					cmd.execute();
			}, 250);
		} else if (cmd != null)
			cmd.execute();
	}

	@Override
	public void enableButton(String id, boolean status) {
		HtmlWidget panel = (HtmlWidget) getWidget();
		//button's parent is the cell.
		//TODO: should we keep a reference to the cell elements instead of looking them up?
		Element e = panel.getElementById(id).getParentElement();
		CSS.setEnabled(e, status);
	}

	@Override
	public void show() {
		super.show();
		Timer.postpone(() -> addStyleName("expanded"), 10);
	}

	public void handleButtonInteraction(DomEvent<?> event) {
		Element e = event.getNativeEvent().getEventTarget().cast();
		event.preventDefault();
		event.stopPropagation();
		String command = e.getAttribute("id");
		if (command != null && !command.isEmpty())
			presenter.onCommandClicked(command);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		handleButtonInteraction(event);
	}

	@Override
	public void onClick(ClickEvent event) {
		handleButtonInteraction(event);
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		handleButtonInteraction(event);
	}
}
