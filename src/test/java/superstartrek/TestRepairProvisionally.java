package superstartrek;

import org.junit.Before;
import org.junit.Test;

import superstartrek.client.eventbus.Events;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TestRepairProvisionally extends BaseTest{

	@Before
	public void setup() {
		when(browser.randomDouble()).thenReturn(0.5,0.6,0.1,0.3,0.3);
	}
	
	@Test
	public void testRepairTorpedos() {
		enterprise.torpedos.damageAndTurnOff(starMap.getStarDate());
		starMap.advanceStarDate(4);
		enterprise.repairProvisionally();
		
		assertTrue(enterprise.torpedos.isOperational());
		assertEquals(1,bus.getFiredCount(Events.MESSAGE_POSTED));
	}

	@Test
	public void testRepairPhasers() {
		enterprise.phasers.damage(enterprise.phasers.getMaximum()/2, starMap.getStarDate());
		enterprise.phasers.setBroken(true);
		starMap.advanceStarDate(3);
		enterprise.repairProvisionally();
		
		assertTrue(enterprise.phasers.isOperational());
		assertTrue(enterprise.phasers.getValue()>10);
		assertFalse(enterprise.phasers.isBroken());
		assertEquals(1, bus.getFiredCount(Events.MESSAGE_POSTED));
	}
}
