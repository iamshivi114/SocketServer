import java.io.*;
import java.net.*;
import java.util.*;

public class SocketServer {
    private static final int TCP_PORT = 17;
    private static final int UDP_PORT = 17;
    private static final int BUFFER_SIZE = 512;
    private static final String[] QUOTES = {
        "As you wish.",
        "You keep using that word.",
        "My way's not very sportsman-like.",
        "I just want you to feel you're doing well.",
        "But for now, rest well and dream of large women.",
        "You're trying to kidnap what I've rightfully stolen.",
        "Never go against a Sicilian when death is on the line!",
        // Quotes from my favorite movie.
        "May the force be with you!",
        "Fear leads to anger. Anger leads to hate. Hate leads to suffering.",
        "The greatest teacher, failure is."
    };
    private static final Random RANDOM = new Random();
    private static final List<String> USED_QUOTES = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket tcpSocket = new ServerSocket(TCP_PORT);
            DatagramSocket udpSocket = new DatagramSocket(UDP_PORT);
            byte[] buffer = new byte[BUFFER_SIZE];
            System.out.println("Server started...");
            while(true) {
                Socket clientSocket = tcpSocket.accept();
                Thread tcpThread = new Thread(() -> {
                    try {
                        String quote = getRandomQuote();
                        USED_QUOTES.add(quote);
                        clientSocket.getOutputStream().write(quote.getBytes());
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                tcpThread.start();
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(requestPacket);
                Thread udpThread = new Thread(() -> {
                    try {
                        String quote = getRandomQuote();
                        USED_QUOTES.add(quote);
                        DatagramPacket responsePacket = new DatagramPacket(quote.getBytes(), quote.getBytes().length, requestPacket.getAddress(), requestPacket.getPort());
                        udpSocket.send(responsePacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                udpThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String getRandomQuote() {
        String quote = QUOTES[RANDOM.nextInt(QUOTES.length)];
        if (USED_QUOTES.size() == QUOTES.length) {
            USED_QUOTES.clear();
        }
        while (USED_QUOTES.contains(quote)) {
            quote = QUOTES[RANDOM.nextInt(QUOTES.length)];
        }
        return quote;
    }
}


