package util;

import server.Commands;
import server.Server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
     * Wrapper function of logging function
     *
     * @see Commands#log(String, String, int)
     *
     * @param from String of where command has logged
     * @param s    String to log
     * @param type type to log (differs the color)
     */

    public static void log(String from, String s, int type) {
        ((Commands) Server.cmd).log(from, s, type);
    }

    /**
     * Generates the random string contains 0-9, A-Z, a-z
     * Format of `{user id}:{random string}:{generated time}`
     *
     * @param id id to append to token
     * @return generated token
     */

    public static String generateToken(String id) {
        Random rnd = new Random();
        StringBuilder res = new StringBuilder(id);
        res.append(":");
        for (int i = 0; i < 30; i++) {
            int r = rnd.nextInt(62);
            if (r < 10)
                res.append((char) (48 + r));
            else if (r < 36)
                res.append((char) (65 + r - 10));
            else
                res.append((char) (97 + r - 36));
        }
        res.append(":");
        res.append(System.currentTimeMillis());
        return res.toString();
    }

    /**
     * Validating tokens when requested
     * check if it is same as saved and time passed
     *
     * @param token token to validate
     * @param saved token saved on server
     * @return true if it is validated token
     */

    public static boolean validateToken(String token, String saved) {
        String[] savedParts = saved.split(":");
        if (!token.equals(saved)) return false;
        long curTiem = System.currentTimeMillis();
        return Long.valueOf(savedParts[2]) <= curTiem && curTiem - Long.valueOf(savedParts[2]) < 24 * 60 * 60 * 1000;
    }
}
