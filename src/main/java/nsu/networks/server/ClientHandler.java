package nsu.networks.server;

import java.io.*;
import java.net.Socket;
import java.util.Date;

import static java.lang.Long.parseLong;

public class ClientHandler {
    public final static int BUFFER_SIZE = 1048576; //Size of the reading buffer from TCP socket
    public final static long SPEED_TEST_DELAY = 3; //seconds

    private DataInputStream in;     //TCP input stream
    private DataOutputStream out;   //TCP output stream

    private String filePath;
    private BufferedOutputStream fileOutputStream;
    private String fileName;
    private long fileLength;

    public ClientHandler(Socket socket) {
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.getMessage();
        }

        new Thread(() -> {
            try {
                File folder = new File("uploads");
                if (!folder.exists()) folder.mkdir();
                this.filePath = "uploads" + System.getProperty("file.separator");
                getFileNameAndSize();
                System.out.println("Starting receiving of " + fileName + " size = " + getSizeString(fileLength));
                this.fileOutputStream = new BufferedOutputStream(new FileOutputStream(filePath));
                byte[] byteArray = new byte[BUFFER_SIZE];
                //Start loading
                long loadedBytes = 0;
                Date startLoading = new Date();
                //start current interval
                long startInterval = startLoading.getTime();
                long startBytes = 0;
                while (loadedBytes < fileLength) {
                    int bytesRead = in.read(byteArray, 0, byteArray.length);
                    Date time = new Date();
                    if (time.getTime() - startInterval > SPEED_TEST_DELAY * 1000) {
                        speedTest(startLoading, time, startBytes, loadedBytes);
                        startInterval = time.getTime();
                        startBytes = loadedBytes;
                    }
                    if (bytesRead >= 0) loadedBytes += bytesRead;
                    else {
                        System.out.println("File did not loaded correctly!");
                        sendMessage("File did not loaded correctly!");
                        break;
                    }
                    fileOutputStream.write(byteArray, 0, bytesRead);
                    fileOutputStream.flush();
                }
                if (loadedBytes == fileLength) {
                    Date time = new Date();
                    System.out.print("It took ");
                    speedTest(startLoading, time, startBytes, loadedBytes);
                    System.out.println("Done!");
                    sendMessage("Successful completion of the download");
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                try {
                    if (fileOutputStream != null) fileOutputStream.close();
                    if (in != null) in.close();
                    if (out != null) out.close();
                    socket.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    private void speedTest(Date startLoading, Date time, long startBytes, long loadedBytes) throws IOException {

        long timeFromStartLoading = time.getTime() - startLoading.getTime();
        String message;
        if (timeFromStartLoading > 1000) {
            long instantSpeed = (loadedBytes - startBytes) / SPEED_TEST_DELAY;
            long absolutSpeed = (loadedBytes) / (timeFromStartLoading / 1000);
            message = timeFromStartLoading / 1000 + " seconds"
                    + "\n\tInstant speed = " + getSizeString(instantSpeed)
                    + "/sec\n\tAverage speed = " + getSizeString(absolutSpeed)
                    + "/sec\n\tTotal downloaded " + getSizeString(loadedBytes);
        } else {
            double instantSpeed = (double) (loadedBytes - startBytes);
            double absolutSpeed = (double) (loadedBytes);
            message = timeFromStartLoading + " milliseconds"
                    + "\n\tInstant speed = " + instantSpeed + " bytes"
                    + "/sec\n\tAverage speed = " + absolutSpeed + " bytes"
                    + "/sec\n\tTotal downloaded " + getSizeString(loadedBytes);
        }

        System.out.println(message);
    }

    private void getFileNameAndSize() throws IOException {
        String inputMessage;
        try {
            inputMessage = in.readUTF();
            //System.out.println("Receive: " + inputMessage);
        } catch (IOException exception) {
            throw new IOException("ERROR with reading");
        }
        String[] words = inputMessage.split(";");
        filePath += words[0];
        fileName = words[0];
        fileLength = parseLong(words[1]);
    }

    public void sendMessage(String msg) throws IOException {
        try {
            System.out.println("Send -> " + ": " + msg);
            out.writeUTF(msg);
            out.flush();
        } catch (IOException exception) {
            throw new IOException("ERROR with sending message");
        }
    }

    private String getSizeString(long fileLength) {
        long size = fileLength;
        int count = 0;
        long fraction = 0;
        while (size / 1024 > 0) {
            fraction = size % 1024;
            size /= 1024;
            count++;
        }
        String strSize = Integer.toString((int) size);
        fraction = fraction * 1000 / 1024;
        if (fraction != 0) {
            strSize = strSize + "," + (int) fraction;
        }
        switch (count) {
            case 1 -> strSize += " KBytes";
            case 2 -> strSize += " MBytes";
            case 3 -> strSize += " GBytes";
            case 4 -> strSize += " TBytes";
        }
        return strSize;
    }
}
