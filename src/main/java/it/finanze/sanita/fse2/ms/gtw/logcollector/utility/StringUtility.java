package it.finanze.sanita.fse2.ms.gtw.logcollector.utility;

import java.util.Base64;

import org.apache.commons.codec.binary.Hex;

public final class StringUtility {

    /**
     * Private constructor to avoid instantiation.
     */
    private StringUtility() {
        // Constructor intentionally empty.
    }

    /**
     * Encode in Base64 the byte array passed as parameter.
     *
     * @param input The byte array to encode.
     * @return The encoded byte array to String.
     */
    public static String encodeBase64(final byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    /**
     * Encodes the byte array passed as parameter in hexadecimal.
     *
     * @param input The byte array to encode.
     * @return The encoded byte array to String.
     */
    public static String encodeHex(final byte[] input) {
        return Hex.encodeHexString(input);
    }

    /**
     * Returns {@code true} if the String passed as parameter is null or empty.
     *
     * @param str String to validate.
     * @return {@code true} if the String passed as parameter is null or empty.
     */
    public static boolean isNullOrEmpty(final String str) {
        return str == null || str.isEmpty();
    }
}
