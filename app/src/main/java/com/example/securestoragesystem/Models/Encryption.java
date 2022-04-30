package com.example.securestoragesystem.Models;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.CipherInputStream;

public class Encryption {
    public static String secretKey = "mydifficultpassw";
    public static void encrypt(Context c, Uri uri, String folder, String file, String fileType)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // InputStream for reading the selected File.
        FileInputStream fis = (FileInputStream) c.getContentResolver().openInputStream(uri);
        // Output Stream for writing to a desired location.
        FileOutputStream fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS+"/"+"SecureStorageSystem_"+folder+"_"+file+fileExtension(fileType)));

        // Secret Key Length is 16 bytes (128 bits)
        SecretKeySpec sks = new SecretKeySpec(secretKey.getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Writing and encrypting the bytes to the desired location.
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }

    public static void decrypt(String folder, String file, String fileType) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Reading the encrypted file from the desired location.
        FileInputStream fis = new FileInputStream(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS+"/SecureStorageSystem_"+folder+"_"+file+fileExtension(fileType)));
        // Output stream for writing the decrypted file to a desired location.
        FileOutputStream fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS+"/"+"SecureStorageSystem_Decrypted_"+folder+"_"+file+fileExtension(fileType)));
        // Secret Key
        SecretKeySpec sks = new SecretKeySpec(secretKey.getBytes(),
                "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        // Writing and the decrypting the file and saving it to desired location.
        int b;
        byte[] d = new byte[8];
        while ((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }

    public static String fileExtension (String s) {
        String[] extension = s.split("/");
        switch (extension[0]) {
            case "image":
                return ".jpeg";
            case "application":
                if (extension[1].charAt(0) == 'v') {
                    return ".docx";
                } else {
                    return ".pdf";
                }
            case "audio":
                return ".mp3";
            case "video":
                return ".mp4";
            case "text":
                return ".txt";
            default:
                return ".zip";
        }
    }
}

