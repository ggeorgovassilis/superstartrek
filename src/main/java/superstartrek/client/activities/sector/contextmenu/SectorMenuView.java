package superstartrek.client.activities.sector.contextmenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseView;
import superstartrek.client.utils.Timer;

public class SectorMenuView extends BaseView<SectorMenuActivity>
		implements ISectorMenuView, MouseDownHandler, TouchStartHandler, ClickHandler {

	public SectorMenuView(SectorMenuPresenter presenter) {
		super(presenter);
	}

	@Override
	protected Widget createWidgetImplementation() {
		HTMLPanel panel = new HTMLPanel(getPresenter().getApplication().getResources().sectorSelectionMenu().getText());
		return panel;
	}

	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		addDomHandler(this, MouseDownEvent.getType());
		addDomHandler(this, ClickEvent.getType());
		addDomHandler(this, TouchStartEvent.getType());
		addStyleName("sector-context-menu");
		hide();
		RootPanel.get().add(this);
	}

	@Override
	public void setLocation(int x, int y) {
		GWT.log("setLocation "+x+":"+y);
		Element e = getElement();
		e.getStyle().setLeft(x, Unit.PX);
		e.getStyle().setTop(y, Unit.PX);
	}

	@Override
	public void hide(ScheduledCommand cmd) {
		removeStyleName("expanded");
		if (isVisible()) {
			Timer.postpone(new RepeatingCommand() {

				@Override
				public boolean execute() {
					SectorMenuView.super.hide();
					if (cmd != null)
						cmd.execute();
					return false;
				}
			}, 250);
		} else if (cmd != null)
			cmd.execute();
	}

	@Override
	public void enableButton(String id, boolean status) {
		HTMLPanel panel = (HTMLPanel) getWidget();
		Element e = panel.getElementById(id).getParentElement();
		if (status)
			e.removeClassName("disabled");
		else e.addClassName("disabled");

	}

	@Override
	public void show() {
		super.show();
		HTMLPanel panel = (HTMLPanel) getWidget();
		panel.getElementById("cmd_navigate").focus();
		Timer.postpone(new RepeatingCommand() {
			
			@Override
			public boolean execute() {
				addStyleName("expanded");
				return false;
			}
		}, 10);
	}

	public void handleButtonInteraction(DomEvent<?> event) {
		Element e = event.getNativeEvent().getEventTarget().cast();
		event.preventDefault();
		event.stopPropagation();
		String command = e.getAttribute("id");
		if (command != null && !command.isEmpty())
			((SectorMenuPresenter) getPresenter()).onCommandClicked(command);
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

	@Override
	public int getMetricWidthInPx() {
		return DOM.getElementById("em-metric").getOffsetWidth();
	}

	@Override
	public int getMetricHeightInPx() {
		return DOM.getElementById("em-metric").getOffsetHeight();
	}

}
