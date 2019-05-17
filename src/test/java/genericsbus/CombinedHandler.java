package genericsbus;

import java.util.List;

public class CombinedHandler implements Handler1, Handler2{

	StringBuffer sb;
	
	public CombinedHandler(StringBuffer sb) {
		this.sb = sb;
	}
	
	@Override
	public void method2(String p1, String p2) {
		sb.append("CombinedHandler.method2 "+p1+" "+p2);
	}

	@Override
	public void method1(int p1, String p2, List<String> p3) {
		sb.append("CombinedHandler.method1 "+p1+" "+p2+" "+p3.size());
	}

}
