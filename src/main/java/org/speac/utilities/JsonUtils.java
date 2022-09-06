package org.speac.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Facilitates loading and dumping json files
 */
public final class JsonUtils {
	public static class JsonException extends Exception {
		public JsonException(String message) {
			super(message);
		}
	}

	private static final JSONParser PARSER = new JSONParser();

	public static JSONObject getRootObject(Path path) throws JsonException {
		return JsonUtils.getRootObject(path, false);
	}
	public static JSONObject getRootObject(Path path, boolean canBeEmpty) throws JsonException {
		try {
			return (JSONObject) JsonUtils.getRoot(path, canBeEmpty);
		} catch (ClassCastException exception) {
			throw new JsonException("root of file wasn't an object");
		}
	}

	public static JSONArray getRootArray(Path path) throws JsonException {
		return JsonUtils.getRootArray(path, false);
	}
	public static JSONArray getRootArray(Path path, boolean canBeEmpty) throws JsonException {
		try {
			return (JSONArray) JsonUtils.getRoot(path, canBeEmpty);
		} catch (ClassCastException exception) {
			throw new JsonException("root of file wasn't an array");
		}
	}

	private static Object getRoot(Path path, boolean canBeEmpty) throws JsonException {
		String contents = JsonUtils.read(path);

		Object root;
		try {
			root = JsonUtils.PARSER.parse(contents);
		} catch (ParseException exception) {
			throw new JsonException("invalid json syntax in file");
		}
		if (!canBeEmpty && root == null)
			throw new JsonException("file is empty");

		return root;
	}

	public static JSONObject getObject(JSONObject source, String key) throws JsonException {
		return JsonUtils.getObject(source, key, false);
	}
	public static JSONObject getObject(JSONObject source, String key, boolean canBeEmpty) throws JsonException {
		try {
			return (JSONObject) JsonUtils.get(source, key, canBeEmpty);
		} catch (ClassCastException exception) {
			throw new JsonException("value of '" + key + "' wasn't an object");
		}
	}

	public static JSONArray getArray(JSONObject source, String key) throws JsonException {
		return JsonUtils.getArray(source, key, false);
	}
	public static JSONArray getArray(JSONObject source, String key, boolean canBeEmpty) throws JsonException {
		try {
			return (JSONArray) JsonUtils.get(source, key, canBeEmpty);
		} catch (ClassCastException exception) {
			throw new JsonException("value of '" + key + "' wasn't an array");
		}
	}

	public static String getString(JSONObject source, String key) throws JsonException {
		return JsonUtils.getString(source, key, false);
	}
	public static String getString(JSONObject source, String key, boolean canBeEmpty) throws JsonException {
		try {
			return (String) JsonUtils.get(source, key, canBeEmpty);
		} catch (ClassCastException exception) {
			throw new JsonException("value of '" + key + "' wasn't a string");
		}
	}

	public static Integer getInteger(JSONObject source, String key) throws JsonException {
		return JsonUtils.getInteger(source, key, false);
	}
	public static Integer getInteger(JSONObject source, String key, boolean canBeEmpty) throws JsonException {
		try {
			return (Integer) JsonUtils.get(source, key, canBeEmpty);
		} catch (ClassCastException exception) {
			throw new JsonException("value of '" + key + "' wasn't an integer");
		}
	}

	public static Boolean getBoolean(JSONObject source, String key) throws JsonException {
		return JsonUtils.getBoolean(source, key, false);
	}
	public static Boolean getBoolean(JSONObject source, String key, boolean canBeEmpty) throws JsonException {
		try {
			return (Boolean) JsonUtils.get(source, key, canBeEmpty);
		} catch (ClassCastException exception) {
			throw new JsonException("value of '" + key + "' wasn't a boolean");
		}
	}

	private static Object get(JSONObject source, String key, boolean canBeEmpty) throws JsonException {
		Object data = source.get(key);
		if (!canBeEmpty && data == null)
			throw new JsonException("no value '" + key + "' was found");

		return data;
	}

	public static void dumpObject(JSONObject root, Path path) throws JsonException {
		JsonUtils.write(path, root.toJSONString());
	}

	public static void dumpArray(JSONArray root, Path path) throws JsonException {
		JsonUtils.write(path, root.toJSONString());
	}

	private static String read(Path path) throws JsonException {
		try {
			return Files.readString(path);
		} catch (IOException exception) {
			throw new JsonException("couldn't find or read from the file");
		}
	}

	private static void write(Path path, String contents) throws JsonException {
		try {
			Files.writeString(path, contents);
		} catch (IOException exception) {
			throw new JsonException("couldn't find or write to the file");
		}
	}
}
