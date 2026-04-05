import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class Receiver {
    // Utility function (Same as Sender)
    public static void main(String[] args) {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Multi-threaded Receiver started on port " + port);

            while (true) {
                // Naye connection ka wait karo
                Socket socket = serverSocket.accept();
                System.out.println("\n[NEW CONNECTION] Sender connected: " + socket.getInetAddress());

                // Har naye sender ke liye ek naya thread banao
                Thread clientThread = new Thread(new ClientHandler(socket));
                clientThread.start(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class ClientHandler implements Runnable {
    private Socket socket;
    private String keyString = "MySecretKey12345";

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // 1. Setup AES Decryption
            SecretKeySpec secretKey = new SecretKeySpec(keyString.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // 2. Read Metadata
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();
            String expectedHash = dis.readUTF();

            // 3. Receive and Decrypt File
            CipherInputStream cis = new CipherInputStream(socket.getInputStream(), cipher);
            File receivedFile = new File("received_" + System.currentTimeMillis() + "_" + fileName); 
            FileOutputStream fos = new FileOutputStream(receivedFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            cis.close();

            // 4. Verify Integrity (Same getFileHash function)
            String calculatedHash = getFileHash(receivedFile);
            if (calculatedHash.equals(expectedHash)) {
                System.out.println(" [" + fileName + "] Success! Integrity Verified.");
            } else {
                System.out.println(" [" + fileName + "] ALERT: Hash Mismatch!");
            }

            socket.close();
        } catch (Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }
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
}