import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Receiver {
    public static void main(String[] args) {
        String keyString = "MySecretKey12345"; // 16-byte key
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting for encrypted file on port " + port + "...");
            Socket socket = serverSocket.accept();
            System.out.println("Sender connected!");

            // 1. Setup Decryption (AES)
            SecretKeySpec secretKey = new SecretKeySpec(keyString.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // 2. Setup Streams
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();
            
            // CipherInputStream automatic data ko decrypt karega
            CipherInputStream cis = new CipherInputStream(socket.getInputStream(), cipher);
            FileOutputStream fos = new FileOutputStream("received_" + fileName);

            // 3. Receive and Save
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalRead = 0;
            while (totalRead < fileSize && (bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }

            System.out.println("Success! File saved as: received_" + fileName);
            
            fos.close();
            cis.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}