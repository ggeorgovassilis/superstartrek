package superstartrek.client.eventbus;

public final class Event<T extends EventHandler> {

	final String name;
	
	public Event(String name){
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
