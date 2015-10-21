package com.xuyinan.barcodesearcher.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static String getHttpResponse(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            return readIt(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static String readIt(InputStream stream) throws IOException {
        int len = 500;
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer;
        StringBuffer ret = new StringBuffer();
        while (true) {
            buffer = new char[len];
            if (reader.read(buffer) != -1) {
                ret.append(buffer);
            } else {
                break;
            }
        }
        return ret.toString();
    }
}
