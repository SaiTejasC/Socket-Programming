import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConcHTTPAsk {
    static int port;
    public static void main(String[] args){
        parseArgs(args);
        try {
        ServerSocket serverSocket = new ServerSocket(port);
        while(true){    
        Socket clientSocket = serverSocket.accept();
        Runnable client = new MyRunnable(clientSocket);
        new Thread(client).start();
        }
                } catch (IOException e) {
            System.out.println("mistake occured");
            e.printStackTrace();
        }
    }

    private static void parseArgs(String[] args) {
        int argIndex = 0;
        try{
        while(argIndex < args.length){
            if (Integer. parseInt(args[argIndex]) == (int)Integer. parseInt(args[argIndex])){
                port = Integer. parseInt(args[argIndex]);
            }
            argIndex++;

        }
        }catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            // Exceeded array while parsing command line, or could
            // not convert port number argument to integer -- tell user
            // how to use the program
            System.out.println("mistake occured");
        }
	}
}

