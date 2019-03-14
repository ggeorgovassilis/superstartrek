package superstartrek;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.messages.MessageEvent;
import superstartrek.client.model.Enterprise;
import static org.junit.Assert.*;

public class TestRepairProvisionally {

	Enterprise enterprise;
	Application application;
	CountingEventBus events;
	
	@Before
	public void setup() {
		application = Application.get();
		application.events = events = new CountingEventBus();
		enterprise = new Enterprise(application);
	}
	
	@Test
	public void testRepairTorpedos() {
		enterprise.getTorpedos().setEnabled(false);
		enterprise.repairProvisionally();
		enterprise.repairProvisionally();
		enterprise.repairProvisionally();
		
		assertTrue(enterprise.getTorpedos().isEnabled());
		assertEquals(3,events.getFiredCount(MessageEvent.TYPE));
	}
}
