package superstartrek.client.eventbus;

/**
 * DO NOT DELETE. 
 * Handler interface for events. A static code analysis will show only one use in {@link EventBus#fireEvent(Event, EventCallback)} 
 * which is wrong because we do lambdas everywhere (single method interfaces) which obscures all other uses.
 * @param <T> T can't be more specific because it is whatever the callback handler needs.
 */
public interface EventCallback<T> {

	void call(T arg);
}
