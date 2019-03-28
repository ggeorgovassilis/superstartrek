package superstartrek.client.activities.sector.contextmenu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import superstartrek.client.activities.PopupView;

public class SectorMenuView extends PopupView<SectorMenuActivity> implements ISectorMenuView{

	public SectorMenuView(SectorMenuPresenter presenter) {
		super(presenter);
	}
	
	@Override
	public void finishUiConstruction() {
		presenter.getApplication().page.add(this);
		getHtmlPanel().addStyleName("sectorselectionbar");

		getHtmlPanel().addDomHandler(new ClickHandler() {
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
		popupPanel.setPopupPosition(x, y);
	}
	
	@Override
	public void enableButton(String id, boolean status) {
		getHtmlPanel().getElementById(id).setClassName(status?"":"disabled");
	}

	@Override
	protected String getContentForHtmlPanel() {
		return getPresenter().getApplication().getResources().sectorSelectionMenu().getText();
	}

}
