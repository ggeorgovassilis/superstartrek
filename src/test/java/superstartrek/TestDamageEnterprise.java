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
		verify(handler).showMessage(eq("Torpedo bay damaged"), eq("enterprise-damaged"));
	}
	
	@Test
	public void testDamagePhasers() {
		MessageHandler handler = mock(MessageHandler.class);
		events.addHandler(MessageEvent.TYPE, handler);

		enterprise.damagePhasers();
		assertTrue(enterprise.getPhasers().isEnabled());
		assertEquals(105, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		enterprise.damagePhasers();
		assertTrue(enterprise.getPhasers().isEnabled());
		assertEquals(60, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		enterprise.damagePhasers();
		assertTrue(enterprise.getPhasers().isEnabled());
		assertEquals(15, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		enterprise.damagePhasers();
		assertEquals(0, enterprise.getPhasers().getCurrentUpperBound(), 0.1);
		assertFalse(enterprise.getPhasers().isEnabled());
		verify(handler, times(4)).showMessage(eq("Phaser array damaged"), eq("enterprise-damaged"));
	}

	@Test
	public void testDamageImpulse() {
		MessageHandler handler = mock(MessageHandler.class);
		events.addHandler(MessageEvent.TYPE, handler);

		enterprise.damageImpulse();
		assertTrue(enterprise.getImpulse().isEnabled());
		assertEquals(2, enterprise.getImpulse().getCurrentUpperBound(), 0.1);
		
		enterprise.damageImpulse();
		assertTrue(enterprise.getImpulse().isEnabled());
		assertEquals(1, enterprise.getImpulse().getCurrentUpperBound(), 0.1);

		enterprise.damageImpulse();
		assertFalse(enterprise.getImpulse().isEnabled());
		assertEquals(0, enterprise.getImpulse().getCurrentUpperBound(), 0.1);

		verify(handler, times(3)).showMessage(eq("Impulse drive damaged"), eq("enterprise-damaged"));
		
	}
}
