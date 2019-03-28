package superstartrek.client.activities.sector.contextmenu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseView;

public class SectorMenuView extends BaseView<SectorMenuActivity> implements ISectorMenuView{

	public SectorMenuView(SectorMenuPresenter presenter) {
		super(presenter);
	}

	@Override
	protected HTMLPanel createWidgetImplementation() {
		return new HTMLPanel(presenter.getApplication().getResources().sectorSelectionMenu().getText());
	}
	
	@Override
	public void finishUiConstruction() {
		addStyleName("sectorselectionbar");
		presenter.getApplication().page.add(this);
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Element e = event.getNativeEvent().getEventTarget().cast();
				String command = e.getAttribute("id");
				if (command!=null && !command.isEmpty())
					((SectorMenuPresenter)getPresenter()).onCommandClicked(command);
				
			}
		}, ClickEvent.getType());
		hide();
	}
	
	@Override
	public void setLocation(int x, int y) {
		getElement().getStyle().setTop(y, Unit.PX);
		getElement().getStyle().setLeft(x, Unit.PX);
	}
	
	@Override
	public void enableButton(String id, boolean status) {
		DOM.getElementById(id).setClassName(status?"":"disabled");
	}

}
