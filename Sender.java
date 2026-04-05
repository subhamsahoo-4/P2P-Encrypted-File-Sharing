import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class Sender {
    // Utility function to calculate SHA-256 Hash
    public static String getFileHash(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[8192];
        int bytesCount;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static void main(String[] args) {
        String keyString = "MySecretKey12345"; 
        String filePath = "test.txt"; 

        try {
            File file = new File(filePath);
            if (!file.exists()) return;

            // 1. Calculate Hash before sending
            String originalHash = getFileHash(file);
            System.out.println("Original SHA-256: " + originalHash);

            Socket socket = new Socket("localhost", 5000);
            
            // 2. Setup AES Encryption
            SecretKeySpec secretKey = new SecretKeySpec(keyString.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // 3. Send Metadata (Name, Size, and Hash)
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());
            dos.writeUTF(originalHash); // Hash bhej rahe hain verification ke liye

            // 4. Send Encrypted File Content
            CipherOutputStream cos = new CipherOutputStream(socket.getOutputStream(), cipher);
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }

            System.out.println("Encrypted file with Hash sent successfully!");
            cos.close(); fis.close(); socket.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}