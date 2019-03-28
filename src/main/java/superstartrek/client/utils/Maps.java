package superstartrek.client.utils;

import superstartrek.client.activities.computer.srs.MapCellRenderer;
import superstartrek.client.model.Quadrant;
import superstartrek.client.model.StarMap;

public class Maps {

	public static void renderCell(int cellX, int cellY, StarMap map, Quadrant q, MapCellRenderer renderer) {
		String symbol = "";
		String css = map.enterprise.getQuadrant() == q ?"has-enterprise":"";
		if (q != null) {
			if (q.isExplored()) {
				css+=" explored";	
				if (!q.getKlingons().isEmpty()) {
					symbol += "K";
					css += " has-klingons";
				} 
			}
			if (q.getStarBase()!=null) {
				symbol += "!";
				css += " has-starbase";
			} else
				symbol += " ";
			symbol += q.getStars().size();
		} else {
			symbol = "0";
		}
		renderer.updateCell(cellX, cellY, symbol, css);
	}
}
