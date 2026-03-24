package com.hconex.crypto;

import com.hconex.core.crypto.Encryption;
import com.hconex.core.crypto.RC4Cipher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RC4Cipher} and {@link Encryption}.
 */
@DisplayName("RC4Cipher and Encryption")
class RC4CipherTest {

    @Test
    @DisplayName("RC4 encrypt then decrypt returns original data")
    void encryptDecrypt_roundTrip() {
        byte[] key = "testKey123".getBytes();
        byte[] original = "Hello Habbo!".getBytes();

        byte[] encrypted = Encryption.rc4Encrypt(original, key);
        byte[] decrypted = Encryption.rc4Decrypt(encrypted, key);

        assertArrayEquals(original, decrypted);
    }

    @Test
    @DisplayName("RC4 encrypted bytes differ from plaintext")
    void encrypt_producesDifferentBytes() {
        byte[] key = "secretKey".getBytes();
        byte[] data = "plaintext".getBytes();

        byte[] encrypted = Encryption.rc4Encrypt(data, key);

        assertFalse(java.util.Arrays.equals(data, encrypted));
    }

    @Test
    @DisplayName("RC4Cipher throws on null key")
    void rc4_nullKey_throws() {
        assertThrows(IllegalArgumentException.class, () -> new RC4Cipher(null));
    }

    @Test
    @DisplayName("RC4Cipher throws on empty key")
    void rc4_emptyKey_throws() {
        assertThrows(IllegalArgumentException.class, () -> new RC4Cipher(new byte[0]));
    }

    @Test
    @DisplayName("Encryption.generateKey returns correct length")
    void generateKey_correctLength() {
        byte[] key = Encryption.generateKey(16);
        assertEquals(16, key.length);
    }

    @Test
    @DisplayName("Encryption.generateKey throws for non-positive length")
    void generateKey_nonPositive_throws() {
        assertThrows(IllegalArgumentException.class, () -> Encryption.generateKey(0));
    }

    @Test
    @DisplayName("Encryption.xor is reversible")
    void xor_reversible() {
        byte[] data = "test data".getBytes();
        byte[] key = "key".getBytes();

        byte[] xored = Encryption.xor(data, key);
        byte[] restored = Encryption.xor(xored, key);

        assertArrayEquals(data, restored);
    }

    @Test
    @DisplayName("RC4Cipher cryptCopy does not modify original array")
    void cryptCopy_doesNotModifyOriginal() {
        byte[] key = "myKey".getBytes();
        byte[] data = "data".getBytes();
        byte[] original = data.clone();

        RC4Cipher cipher = new RC4Cipher(key);
        cipher.cryptCopy(data);

        assertArrayEquals(original, data);
    }
}
