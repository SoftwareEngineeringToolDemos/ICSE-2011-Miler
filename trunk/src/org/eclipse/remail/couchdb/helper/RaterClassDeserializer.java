package org.eclipse.remail.couchdb.helper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * This class is a de-serializer used to convert a json object into
 * an object of the class Raters
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 *
 */
public class RaterClassDeserializer implements JsonDeserializer<Raters> {

	@Override
	public Raters deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Integer> rates = new ArrayList<Integer>();

		JsonObject obj = json.getAsJsonObject();

		Set<Map.Entry<String, JsonElement>> set = obj.entrySet();

		for (Entry<String, JsonElement> e : set) {
			names.add(e.getKey());
			rates.add(e.getValue().getAsInt());
		}

		int[] rs = new int[rates.size()];
		int cont = 0;
		for (int i : rates) {
			rs[cont] = i;
			cont++;
		}

		Raters r = new Raters(names.toArray(new String[names.size()]), rs);

		return r;
	}

}
