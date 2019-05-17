package genericsbus;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.bus.Bus;

import static org.junit.Assert.*;

public class TestGenericsBus {

	@Test
	public void test_individual_handlers() {
		StringBuffer sb = new StringBuffer();
		Handler1 handler1 = new Handler1() {
			@Override
			public void method1(int p1, String p2, List p3) {
				sb.append("handler1.method1 "+p1+" "+p2+" "+p3.size()).append("\n");
			}
		};
		
		Handler2 handler2 = new Handler2() {

			@Override
			public void method2(String p1, String p2) {
				sb.append("handler2.method2 "+p1+" "+p2).append("\n");
			}
		};

		Bus bus = new Bus();
		bus.register(Events.EVENT1, handler1);
		bus.register(Events.EVENT2, handler2);
		bus.invoke(Events.EVENT1, (Callback<Handler1>) ((h) -> h.method1(1, "2", new ArrayList<Object>())));
		bus.invoke(Events.EVENT2, (Callback<Handler2>) ((h) -> h.method2("p1","p2")));
		assertTrue(sb.toString().contains("handler1.method1 1 2 0"));
		assertTrue(sb.toString().contains("handler2.method2 p1 p2"));
	}

	@Test
	public void test_multiple_handlers() {
		StringBuffer sb = new StringBuffer();
		Handler1 handlerA = new Handler1() {
			@Override
			public void method1(int p1, String p2, List p3) {
				sb.append("call 1\n");
			}
		};
		
		Handler1 handlerB = new Handler1() {
			@Override
			public void method1(int p1, String p2, List p3) {
				sb.append("call 2\n");
			}

		};

		Bus bus = new Bus();
		bus.register(Events.EVENT1, handlerA);
		bus.register(Events.EVENT1, handlerB);
		bus.invoke(Events.EVENT1, (Callback<Handler1>) ((h) -> h.method1(1, "2", new ArrayList<Object>())));
		bus.invoke(Events.EVENT1, (Callback<Handler1>) ((h) -> h.method1(1, "2", new ArrayList<Object>())));
		assertTrue(sb.toString().contains("call 1"));
		assertTrue(sb.toString().contains("call 2"));
	}


	@Test
	public void test_combined_handler() {
		StringBuffer sb = new StringBuffer();
		CombinedHandler cb = new CombinedHandler(sb);

		Bus bus = new Bus();
		bus.register(Events.EVENT1, cb);
		bus.register(Events.EVENT2, cb);
		bus.invoke(Events.EVENT1, (Callback<Handler1>) ((h) -> h.method1(1, "2", new ArrayList<Object>())));
		bus.invoke(Events.EVENT2, (Callback<Handler2>) ((h) -> h.method2("p1","p2")));
		assertTrue(sb.toString().contains("CombinedHandler.method1 1 2 0"));
		assertTrue(sb.toString().contains("CombinedHandler.method2 p1 p2"));
	}
}
