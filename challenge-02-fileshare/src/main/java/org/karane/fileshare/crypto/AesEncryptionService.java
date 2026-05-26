package org.karane.fileshare.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

public class AesEncryptionService implements EncryptionService {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;

    private final SecretKey secretKey;

    public AesEncryptionService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, new SecureRandom());
            this.secretKey = keyGen.generateKey();
        } catch (Exception e) {
            throw new CryptoException("Failed to initialise AES key", e);
        }
    }

    public AesEncryptionService(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public byte[] encrypt(byte[] data) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(data);

            // prepend IV so decrypt can recover it
            byte[] result = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(iv, 0, result, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, result, IV_LENGTH, encrypted.length);
            return result;
        } catch (Exception e) {
            throw new CryptoException("Encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] data) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(data, 0, iv, 0, IV_LENGTH);

            byte[] cipherText = new byte[data.length - IV_LENGTH];
            System.arraycopy(data, IV_LENGTH, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new CryptoException("Decryption failed", e);
        }
    }
}
