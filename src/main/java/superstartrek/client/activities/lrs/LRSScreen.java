package superstartrek.client.activities.lrs;

import com.google.gwt.user.client.ui.HTMLPanel;

import superstartrek.client.Resources;
import superstartrek.client.activities.BaseScreen;
import superstartrek.client.activities.CSS;
import superstartrek.client.activities.Presenter;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class LRSScreen extends BaseScreen<LRSActivity>{
	
	Element[][] cells;
	
	@Override
	protected HTMLPanel createPanel() {
		return new HTMLPanel(Resources.INSTANCE.lrsScreen().getText());
	}
	
	@Override
	public void finishUiConstruction() {
		super.finishUiConstruction();
		cells = new Element[8][8];
		NodeList<Element> tds = CSS.querySelectorAll("#longrangescan .quadrants td");
		for (int i=tds.getLength()-1;i>=0;i--) {
			Element eTd = tds.getItem(i);
			int x = Integer.parseInt(eTd.getAttribute("x"));
			int y = Integer.parseInt(eTd.getAttribute("y"));
			cells[x][y] = eTd;
		}
	}
	
	public LRSScreen(Presenter<LRSActivity> p) {
		super(p);
	}
	
	public void updateQuadrant(int x, int y, String text, String css){
		cells[x][y].setInnerHTML(text);
		cells[x][y].setClassName(css);
	}
}
