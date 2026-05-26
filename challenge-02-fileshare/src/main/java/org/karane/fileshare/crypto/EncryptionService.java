package org.karane.fileshare.crypto;

public interface EncryptionService {
    byte[] encrypt(byte[] data);
    byte[] decrypt(byte[] data);
}
