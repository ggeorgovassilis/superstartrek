package eventbus;

import java.util.HashMap;
import java.util.Map;

import superstartrek.client.bus.EventHandler;
import superstartrek.client.bus.EventBus;
import superstartrek.client.bus.EventCallback;
import superstartrek.client.bus.Event;

public class CountingEventBus extends EventBus{

	Map<Event<?>, Integer> fireCount = new HashMap<Event<?>, Integer>();

	@Override
	public <T extends EventHandler> void fireEvent(Event<T> type, EventCallback<T> callback) {
		Integer count = fireCount.get(type);
		if (count == null)
			count = 1;
		else count = count + 1;
		fireCount.put(type, count);
		super.fireEvent(type, callback);
	}
	
	public <T extends EventHandler> int getFiredCount(Event<T> type) {
		Integer count = fireCount.get(type);
		return count==null?0:count;
	}

}
