package superstartrek.client.bus;

public final class Event<T extends BaseHandler> {

	final String name;
	
	public Event(String name){
		this.name = name;
	}
	
	public Event() {
		this("unnamed");
	}
	
	@Override
	public String toString() {
		return name;
	}
}
