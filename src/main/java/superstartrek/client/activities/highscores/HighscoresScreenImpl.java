package superstartrek.client.activities.highscores;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.utils.CSS;

public class HighscoresScreenImpl extends BaseScreen<HighscoresPresenter> implements HighscoresScreen{

	public HighscoresScreenImpl(HighscoresPresenter p) {
		super(p);
	}

	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		element.setInnerHTML(templates.highscoresScreen().getText());
		addStyleName("highscores-screen");
		sinkEvents(Event.ONCLICK);
	}

	@Override
	public void clearEntries() {
		NodeList<Element> trs = CSS.querySelectorAll("#highscores tbody tr");
		int length = trs.getLength();
		for (int i=0;i<length;i++) {
			trs.getItem(i).removeFromParent();
		}
	}

	@Override
	public void addEntry(String date, String score) {
		Element eTable = getElementById("highscores");
		Element eTr = DOM.createTR();
		eTr.setInnerHTML("<td class=score>"+score+"</td><td class=date>"+date+"</td>");
		eTable.appendChild(eTr);
	}

}
