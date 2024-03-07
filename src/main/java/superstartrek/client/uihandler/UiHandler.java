package superstartrek.client.uihandler;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import superstartrek.client.Application;
import superstartrek.client.eventbus.Events;
import superstartrek.client.utils.Strings;

public class UiHandler{
	
	public static int[] parseCoordinatesFromTag(String tag) {
		String[] parts = tag.split("_");
		return new int[] {Integer.parseInt(parts[1]), Integer.parseInt(parts[2])};
	}

	public void initialise() {
		Element e = Document.get().getBody();
		DOM.sinkEvents(e, Event.ONCLICK);
		DOM.setEventListener(e, (Event event) -> handleEvent(event));

	}

	void handleEvent(Event event) {
		Element e = event.getEventTarget().cast();
		Element body = Document.get().getBody();
		do {
			if (e.hasAttribute("data-uih")) 
				break;
			e = e.getParentElement();
			if (e == null || e == body)
				return;
		} while(true);
		String attr = e.getAttribute("data-uih");
		if (Strings.isEmpty(attr))
			attr = e.getId();
		String attrRef = attr;
		event.preventDefault();
		event.stopPropagation();
		Application.get().eventBus.fireEvent(Events.INTERACTION, h->h.onUiInteraction(attrRef));
	}
}
