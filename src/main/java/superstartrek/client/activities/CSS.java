package superstartrek.client.activities;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

import superstartrek.client.utils.Timer;

public class CSS {
	
	final static String[] damageClasses={"damage-offline","damage-bad","damage-medium","damage-light",""};
	/**
	 * Return a CSS damage class
	 * 
	 * @param value between 0 and 1
	 * @return
	 */
	public static String damageClass(double value) {
		int index = (int)Math.floor((damageClasses.length-1)*value);
		return damageClasses[index];
	}
	
	public static void addClassDeferred(Element e, String css) {
		Timer.postpone(new ScheduledCommand() {

			@Override
			public void execute() {
				e.addClassName(css);
			}
			
		});
	}

	public static void removeClassDeferred(Element e, String css) {
		Timer.postpone(new ScheduledCommand() {

			@Override
			public void execute() {
				e.removeClassName(css);
			}
			
		});
	}

	public final static native NodeList<Element> querySelectorAll(String selectors) /*-{
																					return $doc.querySelectorAll(selectors);
																					}-*/;
}