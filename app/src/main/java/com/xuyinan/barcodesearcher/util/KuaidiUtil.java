package com.xuyinan.barcodesearcher.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class KuaidiUtil {
    public static String getResult(String number) throws IOException {
        String info = KuaidiUtil.getInfo(number);
        try {
            return JSONUtil.getResult(new JSONObject(KuaidiUtil.getInfo(JSONUtil.getParams(new JSONObject(info)))));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getInfo(String number) throws IOException {
        String url = "http://www.kuaidi100.com/autonumber/autoComNum?text=" + number;
        return HttpUtil.getHttpResponse(url);
    }

    private static String getInfo(String[] params) throws IOException {
        String url = "http://www.kuaidi100.com/query?type=" +
                params[0] +
                "&postid=" +
                params[1] + "&id=1&valicode=&temp=0.029711376642808318";
        return HttpUtil.getHttpResponse(url);
    }
}
