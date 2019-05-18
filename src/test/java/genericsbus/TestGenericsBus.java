package genericsbus;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import superstartrek.client.bus.EventBus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
public class TestGenericsBus {

	@Test
	public void test_individual_handlers() {
		StringBuffer sb = new StringBuffer();
		Handler1 handler1 = new Handler1() {
			@Override
			public void method1(int p1, String p2, List<String> p3) {
				sb.append("handler1.method1 "+p1+" "+p2+" "+p3.size()).append("\n");
			}
		};
		
		Handler2 handler2 = new Handler2() {

			@Override
			public void method2(String p1, String p2) {
				sb.append("handler2.method2 "+p1+" "+p2).append("\n");
			}
		};

		EventBus eventBus = new EventBus();
		eventBus.addHandler(Events.E1, handler1);
		eventBus.addHandler(Events.E2, handler2);
		eventBus.fireEvent(Events.E1, (h) -> h.method1(1, "2", new ArrayList<String>()));
		eventBus.fireEvent(Events.E2, (h) -> h.method2("p1","p2"));
		assertTrue(sb.toString().contains("handler1.method1 1 2 0"));
		assertTrue(sb.toString().contains("handler2.method2 p1 p2"));
	}

	@Test
	public void test_multiple_handlers() {
		StringBuffer sb = new StringBuffer();
		Handler1 handlerA = new Handler1() {
			@Override
			public void method1(int p1, String p2, List<String> p3) {
				sb.append("call 1\n");
			}
		};
		
		Handler1 handlerB = new Handler1() {
			@Override
			public void method1(int p1, String p2, List<String> p3) {
				sb.append("call 2\n");
			}

		};

		EventBus eventBus = new EventBus();
		eventBus.addHandler(Events.E1, handlerA);
		eventBus.addHandler(Events.E1, handlerB);
		eventBus.fireEvent(Events.E1, (h) -> h.method1(1, "2", new ArrayList<String>()));
		eventBus.fireEvent(Events.E1, (h) -> h.method1(1, "2", new ArrayList<String>()));
		assertTrue(sb.toString().contains("call 1"));
		assertTrue(sb.toString().contains("call 2"));
	}


	@Test
	public void test_combined_handler() {
		StringBuffer sb = new StringBuffer();
		CombinedHandler cb = new CombinedHandler(sb);

		EventBus eventBus = new EventBus();
		eventBus.addHandler(Events.E1, cb);
		eventBus.addHandler(Events.E2, cb);
		eventBus.fireEvent(Events.E1, (h) -> h.method1(1, "2", new ArrayList<String>()));
		eventBus.fireEvent(Events.E2, (h) -> h.method2("p1","p2"));
		assertTrue(sb.toString().contains("CombinedHandler.method1 1 2 0"));
		assertTrue(sb.toString().contains("CombinedHandler.method2 p1 p2"));
	}

	@Test
	public void test_multi_handler() {
		MultiHandler mh = mock(MultiHandler.class);

		EventBus eventBus = new EventBus();
		eventBus.addHandler(Events.E3, mh);
		eventBus.fireEvent(Events.E3, (h) -> h.method1("s1", "s2"));
		eventBus.fireEvent(Events.E3, (h) -> h.method2(1,2));
		verify(mh).method1("s1", "s2");
		verify(mh).method2(1,2);
	}
}
