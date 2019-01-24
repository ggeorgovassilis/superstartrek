package superstartrek.client.activities.glasspanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.activities.BaseView;

public class GlassPanelView extends BaseView<GlassPanelActivity>{

	public GlassPanelView(GlassPanelPresenter presenter) {
		super(presenter);
	}
	
	@Override
	public void finishUiConstruction() {
		DOM.sinkEvents(getElement(), Event.ONCLICK);
		DOM.setEventListener(getElement(), new EventListener() {
			
			@Override
			public void onBrowserEvent(Event event) {
				((GlassPanelPresenter)getPresenter()).glassPanelWasClicked();
			}
		});
	}
	
	@Override
	protected HTMLPanel createPanel() {
		return HTMLPanel.wrap(DOM.getElementById("glasspanel"));
	}

	

}
