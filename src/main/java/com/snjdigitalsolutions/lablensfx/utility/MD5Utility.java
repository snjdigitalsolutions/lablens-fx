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

    public String calculate(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath));
            DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest)) {
            digestInputStream.transferTo(OutputStream.nullOutputStream());
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    public long getFileSize(String filePath){
        long size = 0L;
        File localFile = new File(filePath);
        if (localFile.exists()){
            size = localFile.length();
        }
        return size;
    }

    public byte[] getFileBytes(String filePath) throws IOException {
        byte[] data = null;
        File localFile = new File(filePath);
        if (localFile.exists()){
            data = Files.readAllBytes(localFile.toPath());
        }
        return data;
    }

}
