package genericsbus;

import java.util.HashMap;
import java.util.Map;

import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.bus.BaseHandler;
import superstartrek.client.bus.EventBus;
import superstartrek.client.bus.Event;

public class TestingBus extends EventBus{

	Map<Event<?>, Integer> fireCount = new HashMap<Event<?>, Integer>();

	@Override
	public <T extends BaseHandler> void fireEvent(Event<T> type, Callback<T> callback) {
		Integer count = fireCount.get(type);
		if (count == null)
			count = 1;
		else count = count + 1;
		fireCount.put(type, count);
		super.fireEvent(type, callback);
	}
	
	public <T extends BaseHandler> int getFiredCount(Event<T> type) {
		Integer count = fireCount.get(type);
		return count==null?0:count;
	}

}
