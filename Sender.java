import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Sender {
    public static void main(String[] args) {
        String keyString = "MySecretKey12345"; // Same 16-byte key
        String host = "localhost";
        int port = 5000;
        String filePath = "test.txt"; // Ensure this file exists in the folder

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Error: File 'test.txt' nahi mili!");
                return;
            }

            Socket socket = new Socket(host, port);
            System.out.println("Connected to Receiver!");

            // 1. Setup Encryption (AES)
            SecretKeySpec secretKey = new SecretKeySpec(keyString.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // 2. Setup Streams
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            // CipherOutputStream automatic data ko encrypt karke bhejega
            CipherOutputStream cos = new CipherOutputStream(socket.getOutputStream(), cipher);
            FileInputStream fis = new FileInputStream(file);

            // 3. Read and Send
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }

            System.out.println("Encrypted file sent successfully!");

            fis.close();
            cos.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}