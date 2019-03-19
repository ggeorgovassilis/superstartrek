package superstartrek.client.activities.computer.srs;

public interface MapCellRenderer {

	static final String nbsp = "\u00A0";
	void updateCell(int x, int y, String symbol, String css);
}
