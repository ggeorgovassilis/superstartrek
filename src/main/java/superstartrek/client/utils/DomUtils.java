package superstartrek.client.utils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class DomUtils {

	public static Element getTbody(Element table) {
		NodeList<Element> list = table.getElementsByTagName("tbody");
		if (list == null || list.getLength()==0)
			return table;
		return list.getItem(0);
	}
}
