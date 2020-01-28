package tools;

import java.util.List;
import java.util.ArrayList;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class NashornUtil {
	public static <T> List<T> JSArrayToList(ScriptObjectMirror obj, Class<T[]> type) {
		T[] tarr = obj.to(type);
		List<T> tList = new ArrayList<T>(tarr.length);
		for(T t : tarr) {
			tList.add(t);
		}
		return tList;
	} 
}