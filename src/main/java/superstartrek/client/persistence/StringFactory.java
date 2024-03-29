package superstartrek.client.persistence;

import com.google.gwt.core.client.JsArray;

/**
 * Helper class for constructing large strings quickly with
 * functionality needed by {@link StarMapSerialiser}.
 * 
 * A much faster implementation would be backed by {@link JsArray} which 
 * can grow as needed. However that's not unit-testable anymore.
 */
public class StringFactory {

	// Since the StarMap is fixed size, there's an upper limit of objects in it
	String[] arr=new String[8000];
	int index = 0;
	int length;
	String lastString = "";
	
	public StringFactory append(String s) {
		arr[index++] = lastString = s;
		length+=s.length();
		return this;
	}
	
	public StringFactory append(int i) {
		return append(Integer.toString(i));
	}

	public StringFactory append(boolean b) {
		return append(Boolean.toString(b));
	}

	int length() {
		return length;
	}
	
	public char getLastCharacter() {
		return lastString.charAt(lastString.length()-1);
	}
	
	public StringFactory deleteLastCharacter() {
		arr[index-1] = lastString = lastString.substring(0, lastString.length()-1);
		length--;
		return this;
	}
	
	@Override
	public String toString() {
		String[] copy = new String[index];
		System.arraycopy(arr, 0, copy, 0, index);
		return String.join("", copy);
	}
}
