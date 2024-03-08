package superstartrek.client.activities.lrs;

import superstartrek.client.activities.BaseScreen;
import superstartrek.client.screentemplates.ScreenTemplates;
import superstartrek.client.space.Constants;
import superstartrek.client.utils.CSS;
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
	protected boolean alignsOnItsOwn() {
		return false;
	}
}
