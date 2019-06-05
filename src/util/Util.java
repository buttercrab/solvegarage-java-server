package util;

import server.Commands;
import server.Server;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

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
     * @param from String of where command has logged
     * @param s    String to log
     * @param type type to log (differs the color)
     * @see Commands#log(String, String, int)
     */

    public static void log(String from, String s, int type) {
        ((Commands) Server.cmd).log(from, s, type);
    }

    public static class token {

        /**
         * Generates the random string contains 0-9, A-Z, a-z
         * Format of `{user id}:{random string}:{generated time}`
         *
         * @param id id to append to token
         * @return generated token
         */

        public static String generate(String id) {
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

        public static boolean validate(String token, String saved) {
            String[] savedParts = saved.split(":");
            if (!token.equals(saved)) return false;
            long curTiem = System.currentTimeMillis();
            return Long.valueOf(savedParts[2]) <= curTiem && curTiem - Long.valueOf(savedParts[2]) < 24 * 60 * 60 * 1000;
        }
    }

    /**
     * @see <a href="https://gist.github.com/stunstunstun/8dbc82bd86f38c9232139e0ba9a7d8ad">
     * https://gist.github.com/stunstunstun/8dbc82bd86f38c9232139e0ba9a7d8ad</a>
     */

    public static class RSA {

        public static KeyPair generateKeyPair() {
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048, new SecureRandom());
                return generator.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                Util.log("rsa", "No algorithm named `RSA`", Commands.ERR);
            }
            return null;
        }

        private static PublicKey generatePublicKey(byte[] encodedPublicKey) {
            try {
                return KeyFactory.getInstance("RSA").generatePublic(new PKCS8EncodedKeySpec(encodedPublicKey));
            } catch (Exception e) {
                Util.log("rsa", "Error on generating public key", Commands.ERR);
            }
            return null;
        }

        private static PrivateKey generatePrivateKey(byte[] encodedPrivateKey) {
            try {
                return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));
            } catch (Exception e) {
                Util.log("rsa", "Error on generating public key", Commands.ERR);
            }
            return null;
        }

        public static String encrypt(String plainText, byte[] encodedPublicKey) {
            PublicKey publicKey = RSA.generatePublicKey(encodedPublicKey);
            try {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] bytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
                return Base64.getEncoder().encodeToString(bytes);
            } catch (Exception e) {
                Util.log("rsa", "Error on encrypting", Commands.ERR);
            }
            return null;
        }

        public static String decrypt(String cipherText, byte[] encodedPrivateKey) {
            PrivateKey privateKey = RSA.generatePrivateKey(encodedPrivateKey);
            try {
                byte[] bytes = Base64.getDecoder().decode(cipherText);
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                return new String(cipher.doFinal(bytes), StandardCharsets.UTF_8);
            } catch (Exception e) {
                Util.log("rsa", "Error on decrypting", Commands.ERR);
            }
            return null;
        }

        public static String sign(String plainText, byte[] encodedPrivateKey) {
            try {
                Signature privateSignature = Signature.getInstance("SHA512withRSA");
                privateSignature.initSign(RSA.generatePrivateKey(encodedPrivateKey));
                privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8));
                byte[] signature = privateSignature.sign();
                return Base64.getEncoder().encodeToString(signature);
            } catch (Exception e) {
                Util.log("rsa", "Error on signing", Commands.ERR);
            }
            return null;
        }

        public static boolean verify(String plainText, String signature, byte[] encodedPublicKey) {
            PublicKey publicKey = RSA.generatePublicKey(encodedPublicKey);
            return RSA.verifySignature(plainText, signature, publicKey);
        }

        private static boolean verifySignature(String plainText, String signature, PublicKey publicKey) {
            Signature sig;
            try {
                sig = Signature.getInstance("SHA512withRSA");
                sig.initVerify(publicKey);
                sig.update(plainText.getBytes());
                return sig.verify(Base64.getDecoder().decode(signature));
            } catch (Exception e) {
                Util.log("rsa", "Error on verifying signiture", Commands.ERR);
            }
            return false;
        }
    }
}
