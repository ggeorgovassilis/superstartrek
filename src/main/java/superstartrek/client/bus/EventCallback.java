package superstartrek.client.bus;

public interface EventCallback<T> {

	void call(T arg);
}
