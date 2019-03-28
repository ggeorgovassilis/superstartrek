package superstartrek.client.activities.sector.contextmenu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import superstartrek.client.activities.BaseView;

public class SectorMenuView extends BaseView<SectorMenuActivity> implements ISectorMenuView{

	PopupPanel popupPanel;
	HTMLPanel html;
	
	public SectorMenuView(SectorMenuPresenter presenter) {
		super(presenter);
	}
	
	@Override
	public void show() {
		popupPanel.show();
	}
	
	@Override
	public void hide() {
		popupPanel.hide();
	}

	@Override
	protected Widget createWidgetImplementation() {
		popupPanel = new PopupPanel(true,true);
		popupPanel.setGlassEnabled(true);
		popupPanel.setGlassStyleName("glasspanel");
		html = new HTMLPanel(presenter.getApplication().getResources().sectorSelectionMenu().getText());
		html.addStyleName("sectorselectionbar");
		popupPanel.add(html);
		return new FlowPanel();
	}
	
	@Override
	public void finishUiConstruction() {
		presenter.getApplication().page.add(this);
		html.addDomHandler(new ClickHandler() {
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
		html.getElementById(id).setClassName(status?"":"disabled");
	}

}
