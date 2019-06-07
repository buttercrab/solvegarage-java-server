package util;

import server.Commands;
import server.Server;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
     * @param query      gets the query string
     * @param parameters parameters map to contain
     * @throws UnsupportedEncodingException when it cannot be encoded
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
         * Verifying tokens when requested
         * check if it is same as saved and time passed
         *
         * @param token token to verify
         * @param saved token saved on server
         * @return true if it is validated token
         */

        public static boolean verify(String token, String saved) {
            String[] savedParts = saved.split("[:]");
            if (!token.equals(saved)) return false;
            long curTiem = System.currentTimeMillis();
            return Long.valueOf(savedParts[2]) <= curTiem && curTiem - Long.valueOf(savedParts[2]) < 24 * 60 * 60 * 1000;
        }
    }

    /**
     * Tool for RSA
     *
     * @see <a href="https://gist.github.com/stunstunstun/8dbc82bd86f38c9232139e0ba9a7d8ad">
     * https://gist.github.com/stunstunstun/8dbc82bd86f38c9232139e0ba9a7d8ad</a>
     */

    public static class RSA {

        /**
         * Generates the RSA key pair length of 2048
         *
         * @return generated key pair
         */

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

        /**
         * generates the public key from encoded key
         *
         * @param encodedPublicKey encoded public key
         * @return generated public key
         */

        private static PublicKey generatePublicKey(byte[] encodedPublicKey) {
            try {
                return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encodedPublicKey));
            } catch (Exception e) {
                Util.log("rsa", "Error on generating public key", Commands.ERR);
            }
            return null;
        }

        /**
         * generates the private key from encoded key
         *
         * @param encodedPrivateKey encoded private key
         * @return generated private key
         */

        private static PrivateKey generatePrivateKey(byte[] encodedPrivateKey) {
            try {
                return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));
            } catch (Exception e) {
                Util.log("rsa", "Error on generating public key", Commands.ERR);
            }
            return null;
        }

        /**
         * encrypts the text given with public key
         *
         * @param plainText        text to encrypt
         * @param encodedPublicKey encoded public key
         * @return encrypted text with public key
         */

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

        /**
         * decrypts the text given with private key
         *
         * @param cipherText        text to decrypt
         * @param encodedPrivateKey encoded private key
         * @return decrypted text with private key
         */

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

        /**
         * makes the sign of the text with private key
         *
         * @param plainText         text to encrypt
         * @param encodedPrivateKey encoded private key
         * @return made sign with text and private key
         */

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

        /**
         * verifies signature from text and public key
         *
         * @param plainText        text to check
         * @param signature        signature to check
         * @param encodedPublicKey public key to check
         * @return true or false whether it is verified
         */

        public static boolean verify(String plainText, String signature, byte[] encodedPublicKey) {
            PublicKey publicKey = RSA.generatePublicKey(encodedPublicKey);
            return RSA.verifySignature(plainText, signature, publicKey);
        }

        /**
         * Actual method for verify signature
         *
         * @param plainText text to verify
         * @param signature signature to verify
         * @param publicKey public key to verify
         * @return true if it is verified
         * @see Util.RSA#verify(String, String, byte[])
         */

        private static boolean verifySignature(String plainText, String signature, PublicKey publicKey) {
            Signature sig;
            try {
                sig = Signature.getInstance("SHA512withRSA");
                sig.initVerify(publicKey);
                sig.update(plainText.getBytes());
                return sig.verify(Base64.getDecoder().decode(signature));
            } catch (Exception e) {
                Util.log("rsa", "Error on verifying signature", Commands.ERR);
            }
            return false;
        }
    }

    /**
     * Tools for AES256
     */

    public static class AES {

        /**
         * Generates the random key for AES256.
         * Key would be 256 bytes long.
         *
         * @return byte array of datas
         */

        public static byte[] generateKey() {
            Random rnd = new Random();
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < 256 / 8; i++) {
                int r = rnd.nextInt(62);
                if (r < 10)
                    res.append((char) (48 + r));
                else if (r < 36)
                    res.append((char) (65 + r - 10));
                else
                    res.append((char) (97 + r - 36));
            }
            return res.toString().getBytes();
        }

        /**
         * Encrypts the plain text then encodes with base64
         *
         * @param plainText text to encrypt
         * @param key       key to encrypt
         * @return encrypted text
         */

        public static String encrypt(String plainText, byte[] key) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(new String(key).substring(16).getBytes()));
                byte[] bytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
                return Base64.getEncoder().encodeToString(bytes);
            } catch (Exception e) {
                Util.log("aes", "Error on encrypting", Commands.ERR);
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Decrypts the text
         *
         * @param encryptedText text to decrypt
         * @param key           text to decrypt
         * @return decrypted text
         */

        public static String decrypt(String encryptedText, byte[] key) {
            try {
                byte[] bytes = Base64.getDecoder().decode(encryptedText);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(new String(key).substring(16).getBytes()));
                return new String(cipher.doFinal(bytes));
            } catch (Exception e) {
                Util.log("aes", "Error on decrypting", Commands.ERR);
                e.printStackTrace();
            }
            return null;
        }
    }
}
