package com.hconex.core.crypto;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Utility façade for cryptographic operations used in HConex.
 * <p>
 * Wraps {@link RC4Cipher} and provides convenience methods for key generation
 * and XOR-based transformations.  All stateless methods are thread-safe.
 * </p>
 */
public final class Encryption {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private Encryption() {
        throw new UnsupportedOperationException("Encryption is a utility class");
    }

    /**
     * Encrypts {@code data} with RC4 using the supplied key.
     * <p>
     * Creates a fresh {@link RC4Cipher} instance so this method is stateless
     * and safe to call concurrently.
     * </p>
     *
     * @param data data to encrypt (not modified)
     * @param key  RC4 key
     * @return encrypted byte array
     */
    public static byte[] rc4Encrypt(byte[] data, byte[] key) {
        return new RC4Cipher(key).cryptCopy(data);
    }

    /**
     * Decrypts {@code data} with RC4 using the supplied key.
     * <p>
     * RC4 encryption and decryption are identical operations.
     * </p>
     *
     * @param data data to decrypt (not modified)
     * @param key  RC4 key
     * @return decrypted byte array
     */
    public static byte[] rc4Decrypt(byte[] data, byte[] key) {
        return rc4Encrypt(data, key);
    }

    /**
     * Generates a cryptographically random key of the specified length.
     *
     * @param length desired key length in bytes
     * @return random key bytes
     * @throws IllegalArgumentException if {@code length} is not positive
     */
    public static byte[] generateKey(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Key length must be positive");
        }
        byte[] key = new byte[length];
        SECURE_RANDOM.nextBytes(key);
        return key;
    }

    /**
     * XORs every byte of {@code data} with the corresponding byte of
     * {@code key}, cycling the key if it is shorter than {@code data}.
     *
     * @param data data to process (not modified)
     * @param key  XOR key
     * @return XOR-processed byte array
     */
    public static byte[] xor(byte[] data, byte[] key) {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("XOR key must not be null or empty");
        }
        byte[] result = Arrays.copyOf(data, data.length);
        for (int i = 0; i < result.length; i++) {
            result[i] ^= key[i % key.length];
        }
        return result;
    }
}
