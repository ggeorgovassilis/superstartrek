package eventbus;

import superstartrek.client.bus.Event;

public class Events {

	public final static Event<Handler1> E1 = new Event<Handler1>("E1");
	public final static Event<Handler2> E2 = new Event<Handler2>("E2");
	public final static Event<MultiHandler> E3 = new Event<MultiHandler>("E3");
}
