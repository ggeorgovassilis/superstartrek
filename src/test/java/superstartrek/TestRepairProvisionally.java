package superstartrek;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.model.StarMap;
import superstartrek.client.utils.BrowserAPI;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRepairProvisionally extends BaseTest{

	@Before
	public void setup() {
		when(browser.nextDouble()).thenReturn(0.5,0.6,0.1,0.3,0.3);
	}
	
	@Test
	public void testRepairTorpedos() {
		enterprise.getTorpedos().setEnabled(false);
		enterprise.repairProvisionally();
		enterprise.repairProvisionally();
		enterprise.repairProvisionally();
		
		assertTrue(enterprise.getTorpedos().isEnabled());
		assertEquals(3,events.getFiredCount(MessageHandler.MessagePostedEvent.TYPE));
	}

	@Test
	public void testRepairPhasers() {
		enterprise.getPhasers().damage(enterprise.getPhasers().getMaximum()/2);
		enterprise.getPhasers().setEnabled(false);
		enterprise.repairProvisionally();
		enterprise.repairProvisionally();
		enterprise.repairProvisionally();
		
		assertTrue(enterprise.getPhasers().isEnabled());
		assertTrue(enterprise.getPhasers().getValue()>10);
		assertEquals(3,events.getFiredCount(MessageHandler.MessagePostedEvent.TYPE));
	}
}
