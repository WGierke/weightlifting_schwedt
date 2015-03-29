package de.schwedt.weightlifting.app.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

    JSONArray jsonValues;

    public JsonParser() {

    }

    public boolean getJsonFromString(String json_string) {
        try {
            jsonValues = new JSONArray(json_string);
            for (int i = 0; i < jsonValues.length(); i++) {
                JSONObject jsonObject = jsonValues.getJSONObject(i);
            }
        } catch (Exception e) {
            jsonValues = null;
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getValue(int index, String key) {
        String res = "";
        try {
            JSONObject jsonObject = jsonValues.getJSONObject(index);
            res = jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return res;
    }

    public JSONArray getJsonArray(String key) {
        try {
            String res;
            JSONObject jsonObject = getJsonObject(0);
            res = jsonObject.getString(key);
            JSONArray array = new JSONArray(res);
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public JSONObject getJsonObject(int index) {
        try {
            JSONObject jsonObject = jsonValues.getJSONObject(index);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}