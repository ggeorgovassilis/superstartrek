package superstartrek.client.activities.navigation.astar;

/**
 * Node Class
 *
 * @author Marcelo Surriabre
 * @version 2.0, 2018-02-23
 */
public class Node {

	private int g;
	private int f;
	private int h;
	private final int row;
	private final int col;
	private final int hashcode;
	private boolean isBlock;
	private Node parent;

	public Node(int row, int col) {
		super();
		this.row = row;
		this.col = col;
		hashcode = row*1024+col;
	}

	public void calculateHeuristic(Node finalNode) {
		this.h = Math.abs(finalNode.getRow() - getRow()) + Math.abs(finalNode.getCol() - getCol());
	}

	public void setNodeData(Node currentNode, int cost) {
		int gCost = currentNode.getG() + cost;
		setParent(currentNode);
		g = gCost;
		calculateFinalCost();
	}

	public boolean checkBetterPath(Node currentNode, int cost) {
		int gCost = currentNode.getG() + cost;
		if (gCost < getG()) {
			setNodeData(currentNode, cost);
			return true;
		}
		return false;
	}

	private void calculateFinalCost() {
		f = getG() + h;
	}

	@Override
	public boolean equals(Object arg0) {
		Node other = (Node) arg0;
		return this.getRow() == other.getRow() && this.getCol() == other.getCol();
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public String toString() {
		return "Node [row=" + row + ", col=" + col + "]";
	}

	public int getG() {
		return g;
	}

	public int getF() {
		return f;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public boolean isBlock() {
		return isBlock;
	}

	public void setBlock(boolean isBlock) {
		this.isBlock = isBlock;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}
