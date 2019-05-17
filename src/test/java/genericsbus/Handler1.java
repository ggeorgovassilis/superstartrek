package genericsbus;

import java.util.List;

import superstartrek.client.bus.BaseHandler;

public interface Handler1 extends BaseHandler{

	void method1(int p1, String p2, List<String> p3);
	
}
