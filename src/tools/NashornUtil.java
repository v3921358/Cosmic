package tools;

import java.util.List;
import java.util.ArrayList;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class NashornUtil {
	public static List<Integer> JSArrayToIntegerList(ScriptObjectMirror obj) {
		int[] iarr = obj.to(int[].class);
		List<Integer> intList = new ArrayList<Integer>(iarr.length);
		for(int i : iarr) {
			intList.add(i);
		}
		return intList;
	} 
}