package superstartrek;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.messages.MessageEvent;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.model.Enterprise;
import static org.mockito.Mockito.*;

public class TestDamageEnterprise {
	Enterprise enterprise;
	Application application;
	CountingEventBus events;
	
	
	@Before
	public void setup() {
		application = new Application();
		application.events = events = new CountingEventBus();
		enterprise = new Enterprise(application);
	}
	
	@Test
	public void testDamageTorpedos() {
		MessageHandler handler = mock(MessageHandler.class);
		events.addHandler(MessageEvent.TYPE, handler);

		enterprise.damageTorpedos();
		
		assertFalse(enterprise.getTorpedos().isEnabled());
		verify(handler).showMessage(eq("Torpedo bay damaged"), eq("info"));
	}
}
