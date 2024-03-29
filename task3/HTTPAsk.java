import java.net.*;
import java.util.Arrays;

import tcpclient.TCPClient;

import java.io.*;


public class HTTPAsk {
    static int port;
    public static void main(String[] args){
        parseArgs(args);
        try {
        ServerSocket serverSocket = new ServerSocket(port);
        while(true){       
        Socket clientSocket = serverSocket.accept();
        OutputStream clientOutput = clientSocket.getOutputStream();
        InputStream clientInput = clientSocket.getInputStream();
        ByteArrayOutputStream httpRequest = new ByteArrayOutputStream(); //where we will store the server's output
        ByteArrayOutputStream secondLine = new ByteArrayOutputStream();
        int temp = clientInput.read();
        while (temp != 10){ //10 means newline?
            httpRequest.write(temp);
            temp = clientInput.read();
        }
        String URL = httpRequest.toString();
        
        httpRequest.write(10);
        temp = clientInput.read();
        while (temp != 10){
            secondLine.write(temp);
            temp = clientInput.read();
        }
        clientSocket.shutdownInput();
        String httpResponse;
        if(!URL.contains("/ask")){
            httpResponse = "HTTP/1.1 404 Not Found\r\n";
        }
        else if(!URL.contains("hostname=") || !URL.contains("port=") || !URL.contains(" HTTP/1.1") || !URL.contains("GET ")){
            httpResponse = "HTTP/1.1 400 Bad Request\r\n";
        }
        else{
            String[] Contents = URL.substring(4,(URL.length() - 10)).
            replace("=","&").replace("?","&").split("&");
            boolean shutdown = false;
            Integer timeout = null;
            Integer limit = null;
            String hostname = null;	
            int port = 0;
            byte[] userInputBytes = new byte[0];
            String tempString;
            int index = 0;
            while (index < Contents.length) {
                if (Contents[index].equals("shutdown")) {
                    // Consume next argument as timeout
                    shutdown = true;
                }
                else if (Contents[index].equals("timeout")) {
                    // Consume next argument as timeout
                    index += 1;
                    timeout = Integer.parseInt(Contents[index]);
                }
                else if (Contents[index].equals("limit")) {
                    // Consume next argument as limit
                    index += 1;
                    limit = Integer.parseInt(Contents[index]);
                }
                else if (Contents[index].equals("port")) {
                    // Consume next argument as limit
                    index += 1;
                    port = Integer.parseInt(Contents[index]);
                }
                else if (Contents[index].equals("hostname")) {
                    // Consume next argument as limit
                    index += 1;
                    hostname = Contents[index];
                }
                else if (Contents[index].equals("string")) {
                    // Consume next argument as limit
                    index += 1;
                    tempString = Contents[index];
                    userInputBytes = tempString.getBytes();
                }
                index++;
            }

            TCPClient tcpClient = new tcpclient.TCPClient(shutdown, timeout, limit);
            try{
			byte[] serverBytes  = tcpClient.askServer(hostname, port, userInputBytes);
            
            String type = "200 OK";
            String response = new String(serverBytes);;
            String length = String.valueOf(response.length());
            httpResponse =
            "HTTP/1.1 " + type + "\r\n" + //
            "Content-Type: text/plain\r\n" +
            "Content-Length: " + length + "\r\n" +
            "\r\n" +
            response;}

            catch(Exception e){
                httpResponse = "HTTP/1.1 404 Not Found\r\n";}
        }
        byte[] byteResponse = httpResponse.getBytes();
        clientOutput.write(byteResponse);
        
        clientOutput.flush(); //forcefully send everything to the client

        httpRequest.close();
        secondLine.close();
        clientInput.close();
        clientSocket.close();
        clientOutput.close();
        //serverSocket.close();
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
    /*static public void TCPAsk(String[] args, boolean shutdown, Integer timeout, Integer limit,
    String hostname, int port, byte[] userInputBytes){
        String string;
        int argindex = 0;
        while (argindex < args.length) {
            if (args[argindex].equals("shutdown")) {
                // Consume next argument as timeout
                shutdown = true;
            }
            else if (args[argindex].equals("timeout")) {
                // Consume next argument as timeout
                argindex += 1;
                timeout = Integer.parseInt(args[argindex]);
            }
            else if (args[argindex].equals("limit")) {
                // Consume next argument as limit
                argindex += 1;
                limit = Integer.parseInt(args[argindex]);
            }
            else if (args[argindex].equals("port")) {
                // Consume next argument as limit
                argindex += 1;
                port = Integer.parseInt(args[argindex]);
            }
            else if (args[argindex].equals("hostname")) {
                // Consume next argument as limit
                argindex += 1;
                hostname = args[argindex];
            }
            else if (args[argindex].equals("string")) {
                // Consume next argument as limit
                argindex += 1;
                string = args[argindex];
                userInputBytes = string.getBytes();
            }
            argindex++;
        }
    }*/
}

