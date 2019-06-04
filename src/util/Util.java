package util;

import server.Commands;
import server.Server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Util {

    /**
     * Gets url query string and parse into key-value data.
     * parameters would be {@code Map} with {@code String} keys and
     * {@code String} if key in url is used once
     * {@code List<String>} if key in url is used multiple.
     * <p>
     * Example Code:
     * <pre>
     *     {@code
     *     Util.parseQuery(httpExchange.getRequestURI().getRawQuery(), params);
     *     }
     * </pre>
     *
     * @param query gets the query string
     */
    public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
        if (query != null) {
            String[] pairs = query.split("[&]");
            for (String pair : pairs) {
                String[] param = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        @SuppressWarnings("unchecked")
                        List<String> values = (List<String>) obj;
                        values.add(value);
                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }

    /**
     * Wrapper function to log to stdout
     * @see Commands
     *
     * @param s String to log to stdout
     */

    public static void log(String s, int type) {
        ((Commands) Server.cmd).log(s, type);
    }
}
