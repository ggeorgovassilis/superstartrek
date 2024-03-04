package superstartrek.client.activities.lrs;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.eventbus.Events;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.space.Constants;
import superstartrek.client.utils.CSS;
import superstartrek.client.utils.Strings;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class LRSScreenImpl extends BaseScreen<LRSPresenter> implements LRSScreen{
	
	Element[][] cells;
	
	@Override
	protected void createWidgetImplementation() {
		Element e = Document.get().createTableElement();
		setElement(e);
	}
	
	@Override
	protected void decorateScreen(ScreenTemplates templates, Element element) {
		element.setInnerHTML(templates.lrsScreen().getText());
		element.setId("longrangescan");
		cells = new Element[Constants.SECTORS_EDGE][Constants.SECTORS_EDGE];
		NodeList<Element> tds = CSS.querySelectorAll("#longrangescan .quadrants td");
		int length = tds.getLength()-1;
		for (int i=length;i>=0;i--) {
			Element eTd = tds.getItem(i);
			int x = i % Constants.SECTORS_EDGE;
			int y = i / Constants.SECTORS_EDGE;
			eTd.setAttribute("data-uih", "q_"+x+"_"+y);
			eTd.setAttribute("tabindex", ""+i);
			cells[x][y] = eTd;
		}
		presenter.getApplication().eventBus.addHandler(Events.INTERACTION, tag->{
			if (!tag.startsWith("q_"))
				return;
			String[] parts = tag.split("_");
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			presenter.quadrantWasClicked(x, y);
		});
	}
	
	public LRSScreenImpl(LRSPresenter p) {
		super(p);
	}
	
	@Override
	public void addCss(int x, int y, String css) {
		cells[x][y].addClassName(css);
	}
	
	@Override
	public void updateCell(int x, int y, String text, String css){
		Element e = cells[x][y];
		e.setInnerText(text);
		e.setClassName(css);
	}

	@Override
	public void removeCss(int x, int y, String css) {
		cells[x][y].removeClassName(css);
	}
	
	@Override
	protected boolean alignsOnItsOwn() {
		return false;
	}
}
