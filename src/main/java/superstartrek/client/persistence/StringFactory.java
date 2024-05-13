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

	// Since the StarMap is fixed size, there's an upper limit of objects in it and the serialized size, which
	// currently (may 2024) is about 50k. Since arrays aren't bound-checked in compiled JS, the
	// initial capacity doesn't mean anything.
	
	// This is an array of strings. Concatenating them produces the final string.
	String[] arr=new String[100000];
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
	
	//doesn't check for negative length etc because this method is used only in the StarMapSerializer
	//where such a condition doesn't occur
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
