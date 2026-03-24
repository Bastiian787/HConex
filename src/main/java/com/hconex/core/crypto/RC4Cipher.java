package com.hconex.core.crypto;

/**
 * RC4 (Rivest Cipher 4) stream cipher implementation.
 * <p>
 * RC4 is used by Habbo Hotel to encrypt certain parts of the communication.
 * This implementation is intentionally minimal and stateful: after
 * initialisation with a key, successive calls to {@link #crypt(byte[])} will
 * process the byte stream incrementally (i.e. the internal state carries over
 * between calls).
 * </p>
 * <p>
 * <strong>Thread safety:</strong> instances are <em>not</em> thread-safe.
 * Use a separate instance per connection.
 * </p>
 */
public final class RC4Cipher {

    private final int[] state = new int[256];
    private int i = 0;
    private int j = 0;

    /**
     * Initialises the RC4 cipher with the given key.
     *
     * @param key the encryption key (must not be {@code null} or empty)
     * @throws IllegalArgumentException if the key is null or empty
     */
    public RC4Cipher(byte[] key) {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("RC4 key must not be null or empty");
        }
        init(key);
    }

    /**
     * Key-scheduling algorithm (KSA).
     */
    private void init(byte[] key) {
        for (int k = 0; k < 256; k++) {
            state[k] = k;
        }
        int jj = 0;
        for (int k = 0; k < 256; k++) {
            jj = (jj + state[k] + (key[k % key.length] & 0xFF)) & 0xFF;
            swap(k, jj);
        }
    }

    /**
     * Encrypts or decrypts (RC4 is symmetric) the given bytes in-place.
     *
     * @param data the bytes to process; modified in-place
     * @return the same {@code data} array for convenience
     */
    public byte[] crypt(byte[] data) {
        for (int k = 0; k < data.length; k++) {
            i = (i + 1) & 0xFF;
            j = (j + state[i]) & 0xFF;
            swap(i, j);
            int keyByte = state[(state[i] + state[j]) & 0xFF];
            data[k] = (byte) (data[k] ^ keyByte);
        }
        return data;
    }

    /**
     * Returns a new encrypted/decrypted copy of the input without modifying it.
     *
     * @param data the bytes to process
     * @return a new byte array containing the processed data
     */
    public byte[] cryptCopy(byte[] data) {
        byte[] copy = java.util.Arrays.copyOf(data, data.length);
        return crypt(copy);
    }

    private void swap(int a, int b) {
        int tmp = state[a];
        state[a] = state[b];
        state[b] = tmp;
    }
}
