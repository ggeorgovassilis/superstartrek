package eventbus;

import java.util.List;

import superstartrek.client.bus.EventHandler;

public interface Handler1 extends EventHandler{

	Void method1(int p1, String p2, List<String> p3);
	
}
