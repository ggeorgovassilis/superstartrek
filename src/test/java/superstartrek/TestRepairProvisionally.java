package superstartrek;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.bus.Events;
import superstartrek.client.model.Enterprise;

import static org.junit.Assert.*;
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
		assertEquals(3,bus.getFiredCount(Events.MESSAGE_POSTED));
	}

	@Test
	public void testRepairPhasers() {
		enterprise.getPhasers().damage(enterprise.getPhasers().getMaximum()/2, starMap.getStarDate());
		enterprise.getPhasers().setEnabled(false);
		starMap.advanceStarDate(Enterprise.TIME_TO_REPAIR_SETTING);
		enterprise.repairProvisionally();
		
		assertTrue(enterprise.getPhasers().isEnabled());
		assertTrue(enterprise.getPhasers().getValue()>10);
		assertEquals(1, bus.getFiredCount(Events.MESSAGE_POSTED));
	}
}
