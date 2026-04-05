🛡️ P2P Encrypted File Sharing System
A high-performance, decentralized Peer-to-Peer (P2P) file transfer application built with Java. This project focuses on the CIA Triad (Confidentiality, Integrity, and Availability) by implementing end-to-end encryption and multi-threaded architecture.

🚀 Key Features
End-to-End Encryption (E2EE): Utilizes AES (128-bit) to ensure that file data is encrypted before transmission and decrypted only at the receiver's end.

Data Integrity Verification: Implements SHA-256 Hashing to generate a digital fingerprint of the file, ensuring the file hasn't been tampered with or corrupted during transit.

Multi-threaded Architecture: The Receiver is capable of handling multiple incoming file transfer requests simultaneously using Java's Multithreading (Thread-per-client model).

Decentralized (True P2P): No central server required; peers communicate directly via TCP/IP Sockets.

Fault Tolerance: Basic error handling for file existence, connection timeouts, and hash mismatches.

🛠️ Tech Stack
Language: Java (JDK 11+)

Networking: Java Sockets (TCP/IP)

Security: javax.crypto (AES), java.security (MessageDigest SHA-256)

Concurrency: Java Threads & Runnable Interface

📸 System Workflow
Handshake: Sender connects to Receiver's IP and Port.

Hashing: Sender calculates the SHA-256 hash of the original file.

Metadata Exchange: Sender sends File Name, Size, and the original Hash.

Secure Transfer: File is read in chunks, encrypted via AES, and streamed over the socket.

Verification: Receiver decrypts the file, calculates its own hash, and compares it with the Sender's hash to verify integrity.

🏃 How to Run
1. Clone the Repository
Bash
git clone https://github.com/subhamsahoo-4/P2P-Encrypted-File-Sharing.git
cd P2P-Encrypted-File-Sharing
2. Compile the Source Code
Bash
javac Sender.java Receiver.java
3. Start the Receiver (Server Mode)
Run this in the first terminal:

Bash
java Receiver
4. Start the Sender (Client Mode)
Run this in the second terminal:

Bash
java Sender
📝 Future Enhancements
[ ] RSA Key Exchange: Implement asymmetric encryption for secure AES key sharing.

[ ] GUI: Build a JavaFX or Swing-based user interface.

[ ] NAT Traversal: Support for transfers across different networks (Internet).
