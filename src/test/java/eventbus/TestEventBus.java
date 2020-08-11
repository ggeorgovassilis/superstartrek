package eventbus;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.UmbrellaException;

import superstartrek.client.bus.EventBus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestEventBus {

	EventBus eventBus;

	@Before
	public void setup() {
		eventBus = new EventBus();
	}

	@Test
	public void test_individual_handlers() {
		StringBuffer sb = new StringBuffer();
		Handler1 handler1 = new Handler1() {
			@Override
			public Void method1(int p1, String p2, List<String> p3) {
				sb.append("handler1.method1 " + p1 + " " + p2 + " " + p3.size()).append("\n");
				return null;
			}
		};

		Handler2 handler2 = new Handler2() {

			@Override
			public void method2(String p1, String p2) {
				sb.append("handler2.method2 " + p1 + " " + p2).append("\n");
			}
		};

		eventBus.addHandler(Events.E1, handler1);
		eventBus.addHandler(Events.E2, handler2);
		eventBus.fireEvent(Events.E1, (h) -> h.method1(1, "2", new ArrayList<String>()));
		eventBus.fireEvent(Events.E2, (h) -> h.method2("p1", "p2"));
		assertTrue(sb.toString().contains("handler1.method1 1 2 0"));
		assertTrue(sb.toString().contains("handler2.method2 p1 p2"));
	}

	@Test
	public void test_multiple_handlers() {
		StringBuffer sb = new StringBuffer();
		Handler1 handlerA = new Handler1() {
			@Override
			public Void method1(int p1, String p2, List<String> p3) {
				sb.append("call 1\n");
				return null;
			}
		};

		Handler1 handlerB = new Handler1() {
			@Override
			public Void method1(int p1, String p2, List<String> p3) {
				sb.append("call 2\n");
				return null;
			}

		};

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

		eventBus.addHandler(Events.E1, cb);
		eventBus.addHandler(Events.E2, cb);
		eventBus.fireEvent(Events.E1, (h) -> h.method1(1, "2", new ArrayList<String>()));
		eventBus.fireEvent(Events.E2, (h) -> h.method2("p1", "p2"));
		assertTrue(sb.toString().contains("CombinedHandler.method1 1 2 0"));
		assertTrue(sb.toString().contains("CombinedHandler.method2 p1 p2"));
	}

	@Test
	public void test_multi_handler() {
		MultiHandler mh = mock(MultiHandler.class);

		eventBus.addHandler(Events.E3, mh);
		eventBus.fireEvent(Events.E3, (h) -> h.method1("s1", "s2"));
		eventBus.fireEvent(Events.E3, (h) -> h.method2(1, 2));
		verify(mh).method1("s1", "s2");
		verify(mh).method2(1, 2);
	}

	@Test
	public void test_remove_non_existent_handler() {
		eventBus.removeHandler(mock(Handler1.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_that_exceptions_dont_stop_propagation() {
		Handler1 handler1a = mock(Handler1.class);
		when(handler1a.method1(any(int.class), any(String.class), any(List.class))).thenThrow(RuntimeException.class);
		eventBus.addHandler(Events.E1, handler1a);

		Handler1 handler1b = mock(Handler1.class);
		eventBus.addHandler(Events.E1, handler1b);

		try {
			eventBus.fireEvent(Events.E1, (h) -> h.method1(1, "2", null));
			fail("Excpected exception");
		} catch (UmbrellaException ue) {
		}

		verify(handler1b).method1(1, "2", null);
	}

}
