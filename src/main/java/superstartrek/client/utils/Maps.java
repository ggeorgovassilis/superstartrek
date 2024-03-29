package superstartrek.client.utils;

import superstartrek.client.activities.computer.srs.MapCellRenderer;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.StarMap;

public class Maps {

	//extraCssPadded: css must end with a blank space
	public static void renderCell(int cellX, int cellY, StarMap map, Quadrant q, String extraCssPadded, MapCellRenderer renderer) {
		String symbol = "";
		String css = extraCssPadded+(map.enterprise.getQuadrant() == q ?"has-enterprise":"");
		if (q != null) {
			if (q.isExplored()) {
				css+=" explored";	
				if (q.hasKlingons()) {
					symbol += "K";
					css += " has-klingons";
				} 
			}
			if (q.getStarBase()!=null) {
				symbol += "!";
				css += " has-starbase";
			}
			symbol += q.getStars().size();
		} else {
			symbol = "0";
		}
		renderer.updateCell(cellX, cellY, symbol, css);
	}
}
