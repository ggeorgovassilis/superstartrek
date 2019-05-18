package genericsbus;

import java.util.HashMap;
import java.util.Map;

import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.bus.BaseHandler;
import superstartrek.client.bus.Bus;

public class TestingBus extends Bus{

	Map<String, Integer> fireCount = new HashMap<String, Integer>();

	@Override
	public <T extends BaseHandler> void invoke(String type, Callback<T> callback) {
		Integer count = fireCount.get(type);
		if (count == null)
			count = 1;
		else count = count + 1;
		fireCount.put(type, count);
		super.invoke(type, callback);
	}
	
	public int getFiredCount(String type) {
		Integer count = fireCount.get(type);
		return count==null?0:count;
	}

}
