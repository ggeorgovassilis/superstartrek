package superstartrek.client.eventbus;

public interface EventCallback<T> {

	void call(T arg);
}
