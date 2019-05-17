package superstartrek.client.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.shared.UmbrellaException;

import superstartrek.client.activities.pwa.Callback;

public class Bus {

	Map<String, List<? extends BaseHandler>> handlers = new HashMap<String, List<? extends BaseHandler>>();
	Map<String, Integer> fireCount = new HashMap<String, Integer>();
	
	public <T extends BaseHandler> void register(String type, T handler) {
		@SuppressWarnings("unchecked")
		List<T> list = (List<T>) handlers.get(type);
		if (list == null)
			list = new ArrayList<T>();
		if (list.contains(handler))
			return;
		list.add(handler);
		handlers.put(type, list);
	}

	public <T extends BaseHandler> void unregister(String type, T handler) {
		List<T> list = (List<T>) handlers.get(type);
		if (list != null)
			list.remove(handler);
	}

	public <T extends BaseHandler> void unregister(T handler) {
		for (String type : handlers.keySet()) {
			@SuppressWarnings("unchecked")
			List<T> list = (List<T>) handlers.get(type);
			if (list != null)
				list.remove(handler);
		}
	}

	public <T extends BaseHandler> void invoke(String type, Callback<T> callback) {
		Integer count = fireCount.get(type);
		if (count == null)
			count = 1;
		else count = count + 1;
		fireCount.put(type, count);
		@SuppressWarnings("unchecked")
		List<T> protoList = (List<T>) handlers.get(type);
		if (protoList == null)
			return;
		List<T> list = new ArrayList<T>(protoList);
		Set<Throwable> errors = null;
		for (T h : list)
			try {
				callback.onSuccess(h);
			} catch (Throwable e) {
				if (errors == null)
					errors = new HashSet<Throwable>();
				errors.add(e);
			}
		if (errors != null)
			throw new UmbrellaException(errors);
	}
	
	public int getFiredCount(String type) {
		Integer count = fireCount.get(type);
		return count==null?0:count;
	}

}
