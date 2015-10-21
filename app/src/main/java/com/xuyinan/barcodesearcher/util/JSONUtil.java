package com.xuyinan.barcodesearcher.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {
    public static String[] getParams(JSONObject json) throws JSONException {
        String[] params = new String[2];
        JSONObject tmp = (JSONObject) json.getJSONArray("auto").get(0);
        params[0] = tmp.getString("comCode");
        params[1] = json.getString("num");
        return params;
    }

    public static String getResult(JSONObject json) throws JSONException {
        StringBuffer ret = new StringBuffer();
        JSONArray array = json.getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            JSONObject tmp = array.getJSONObject(i);
            ret.append(tmp.getString("time") + "\n" + tmp.getString("context") + "\n");
        }
        return ret.toString();
    }
}
