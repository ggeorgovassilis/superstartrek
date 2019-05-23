package superstartrek.client.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gwt.event.shared.UmbrellaException;

public class EventBus {

	Map<Event<? extends BaseHandler>, List<? extends BaseHandler>> handlers = new HashMap<Event<? extends BaseHandler>, List<? extends BaseHandler>>();

	public <T extends BaseHandler> void addHandler(Event<T> type, T handler) {
		@SuppressWarnings("unchecked")
		List<T> list = (List<T>) handlers.get(type);
		if (list == null) {
			list = new ArrayList<T>();
			handlers.put(type, list);
		} else
		if (list.contains(handler))
			return;
		list.add(handler);
	}

	public <T extends BaseHandler> void removeHandler(Event<T> type, T handler) {
		@SuppressWarnings("unchecked")
		List<T> list = (List<T>) handlers.get(type);
		if (list != null)
			list.remove(handler);
	}

	public <T extends BaseHandler> void removeHandler(T handler) {
		for (Event<?> type : handlers.keySet()) {
			@SuppressWarnings("unchecked")
			List<T> list = (List<T>) handlers.get(type);
			list.remove(handler);
		}
	}

	public <T extends BaseHandler> void fireEvent(Event<T> type, EventCallback<T> callback) {
		@SuppressWarnings("unchecked")
		List<T> protoList = (List<T>) handlers.get(type);
		if (protoList == null)
			return;
		//TODO: should we use protoList directly and instead queue add/remove handlers?
		//pro: firing events is much more common than modifying listeners
		//con: array lists are backed by js native arrays, cloning should be very fast.
		List<T> copy = new ArrayList<T>(protoList);
		Set<Throwable> errors = null;
		for (T h : copy)
			try {
				callback.call(h);
			} catch (Throwable e) {
				if (errors == null)
					errors = new HashSet<Throwable>();
				errors.add(e);
			}
		if (errors != null)
			throw new UmbrellaException(errors);
	}
	
}
