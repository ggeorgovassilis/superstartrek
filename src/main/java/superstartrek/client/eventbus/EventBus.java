package superstartrek.client.eventbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.shared.UmbrellaException;

public class EventBus {

	// List vs Set: Lists guarantees constant handler order
	Map<Event<? extends EventHandler>, List<? extends EventHandler>> handlers = new HashMap<Event<? extends EventHandler>, List<? extends EventHandler>>();

	public <T extends EventHandler> void addHandler(Event<T> type, T handler) {
		@SuppressWarnings("unchecked")
		List<T> list = (List<T>) handlers.get(type);
		if (list == null) {
			list = new ArrayList<T>();
			handlers.put(type, list);
		} else if (list.contains(handler))
			return;
		list.add(handler);
	}

	public <T extends EventHandler> void removeHandler(Event<T> type, T handler) {
		@SuppressWarnings("unchecked")
		List<T> list = (List<T>) handlers.get(type);
		if (list != null)
			list.remove(handler);
	}

	public <T extends EventHandler> void removeHandler(T handler) {
		for (Event<?> type : handlers.keySet())
			handlers.get(type).remove(handler);
	}

	public <T extends EventHandler> void fireEvent(Event<T> type, EventCallback<T> callback) {
		@SuppressWarnings("unchecked")
		List<T> protoList = (List<T>) handlers.get(type);
		//regarding null check: once the game is fully loaded it's unlikely that there will be no handlers
		//for any event. But during setup this is quite likely.
		if (protoList == null)
			return;
		//A copy makes sure handlers which add/remove other handlers during their invocation
		//don't mess with invocation order
		List<T> copy = new ArrayList<T>(protoList);
		Set<Throwable> errors = null;
		for (T h : copy)
			try {
				callback.call(h);
			} catch (Throwable e) {
				errors=errors==null?new HashSet<Throwable>():errors;
				errors.add(e);
			}
		if (errors != null)
			throw new UmbrellaException(errors);
	}

}
