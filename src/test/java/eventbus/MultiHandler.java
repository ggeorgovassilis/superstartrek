package eventbus;

import superstartrek.client.bus.EventHandler;

public interface MultiHandler extends EventHandler{

	void method1(String p1, String p2);
	void method2(int p1, int p2);
}
