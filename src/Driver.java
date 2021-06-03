import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
/**
 * TCSS 487 A Sp 21: Cryptography
 * Group Project –cryptographic library & app
 * Anna Blanton, Caleb Chang and Taehong Kim
 *
 * References: the test methods from the tiny-sha3 implementation
 * by Dr. Markku-Juhani O. Saarinen
 * https://github.com/mjosaarinen/tiny_sha3
 *
 */

public class Driver {
    private static SecureRandom rand = new SecureRandom();
    private static Map<Integer, Character> HEX_MAP;

    public static void main(String[] args) throws IOException {
        HEX_MAP = new HashMap<Integer, Character>();
        HEX_MAP.put(0, '0');
        HEX_MAP.put(1, '1');
        HEX_MAP.put(2, '2');
        HEX_MAP.put(3, '3');
        HEX_MAP.put(4, '4');
        HEX_MAP.put(5, '5');
        HEX_MAP.put(6, '6');
        HEX_MAP.put(7, '7');
        HEX_MAP.put(8, '8');
        HEX_MAP.put(9, '9');
        HEX_MAP.put(10, 'A');
        HEX_MAP.put(11, 'B');
        HEX_MAP.put(12, 'C');
        HEX_MAP.put(13, 'D');
        HEX_MAP.put(14, 'E');
        HEX_MAP.put(15, 'F');

        Scanner userInput = new Scanner(System.in);
        String in;
        do {
            System.out.println("1) KMACXOF256 functionality");
            System.out.println("2) ECHDIES encryption/Schnorr signature functionality");
            System.out.println("3) Quit program");
            System.out.print("Select an option: ");
            in = userInput.next();
            while (!in.matches("[123]")) {
                System.out.println("Please select a valid option (enter 1 or 2)");
                in = userInput.next();
            }
            int selection = Integer.parseInt(in);
            switch (selection) {
                case 1:
                    kmacFunctions(userInput);
                    break;
                case 2:
                    eccFunctions(userInput);
                    break;
            }
        } while (!in.equals("3"));
    }

    /**
     * Kmac Function to give the options to the users
     * @param userInput scanner for user's input
     * @throws IOException
     */
    private static void kmacFunctions(Scanner userInput) throws IOException {
        System.out.println("1) Compute a plain cryptographic hash of a file");
        System.out.println("2) Compute a plain cryptographic hash of a given input");
        System.out.println("3) Encrypt a plaintext file under a passphrase");
        System.out.println("4) Decrypt a symmetric cryptogram with a given passphrase");
        System.out.println("5) Compute an authentication tag (MAC) of a given file under a given passphrase");
        System.out.print("Select an option: ");
        String in = userInput.next();
        while (!in.matches("[1-5]")) {
            System.out.println("Please select a valid option (enter a number 1-5)");
            in = userInput.next();
        }
        int selection = Integer.parseInt(in);
        switch (selection) {
            case 1:
                computeHashOfFile(userInput);
                break;
            case 2:
                computeHashOfInput(userInput);
                break;
            case 3:
                encryptFile(userInput);
                System.out.println("Cryptogram written to ./cryptogram.txt");1
                break;
            case 4:
                decryptFile(userInput);
                break;
            case 5:
                computeMAC(userInput);
                break;
        }
    }

    /**
     * Elliptical curve function to give the options to the users
     * @param userInput scanner for user's input
     * @throws IOException
     */
    private static void eccFunctions(Scanner userInput) throws IOException {
        System.out.println("1) Generate elliptic key pair from passphrase");
        System.out.println("2) Encrypt data file under elliptic public key file");
        System.out.println("3) Decrypt elliptic-encrypted file from passphrase");
        System.out.println("4) Encrypt given text under elliptic public key file");
        System.out.println("5) Decrypt given elliptic-encrypted text from passphrase");
        System.out.println("6) Sign file from passphrase and write signature to file");
        System.out.println("7) Verify given data file and its signature file under a public key file");
        System.out.println("8) Encrypt data file under recipient's public key and sign data file under your own private key");
        System.out.print("Select an option: ");
        String in = userInput.next();
        while (!in.matches("[1-8]")) {
            System.out.println("Please select a valid option (enter a number 1-7)");
            in = userInput.next();
        }
        int selection = Integer.parseInt(in);
        switch (selection) {
            case 1:
                generateEllipticKeyPair(userInput);
                break;
            case 2:
                System.out.print("Please enter the filepath for the file to be encrypted: ");
                String filePath = userInput.next();
                System.out.print("Please enter the filepath for the (Schnorr/ECDHIES) public key : ");
                String keyPath = userInput.next();
                encryptFileEllipticKey(filePath, keyPath, "./cryptogram.txt");
                System.out.println("Cryptogram written to ./cryptogram.txt");
                break;
            case 3:
                decryptEllipticFile(userInput);
                break;
            case 4:
                System.out.println("Please enter the text to be encrypted: ");
                userInput.nextLine();
                String text = userInput.nextLine();
                System.out.print("Please enter the filepath for the (Schnorr/ECDHIES) public key : ");
                keyPath = userInput.next();
                encryptTextEllipticKey(text, keyPath, "./cryptogram.txt");
                System.out.println("Cryptogram written to ./cryptogram.txt");
                break;
            case 5:
                decryptEllipticText(userInput);
                break;
            case 6:
                System.out.print("Please enter the filepath for the file to be signed: ");
                filePath = userInput.next();
                System.out.print("Please enter the passphrase: ");
                String key = userInput.next();
                signFile(filePath, key);
                System.out.println("Signature written to ./signature.txt");
                break;
            case 7:
                verifySignature(userInput);
                break;
            case 8:
                System.out.print("Please enter the filepath for the file to be encrypted/signed: ");
                filePath = userInput.next();
                System.out.print("Please enter the filepath for the (Schnorr/ECDHIES) public key : ");
                String publicKeyPath = userInput.next();
                System.out.println("Please enter your passphrase: ");
                key = userInput.next();
                encryptFileEllipticKey(filePath, publicKeyPath, "./cryptogram.txt");
                signFile("./cryptogram.txt", key);
                System.out.println("Cryptogram written to ./cryptogram.txt");
                System.out.println("Signature written to ./signature.txt");
        }
    }

    /**
     * Method to generate elliptic key pair from passphrase
     * @param userInput scanner user input
     * @throws FileNotFoundException
     */
    private static void computeHashOfFile(Scanner userInput) throws FileNotFoundException {
        System.out.print("Please enter the filepath: ");
        String filePath = userInput.next();
        byte[] md = new byte[64];
        KMACXOF256 kmac = new KMACXOF256();
        Scanner file = new Scanner(new File(filePath));
        int fileLength = 0;
        while (file.hasNextLine()) {
            fileLength += file.nextLine().length();
            if (file.hasNext()) fileLength++;
        }

        byte[] fileArray = new byte[fileLength];
        int i = 0;
        file = new Scanner(new File(filePath));
        while (file.hasNextLine()) {
            String line = file.nextLine();
            for (int j = 0; j < line.length(); j++) {
                fileArray[i] = (byte) line.charAt(j);
                i++;
            }
            if (file.hasNext()) {
                fileArray[i] = (byte) '\n';
                i++;
            }
        }
        kmac.kmacxof256(new byte[]{}, fileArray, fileArray.length, md, md.length, "D".getBytes());
        System.out.println(bytesToHex(md));
    }

    /**
     * Method to Compute a plain cryptographic hash of a given input
     * @param userInput scanner user input
     * @throws FileNotFoundException
     */
    private static void computeHashOfInput(Scanner userInput) {
        System.out.print("Please enter the text to be decrypted: ");
        String userInputText = userInput.next();

        KMACXOF256 kmac = new KMACXOF256();
        byte[] md = new byte[64];
        kmac.kmacxof256("".getBytes(),userInputText.getBytes(), 512, md, md.length, "D".getBytes());
        System.out.println(bytesToHex(md));
    }

    /**
     * Method to Encrypt a plaintext file under a passphrase
     * @param userInput scanner user input
     * @throws FileNotFoundException
     */
    private static void encryptFile(Scanner userInput) throws IOException {
        System.out.print("Please enter the filepath for the file to be encrypted: ");
        String filePath = userInput.next();
        System.out.print("Please enter the passphrase: ");
        String key = userInput.next();
        byte[] zArray = new byte[64];
        rand.nextBytes(zArray);
        byte[] ke_ka = new byte[128];
        KMACXOF256 kmac = new KMACXOF256();
        Scanner file = new Scanner(new File(filePath));
        int fileLength = 0;
        while (file.hasNextLine()) {
            fileLength += file.nextLine().length();
            if (file.hasNext()) fileLength++;
        }

        byte[] fileArray = new byte[fileLength];
        int i = 0;
        file = new Scanner(new File(filePath));
        while (file.hasNextLine()) {
            String line = file.nextLine();
            for (int j = 0; j < line.length(); j++) {
                fileArray[i] = (byte) line.charAt(j);
                i++;
            }
            if (file.hasNext()) {
                fileArray[i] = (byte) '\n';
                i++;
            }
        }

        kmac.kmacxof256(concat(zArray, key.getBytes()), new byte[]{}, 0, ke_ka, ke_ka.length, "S".getBytes());
        byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
        byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);
        byte[] c = new byte[fileLength];
        kmac.kmacxof256(ke, new byte[]{}, 0, c, c.length, "SKE".getBytes());
        for (i = 0; i < c.length; i++) {
            c[i] ^= fileArray[i];
        }

        byte[] t = new byte[64];
        kmac.kmacxof256(ka, fileArray, fileArray.length, t, t.length, "SKA".getBytes());
        FileWriter output = new FileWriter(new File("./cryptogram.txt"), false);
        output.append(bytesToHex(zArray) + "\n");
        output.append(bytesToHex(c) + "\n");
        output.append(bytesToHex(t));
        output.close();
    }

    /**
     * Method to decrypt a symmetric cryptogram with a given passphrase
     * @param userInput scanner user input
     * @throws FileNotFoundException
     */
    private static void decryptFile(Scanner userInput) throws IOException {
        System.out.print("Please enter the filepath of the cryptogram to be decrypted: ");
        String filePath = userInput.next();
        System.out.print("Please enter the passphrase: ");
        String key = userInput.next();
        Scanner cryptogram = new Scanner(new File(filePath));
        String zString = cryptogram.next();
        String cString = cryptogram.next();
        String tString = cryptogram.next();
        byte[] z = new byte[zString.length() / 2];
        byte[] c = new byte[cString.length() / 2];
        byte[] t = new byte[tString.length() / 2];

        for (int i = 0; i < zString.length() / 2; i++) {
            String byteString = "" + zString.charAt(i * 2) + zString.charAt(i * 2 + 1);
            z[i] = stringToByte(byteString);
        }

        for (int i = 0; i < cString.length() / 2; i++) {
            String byteString = "" + cString.charAt(i * 2) + cString.charAt(i * 2 + 1);
            c[i] = stringToByte(byteString);
        }

        for (int i = 0; i < tString.length() / 2; i++) {
            String byteString = "" + tString.charAt(i * 2) + tString.charAt(i * 2 + 1);
            t[i] = stringToByte(byteString);
        }

        byte[] ke_ka = new byte[128];

        KMACXOF256 kmac = new KMACXOF256();
        kmac.kmacxof256(concat(z, key.getBytes()), new byte[]{}, 0, ke_ka, ke_ka.length, "S".getBytes());
        byte[] m = new byte[c.length];
        byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
        byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);
        kmac.kmacxof256(ke, new byte[]{}, 0, m, m.length, "SKE".getBytes());
        for (int i = 0; i < m.length; i++) {
            m[i] ^= c[i];
        }

        byte[] tPrime = new byte[64];
        kmac.kmacxof256(ka, m, m.length, tPrime, tPrime.length, "SKA".getBytes());
        if (Arrays.equals(t, tPrime)) {
            FileWriter output = new FileWriter(new File("./plaintext.txt"), false);
            for (int i = 0; i < m.length; i++) {
                output.append((char) m[i]);
            }
            output.close();
            System.out.println("Decrypted file stored at ./plaintext.txt");
        } else {
            System.out.println("The passphrase is incorrect.");
        }
    }

    /**
     * Method to compute an authentication tag (MAC) of a given file under a given passphrase
     * @param userInput scanner user input
     * @throws FileNotFoundException
     */
    private static void computeMAC(Scanner userInput) throws FileNotFoundException {
        System.out.print("Please enter the filepath: ");
        String filePath = userInput.next();
        System.out.print("Please enter the passphrase: ");
        String pw = userInput.next();
        byte[] md = new byte[64];
        KMACXOF256 kmac = new KMACXOF256();
        Scanner file = new Scanner(new File(filePath));
        int fileLength = 0;
        while (file.hasNextLine()) {
            fileLength += file.nextLine().length();
            if (file.hasNext()) fileLength++;
        }

        byte[] fileArray = new byte[fileLength];
        int i = 0;
        file = new Scanner(new File(filePath));
        while (file.hasNextLine()) {
            String line = file.nextLine();
            for (int j = 0; j < line.length(); j++) {
                fileArray[i] = (byte) line.charAt(j);
                i++;
            }
            if (file.hasNext()) {
                fileArray[i] = (byte) '\n';
                i++;
            }
        }
        kmac.kmacxof256(pw.getBytes(), fileArray, fileArray.length, md, md.length, "T".getBytes());
        System.out.println(bytesToHex(md));
    }

    /**
     * Method to Generate elliptic key pair from passphrase
     * @param userInput scanner user input
     * @throws FileNotFoundException
     */
    private static void generateEllipticKeyPair(Scanner userInput) throws IOException {
        byte[] sArr= new byte[64];
        System.out.print("Please enter the passphrase: ");
        String key = userInput.next();
        KMACXOF256 kmac = new KMACXOF256();
        kmac.kmacxof256(key.getBytes(), new byte[0], 0, sArr, sArr.length, "K".getBytes());
        BigInteger s = new BigInteger(1, sArr);
        s = s.multiply(BigInteger.valueOf(4));
        ECPoint v = ECPoint.g.multiply(s);
        FileWriter output = new FileWriter(new File("./publickey.txt"), false);
        BigInteger publicKey = v.x;
        publicKey = publicKey.shiftLeft(1);
        if (v.y.testBit(0)) publicKey = publicKey.add(BigInteger.ONE);
        byte[] pkTempArr = publicKey.toByteArray();
        byte[] pkArr = new byte[66];
        for (int i = 0; i < pkTempArr.length; i++) {
            pkArr[pkArr.length - 1 - i] = pkTempArr[pkTempArr.length - 1 - i];
        }
        output.append(bytesToHex(pkArr));
        output.close();
        encryptTextEllipticKey(bytesToHex(s.toByteArray()), "./publickey.txt", "./privatekey.txt");
        System.out.println("522-bit public key written to ./publickey.txt");
        System.out.println("LSB is equal to LSB of y-coordinate; remaining 521 bits are equal to the x-coordinate left-shifted by one bit");
        System.out.println("Private key is " + bytesToHex(s.toByteArray()));
        System.out.println("Do not share this private key!");
        System.out.println("Encrypted private key also written to ./privatekey.txt");
    }

    /**
     * Method to Encrypt data file under elliptic public key file
     * @param filePath  the filepath for the file to be encrypted
     * @param keyPath the filepath for the (Schnorr/ECDHIES) public key
     * @param outputName the output name ./cryptogram.txt"
     * @throws IOException
     */
    private static void encryptFileEllipticKey(String filePath, String keyPath, String outputName) throws IOException {
        String key = "";
        BufferedReader br = new BufferedReader(new FileReader(keyPath));
        StringBuilder sb = new StringBuilder();
        key = br.readLine(); //prevent encrypted private key from getting added to public key var
        // k<-random(512);
        byte[] kArray = new byte[64];
        rand.nextBytes(kArray);
        BigInteger k = new BigInteger(1, kArray);

        //k<-4k
        k = k.multiply(BigInteger.valueOf(4));

        //Generate v
        int len = key.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(key.charAt(i), 16) << 4)
                    + Character.digit(key.charAt(i+1), 16));
        }
        byte[] temp = data;
        BigInteger x = new BigInteger(1, temp);
        boolean y = x.testBit(0);
        x = x.shiftRight(1);
        ECPoint v = new ECPoint(x, y);

        // w<-v*k
        ECPoint w = v.multiply(k);

        //z<-k*G
        ECPoint z = ECPoint.g.multiply(k);

        // (ke||ka) <- KMACXOF256(W_x,"",1024,"P");
        KMACXOF256 kmac = new KMACXOF256();
        byte[] ke_ka = new byte[128];

        kmac.kmacxof256(w.x.toByteArray(), new byte[]{}, 0, ke_ka, ke_ka.length, "P".getBytes());
        byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
        byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);

        //(c<-KMACXOF256 &&t<-KMACXOF256)
        // |m| & m
        int i = 0;
        Scanner file = new Scanner(new File(filePath));
        int fileLength = 0;
        while (file.hasNextLine()) {
            fileLength += file.nextLine().length();
            if (file.hasNext()) fileLength++;
        }

        byte[] fileArray = new byte[fileLength];
        file = new Scanner(new File(filePath));
        while (file.hasNextLine()) {
            String line1 = file.nextLine();
            for (int j = 0; j < line1.length(); j++) {
                fileArray[i] = (byte) line1.charAt(j);
                i++;
            }
            if (file.hasNext()) {
                fileArray[i] = (byte) '\n';
                i++;
            }
        }

        //c<-KMACXOF256
        byte[] c = new byte[fileLength];
        kmac.kmacxof256(ke, new byte[]{}, 0, c, c.length, "PKE".getBytes());
        for (i = 0; i < c.length; i++) {
            c[i] ^= fileArray[i];
        }
        //t<-KMACXOF256
        byte[] t = new byte[64];
        kmac.kmacxof256(ka, fileArray, fileArray.length, t, t.length, "PKA".getBytes());

        //Z
        BigInteger publicKey = z.x;
        publicKey = publicKey.shiftLeft(1);
        if (z.y.testBit(0)) publicKey = publicKey.add(BigInteger.ONE);
        byte[] pkTempArr = publicKey.toByteArray();
        byte[] pkArr = new byte[66];
        for (i = 0; i < pkTempArr.length; i++) {
            pkArr[pkArr.length - 1 - i] = pkTempArr[pkTempArr.length - 1 - i];
        }

        //cryptogram:(Z,c,t);
        FileWriter output = new FileWriter(new File(outputName), false);
        output.append(bytesToHex(pkArr) + "\n");
        output.append(bytesToHex(c)+"\n");
        output.append(bytesToHex(t));
        output.close();
    }

    /**
     * Method to decrypt elliptic-encrypted file from passphrase
     * @param userInput scanner user input
     * @throws IOException
     */
    private static void decryptEllipticFile(Scanner userInput) throws IOException {
        System.out.print("Please enter the filepath for the file to be decrypted: ");
        String filePath = userInput.next();
        System.out.print("Please enter the passphrase: ");
        String key = userInput.next();
        // Decode cryptogram from the text file
        Scanner cryptogram = new Scanner(new File(filePath));
        String zString = cryptogram.next();
        String cString = cryptogram.next();
        String tString = cryptogram.next();
        byte[] z = new byte[zString.length() / 2];
        byte[] c = new byte[cString.length() / 2];
        byte[] t = new byte[tString.length() / 2];

        for (int i = 0; i < zString.length() / 2; i++) {
            String byteString = "" + zString.charAt(i * 2) + zString.charAt(i * 2 + 1);
            z[i] = stringToByte(byteString);
        }

        for (int i = 0; i < cString.length() / 2; i++) {
            String byteString = "" + cString.charAt(i * 2) + cString.charAt(i * 2 + 1);
            c[i] = stringToByte(byteString);
        }

        for (int i = 0; i < tString.length() / 2; i++) {
            String byteString = "" + tString.charAt(i * 2) + tString.charAt(i * 2 + 1);
            t[i] = stringToByte(byteString);
        }

        BigInteger x = new BigInteger(1, z);
        boolean y = x.testBit(0);
        x = x.shiftRight(1);
        ECPoint Z = new ECPoint(x, y);

        // s <- KMACXOF256(pw, “”, 512, “K”)
        byte[] sArr = new byte[64];
        KMACXOF256 kmac = new KMACXOF256();
        kmac.kmacxof256(key.getBytes(), new byte[0], 0, sArr, sArr.length, "K".getBytes());

        // s <- 4s
        BigInteger s = new BigInteger(1, sArr);
        s = s.multiply(BigInteger.valueOf(4));

        // W <- s*Z
        ECPoint w = Z.multiply(s);;

        // (ke||ka) <- KMACXOF256(W_x,"",1024,"P");
        byte[] ke_ka = new byte[128];
        kmac.kmacxof256(w.x.toByteArray(), new byte[]{}, 0, ke_ka, ke_ka.length, "P".getBytes());
        byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
        byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);

        // m <- KMACXOF256(ke, “”, |c|,“PKE”) ^ c
        byte[] m = new byte[c.length];
        kmac.kmacxof256(ke, new byte[]{}, 0, m, c.length, "PKE".getBytes());
        for (int i = 0; i < m.length; i++) {
            m[i] ^= c[i];
        }

        // t’ <- KMACXOF256(ka, m, 512, “PKA”)
        byte[] tPrime = new byte[64];
        kmac.kmacxof256(ka, m, m.length, tPrime, tPrime.length, "PKA".getBytes());

        // accept if, and only if, t’ = t
        if (Arrays.equals(t, tPrime)) {
            FileWriter output = new FileWriter(new File("./plaintext.txt"), false);
            for (int i = 0; i < m.length; i++) {
                output.append((char) m[i]);
            }
            output.close();
            System.out.println("Decrypted file stored at ./plaintext.txt");
        } else {
            System.out.println("The passphrase is incorrect.");
        }
    }

    /**
     * Method to encrypt given text under elliptic public key file
     * @param text the text to be encrypted
     * @param keyPath the filepath for the (Schnorr/ECDHIES) public key
     * @param outputPath the out put path "Cryptogram written to ./cryptogram.txt"
     * @throws IOException
     */
    private static void encryptTextEllipticKey(String text, String keyPath, String outputPath) throws IOException {
        String key = "";
        BufferedReader br = new BufferedReader(new FileReader(keyPath));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }
        key = sb.toString();

        // k<-random(512);
        byte[] kArray = new byte[64];
        rand.nextBytes(kArray);
        BigInteger k = new BigInteger(1, kArray);

        //k<-4k
        k = k.multiply(BigInteger.valueOf(4));

        //Generate v
        int len = key.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(key.charAt(i), 16) << 4)
                    + Character.digit(key.charAt(i+1), 16));
        }
        byte[] temp = data;
        BigInteger x = new BigInteger(1, temp);
        boolean y = x.testBit(0);
        x = x.shiftRight(1);
        ECPoint v = new ECPoint(x, y);

        // w<-v*k
        ECPoint w = v.multiply(k);

        //z<-k*G
        ECPoint z = ECPoint.g.multiply(k);

        // (ke||ka) <- KMACXOF256(W_x,"",1024,"P");
        KMACXOF256 kmac = new KMACXOF256();
        byte[] ke_ka = new byte[128];
        kmac.kmacxof256(w.x.toByteArray(), new byte[]{}, 0, ke_ka, ke_ka.length, "P".getBytes());
        byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
        byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);

        //(c<-KMACXOF256 &&t<-KMACXOF256) // |m| & m
        int i = 0;
        int textlength = text.length();
        byte[] textArray = new byte[textlength];
        for (i =0; i<text.length(); i++){
            textArray[i] = (byte) text.charAt(i);
        }

        //c<-KMACXOF256
        byte[] c = new byte[textlength];
        kmac.kmacxof256(ke, new byte[]{}, 0, c, c.length, "PKE".getBytes());
        for (i = 0; i < c.length; i++) {
            c[i] ^= textArray[i];
        }
        //t<-KMACXOF256
        byte[] t = new byte[64];
        kmac.kmacxof256(ka, textArray, textArray.length, t, t.length, "PKA".getBytes());

        //Z
        BigInteger publicKey = z.x;
        publicKey = publicKey.shiftLeft(1);
        if (z.y.testBit(0)) publicKey = publicKey.add(BigInteger.ONE);
        byte[] pkTempArr = publicKey.toByteArray();
        byte[] pkArr = new byte[66];
        for (i = 0; i < pkTempArr.length; i++) {
            pkArr[pkArr.length - 1 - i] = pkTempArr[pkTempArr.length - 1 - i];
        }

        //cryptogram:(Z,c,t);
        FileWriter output = new FileWriter(new File(outputPath), false);
        output.append(bytesToHex(pkArr) + "\n");
        output.append(bytesToHex(c)+"\n");
        output.append(bytesToHex(t));
        output.close();
    }

    /**
     * Method to decrypt given elliptic-encrypted text from passphrase
     * @param userInput scanner user input
     * @throws IOException
     */
    private static void decryptEllipticText(Scanner userInput) throws IOException {
        System.out.print("Please enter the passphrase: ");
        String key = userInput.next();
        System.out.print("Please enter the Z string: ");
        String zString = userInput.next();
        System.out.print("Please enter the c string: ");
        String cString = userInput.next();
        System.out.print("Please enter the t string: ");
        String tString = userInput.next();
        byte[] z = new byte[zString.length() / 2];
        byte[] c = new byte[cString.length() / 2];
        byte[] t = new byte[tString.length() / 2];

        for (int i = 0; i < zString.length() / 2; i++) {
            String byteString = "" + zString.charAt(i * 2) + zString.charAt(i * 2 + 1);
            z[i] = stringToByte(byteString);
        }

        for (int i = 0; i < cString.length() / 2; i++) {
            String byteString = "" + cString.charAt(i * 2) + cString.charAt(i * 2 + 1);
            c[i] = stringToByte(byteString);
        }

        for (int i = 0; i < tString.length() / 2; i++) {
            String byteString = "" + tString.charAt(i * 2) + tString.charAt(i * 2 + 1);
            t[i] = stringToByte(byteString);
        }

        BigInteger x = new BigInteger(1,z);
        boolean y = x.testBit(0);
        x = x.shiftRight(1);
        ECPoint Z = new ECPoint(x, y); // The ending point

        // s <- KMACXOF256(pw, “”, 512, “K”)
        byte[] sArr = new byte[64];
        KMACXOF256 kmac = new KMACXOF256();
        kmac.kmacxof256(key.getBytes(), new byte[0], 0, sArr, sArr.length, "K".getBytes());

        // s <- 4s
        BigInteger s = new BigInteger(1, sArr);
        s = s.multiply(BigInteger.valueOf(4));

        // W <- s*Z
        ECPoint w = Z.multiply(s);;

        // (ke||ka) <- KMACXOF256(W_x,"",1024,"P");
        byte[] ke_ka = new byte[128];
        kmac.kmacxof256(w.x.toByteArray(), new byte[]{}, 0, ke_ka, ke_ka.length, "P".getBytes());
        byte[] ke = Arrays.copyOfRange(ke_ka, 0, 64);
        byte[] ka = Arrays.copyOfRange(ke_ka, 64, 128);

        // m <- KMACXOF256(ke, “”, |c|,“PKE”) ^ c
        byte[] m = new byte[c.length];
        kmac.kmacxof256(ke, new byte[]{}, 0, m, c.length, "PKE".getBytes());
        for (int i = 0; i < m.length; i++) {
            m[i] ^= c[i];
        }

        // t’ <- KMACXOF256(ka, m, 512, “PKA”)
        byte[] tPrime = new byte[64];
        kmac.kmacxof256(ka, m, m.length, tPrime, tPrime.length, "PKA".getBytes());

        String result = "";
        // accept if, and only if, t’ = t
        if (Arrays.equals(t, tPrime)) {
            for (int i = 0; i < m.length; i++) {
                result += (char)m[i];
            }
            System.out.println("Decrypted text: " + result);
        } else {
            System.out.println("The passphrase is incorrect.");
        }
    }

    /**
     * method to sign file from passphrase and write signature to file
     * @param filePath the filepath for the file to be signed
     * @param key the passphrase
     * @throws IOException
     */
    private static void signFile(String filePath, String key) throws IOException {
        //s<-KMACXOF256(pw,"",512,"k")
        KMACXOF256 kmac = new KMACXOF256();
        byte[] sArr= new byte[64];
        byte[] kArr= new byte[64];
        kmac.kmacxof256(key.getBytes(), new byte[0], 0, sArr, sArr.length, "K".getBytes());
        BigInteger s = new BigInteger(1, sArr);
        //s<-4s
        s = s.multiply(BigInteger.valueOf(4));

        //k<-KMACXOF256(s,m,512,"N")
        Scanner file = new Scanner(new File(filePath));
        int fileLength = 0;
        while (file.hasNextLine()) {
            fileLength += file.nextLine().length();
            if (file.hasNext()) fileLength++;
        }

        byte[] fileArray = new byte[fileLength];
        int i = 0;
        file = new Scanner(new File(filePath));
        while (file.hasNextLine()) {
            String line = file.nextLine();
            for (int j = 0; j < line.length(); j++) {
                fileArray[i] = (byte) line.charAt(j);
                i++;
            }
            if (file.hasNext()) {
                fileArray[i] = (byte) '\n';
                i++;
            }
        }
        kmac.kmacxof256(s.toByteArray(), fileArray, fileArray.length, kArr, kArr.length, "N".getBytes());
        BigInteger k = new BigInteger(1,  kArr);

        //k<-4k
        k = k.multiply(BigInteger.valueOf(4));
        //U<-K*G;
        ECPoint u = ECPoint.g.multiply(k);
        //h<-KMAXCOF256(Ux,m,512,"T");
        byte[] hArr=new byte[64];
        kmac.kmacxof256(u.x.toByteArray(), fileArray, fileArray.length, hArr, hArr.length, "T".getBytes());
        BigInteger h = new BigInteger(1, hArr);

        //z<-(k-hs)mod r
        BigInteger z = k.subtract(h.multiply(s)).mod(ECPoint.r);
        //signature:(h,z);
        FileWriter output = new FileWriter(new File("./signature.txt"), false);
        output.append(bytesToHex(hArr)+"\n");
        output.append(bytesToHex(z.toByteArray()));
        output.close();
    }

    /**
     * method to verify given data file and its signature file under a public key file
     * @param userInput scanner user input
     * @throws FileNotFoundException
     */
    private static void verifySignature(Scanner userInput) throws FileNotFoundException {
        System.out.print("Enter the filepath for the signed data file: ");
        String dataFilePath = userInput.next();
        System.out.print("Enter the filepath for the signature: ");
        String signaturePath = userInput.next();
        System.out.print("Enter the filepath for the public key: ");
        String keyPath = userInput.next();
        Scanner dataFile = new Scanner(new File(dataFilePath));
        int dataFileLength = 0;
        while (dataFile.hasNextLine()) {
            dataFileLength += dataFile.nextLine().length();
            if (dataFile.hasNext()) dataFileLength++;
        }

        byte[] dataFileArray = new byte[dataFileLength];
        int i = 0;
        dataFile = new Scanner(new File(dataFilePath));
        while (dataFile.hasNextLine()) {
            String line = dataFile.nextLine();
            for (int j = 0; j < line.length(); j++) {
                dataFileArray[i] = (byte) line.charAt(j);
                i++;
            }
            if (dataFile.hasNext()) {
                dataFileArray[i] = (byte) '\n';
                i++;
            }
        }
        Scanner signatureFile = new Scanner(new File(signaturePath));

        String hString = signatureFile.nextLine();
        byte[] hArray = new byte[hString.length() / 2];

        for (int j = 0; j < hString.length(); j+=2) {
            hArray[j / 2] = (byte) ((Character.digit(hString.charAt(j), 16) << 4)
                    + Character.digit(hString.charAt(j+1), 16));
        }

        String zString = signatureFile.nextLine();
        byte[] zArray = new byte[zString.length() / 2];
        for (int j = 0; j < zString.length(); j+=2) {
            zArray[j / 2] = (byte) ((Character.digit(zString.charAt(j), 16) << 4)
                    + Character.digit(zString.charAt(j+1), 16));
        }

        Scanner keyFile = new Scanner(new File(keyPath));
        String pkString = keyFile.nextLine();
        byte[] pkArray = new byte[pkString.length() / 2];
        for (int j = 0; j < pkString.length(); j+=2) {
            pkArray[j/2] = (byte) ((Character.digit(pkString.charAt(j), 16) << 4)
                    + Character.digit(pkString.charAt(j+1), 16));
        }

        BigInteger z = new BigInteger(1, zArray);
        BigInteger h = new BigInteger(1,hArray);
        BigInteger x = new BigInteger(1,pkArray);
        boolean y = x.testBit(0);
        x = x.shiftRight(1);
        ECPoint v = new ECPoint(x, y);
        ECPoint u = ECPoint.g.multiply(z).add(v.multiply(h));

        KMACXOF256 kmac = new KMACXOF256();
        byte[] hPrime = new byte[64];
        kmac.kmacxof256(u.x.toByteArray(), dataFileArray, dataFileArray.length, hPrime, hPrime.length, "T".getBytes());

        if (Arrays.equals(hArray, hPrime)) {
            System.out.println("The signature has been verified.");
        } else {
            System.out.println("The signature is incorrect for the given data file.");
        }
    }


    /**
     * Method convert string to byte
     * @param byteString string which need to be converted to byte
     * @return converted bytes from string
     */
    private static byte stringToByte(String byteString) {
        byte b = 0;
        Set<Map.Entry<Integer, Character>> entries = HEX_MAP.entrySet();
        Iterator<Map.Entry<Integer, Character>> itr = entries.iterator();
        Map.Entry<Integer, Character> curr = itr.next();
        while (curr.getValue() != byteString.charAt(0)) curr = itr.next();
        b += curr.getKey() << 4;
        itr = entries.iterator();
        curr = itr.next();
        while (curr.getValue() != byteString.charAt(1)) curr = itr.next();
        b += curr.getKey();
        return b;
    }

    /**
     * Method to convert bytes to hex
     * @param bytes byte array for bytes
     * @return string for hex character based on bytes[]
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int val1 = (bytes[j] & 0xF0) >>> 4;
            hexChars[j * 2] = HEX_MAP.get(val1);
            int val2 = bytes[j] & 0x0F;
            hexChars[j * 2 + 1] = HEX_MAP.get(val2);
        }
        return new String(hexChars);
    }

    /**
     * method to concatenate bytes array
     * @param a user's input first byte array
     * @param b user's input second byte array
     * @return new byte array through concatenation
     */
    private static byte[] concat(byte[] a, byte[] b) {
        byte[] ret = new byte[a.length + b.length];
        int i;
        for (i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }

        for (int j = 0; j < b.length; j++) {
            ret[j + i] = b[j];
        }
        return ret;
    }


    /**
     * method to test hex digit
     * @param ch character inputs to test
     * @return return with hex digit or -1
     */
    static int test_hexdigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return  ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return  ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return  ch - 'a' + 10;
        return -1;
    }

    /**
     * Method to test read hex
     * @param buf buffer byte array
     * @param str string to test to read
     * @param maxbytes number of max bytes to test
     * @return print out number of fails
     */
    static int test_readhex(byte[] buf, String str, int maxbytes) {
        int i, h, l;
        for (i = 0; 2 * i < str.length(); i++) {
            h = test_hexdigit(str.charAt(2 * i));
            if (2 * i + 1 >= str.length()) return i;
            l = test_hexdigit(str.charAt(2 * i + 1));
            buf[i] = (byte) ((h << 4) + l);
        }
        return i;
    }
    // returns zero on success, nonzero + stderr messages on failure
    static int test_sha3() {
        // message / digest pairs, lifted from ShortMsgKAT_SHA3-xxx.txt files
        // in the official package: https://github.com/gvanas/KeccakCodePackage
        final String[][] testvec = {
                {   // SHA3-224, corner case with 0-length message
                        "",
                        "6B4E03423667DBB73B6E15454F0EB1ABD4597F9A1B078E3F5B5A6BC7"
                },
                {   // SHA3-256, short message
                        "9F2FCC7C90DE090D6B87CD7E9718C1EA6CB21118FC2D5DE9F97E5DB6AC1E9C10",
                        "2F1A5F7159E34EA19CDDC70EBF9B81F1A66DB40615D7EAD3CC1F1B954D82A3AF"
                },
                {   // SHA3-384, exact block size
                        "E35780EB9799AD4C77535D4DDB683CF33EF367715327CF4C4A58ED9CBDCDD486" +
                                "F669F80189D549A9364FA82A51A52654EC721BB3AAB95DCEB4A86A6AFA93826D" +
                                "B923517E928F33E3FBA850D45660EF83B9876ACCAFA2A9987A254B137C6E140A" +
                                "21691E1069413848",
                        "D1C0FA85C8D183BEFF99AD9D752B263E286B477F79F0710B0103170173978133" +
                                "44B99DAF3BB7B1BC5E8D722BAC85943A"
                },
                {   // SHA3-512, multiblock message
                        "3A3A819C48EFDE2AD914FBF00E18AB6BC4F14513AB27D0C178A188B61431E7F5" +
                                "623CB66B23346775D386B50E982C493ADBBFC54B9A3CD383382336A1A0B2150A" +
                                "15358F336D03AE18F666C7573D55C4FD181C29E6CCFDE63EA35F0ADF5885CFC0" +
                                "A3D84A2B2E4DD24496DB789E663170CEF74798AA1BBCD4574EA0BBA40489D764" +
                                "B2F83AADC66B148B4A0CD95246C127D5871C4F11418690A5DDF01246A0C80A43" +
                                "C70088B6183639DCFDA4125BD113A8F49EE23ED306FAAC576C3FB0C1E256671D" +
                                "817FC2534A52F5B439F72E424DE376F4C565CCA82307DD9EF76DA5B7C4EB7E08" +
                                "5172E328807C02D011FFBF33785378D79DC266F6A5BE6BB0E4A92ECEEBAEB1",
                        "6E8B8BD195BDD560689AF2348BDC74AB7CD05ED8B9A57711E9BE71E9726FDA45" +
                                "91FEE12205EDACAF82FFBBAF16DFF9E702A708862080166C2FF6BA379BC7FFC2"
                }};

        int i, fails, msg_len, sha_len;
        byte[/*64, 64, 256*/] sha, buf, msg;

        fails = 0;
        for (i = 0; i < 4; i++) {
            sha = new byte[64];
            buf = new byte[64];
            msg = new byte[256];
            msg_len = test_readhex(msg, testvec[i][0], msg.length);
            sha_len = test_readhex(sha, testvec[i][1], sha.length);
            SHA3 test_sha3 = new SHA3();
            test_sha3.sha3(msg, msg_len, buf, sha_len);
            if (!Arrays.equals(buf, sha)) {
                System.out.println("[" + i + "] SHA3-" + sha_len * 8 + ", len" + msg_len + " test FAILED.\n");
                fails++;
            }
        }

        return fails;
    }

    /**
     * method to test shake
     * @return print out number of fails
     */
    static int test_shake() {
        // Test vectors have bytes 480..511 of XOF output for given inputs.
        // From http://csrc.nist.gov/groups/ST/toolkit/examples.html#aHashing

        final String[] testhex = {
                // SHAKE128, message of length 0
                "43E41B45A653F2A5C4492C1ADD544512DDA2529833462B71A41A45BE97290B6F",
                // SHAKE256, message of length 0
                "AB0BAE316339894304E35877B0C28A9B1FD166C796B9CC258A064A8F57E27F2A",
                // SHAKE128, 1600-bit test pattern
                "44C9FB359FD56AC0A9A75A743CFF6862F17D7259AB075216C0699511643B6439",
                // SHAKE256, 1600-bit test pattern
                "6A1A9D7846436E4DCA5728B6F760EEF0CA92BF0BE5615E96959D767197A0BEEB"
        };

        SHAKE test_shake = new SHAKE();
        int i, j, fails;
        byte[/*32*/] buf = new byte[32], ref = new byte[32];
        fails = 0;

        for (i = 0; i < 4; i++) {
            if ((i & 1) == 0) {             // test each twice
                test_shake.shake128_init();
            } else {
                test_shake.shake256_init();
            }

            if (i >= 2) {                   // 1600-bit test pattern
                Arrays.fill(buf, 0, 20, (byte) 0xA3);
                for (j = 0; j < 200; j += 20)
                    test_shake.shake_update(buf, 20);
            }
            test_shake.shake_xof();               // switch to extensible output

            for (j = 0; j < 512; j += 32)   // output. discard bytes 0..479
                test_shake.shake_out(buf, 32);
            // compare to reference
            test_readhex(ref, testhex[i], ref.length);
            if (!Arrays.equals(buf, ref)) {
                System.out.println("[" + i + "] SHAKE" + ((i & 1) == 1 ? 256 : 128) + ", len "+ (i >= 2 ? 1600 : 0) + " test FAILED.\n");
                System.out.println(Arrays.toString(buf));
                System.out.println(Arrays.toString(ref));
                fails++;
            }
        }
        return fails;
    }
}
