package com.snjdigitalsolutions.lablensfx.utility;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class MD5Utility {

    /**
     * Calculates the MD5 hash of a local file.
     *
     * @param filePath the absolute path of the file to hash
     * @return the lowercase hex MD5 digest string
     * @throws IOException              if the file cannot be read
     * @throws NoSuchAlgorithmException if the MD5 algorithm is unavailable
     */
    public String calculate(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath));
            DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest)) {
            digestInputStream.transferTo(OutputStream.nullOutputStream());
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    /**
     * Returns the size of a local file in bytes.
     *
     * @param filePath the absolute path of the file
     * @return the file size in bytes, or {@code 0} if the file does not exist
     */
    public long getFileSize(String filePath){
        long size = 0L;
        File localFile = new File(filePath);
        if (localFile.exists()){
            size = localFile.length();
        }
        return size;
    }

    /**
     * Reads all bytes of a local file into a byte array.
     *
     * @param filePath the absolute path of the file
     * @return the complete file contents as a byte array, or {@code null} if the file does not exist
     * @throws IOException if the file cannot be read
     */
    public byte[] getFileBytes(String filePath) throws IOException {
        byte[] data = null;
        File localFile = new File(filePath);
        if (localFile.exists()){
            data = Files.readAllBytes(localFile.toPath());
        }
        return data;
    }

}
