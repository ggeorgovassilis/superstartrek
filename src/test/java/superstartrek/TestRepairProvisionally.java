package superstartrek;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.event.shared.testing.CountingEventBus;

import superstartrek.client.Application;
import superstartrek.client.activities.messages.MessageHandler;
import superstartrek.client.model.Enterprise;
import superstartrek.client.utils.Random;
import superstartrek.client.utils.RandomNumberFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRepairProvisionally {

	Enterprise enterprise;
	Application application;
	CountingEventBus events;
	
	@Before
	public void setup() {
		application = new Application();
		application.events = events = new CountingEventBus();
		enterprise = new Enterprise(application);
		
		RandomNumberFactory random = mock(RandomNumberFactory.class);
		when(random.nextDouble()).thenAnswer(new Answer<Double>() {
			int counter = 0;
			double numbers[]= {0.5,0.6,0.1,0.3,0.3};
			@Override
			public Double answer(InvocationOnMock invocation) throws Throwable {
				return numbers[counter++];
			}
		});
		application.random = new Random(random);

	}
	
	@Test
	public void testRepairTorpedos() {
		enterprise.getTorpedos().setEnabled(false);
		enterprise.repairProvisionally();
		enterprise.repairProvisionally();
		enterprise.repairProvisionally();
		
		assertTrue(enterprise.getTorpedos().isEnabled());
		assertEquals(3,events.getFiredCount(MessageHandler.MessageEvent.TYPE));
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
		assertEquals(3,events.getFiredCount(MessageHandler.MessageEvent.TYPE));
	}
}
