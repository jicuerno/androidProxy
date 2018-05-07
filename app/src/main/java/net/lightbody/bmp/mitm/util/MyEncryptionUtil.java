package net.lightbody.bmp.mitm.util;

import net.lightbody.bmp.mitm.exception.ExportException;
import net.lightbody.bmp.mitm.exception.ImportException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.util.Objects;
import java.util.Random;

import javax.crypto.Cipher;

public class MyEncryptionUtil {
    public static String getSignatureAlgorithm(String messageDigest, Key signingKey) {
        return messageDigest + "with" + getDigitalSignatureType(signingKey);
    }

    public static String getDigitalSignatureType(Key signingKey) {
        if (signingKey instanceof ECKey) {
            return "ECDSA";
        } else if (signingKey instanceof RSAKey) {
            return "RSA";
        } else if (signingKey instanceof DSAKey) {
            return "DSA";
        } else {
            throw new IllegalArgumentException("Cannot determine digital signature encryption type for unknown key type: " + signingKey.getClass().getCanonicalName());
        }

    }

    public static BigInteger getRandomBigInteger(int bits) {
        return new BigInteger(bits, new Random());
    }

    public static boolean isRsaKey(Key key) {
        return "RSA".equals(key.getAlgorithm());
    }

    public static boolean isEcKey(Key key) {
        return "EC".equals(key.getAlgorithm());
    }

    public static void writePemStringToFile(File file, String pemDataToWrite) {
        try {
            byte[] bytes = pemDataToWrite.getBytes(StandardCharsets.US_ASCII);
            Objects.requireNonNull(bytes);
            FileUtils.writeByteArrayToFile(file, bytes);
        } catch (IOException e) {
            throw new ExportException("Unable to write PEM string to file: " + file.getName(), e);
        }
    }

    public static String readPemStringFromFile(File file) {
        try {
            byte[] fileContents = FileUtils.readFileToByteArray(file);
            return new String(fileContents, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            throw new ImportException("Unable to read PEM-encoded data from file: " + file.getName());
        }
    }

    public static boolean isUnlimitedStrengthAllowed() {
        try {
            return Cipher.getMaxAllowedKeyLength("AES") >= 256;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }

    }
}
