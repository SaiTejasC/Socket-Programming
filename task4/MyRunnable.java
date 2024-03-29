import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import tcpclient.TCPClient;

public class MyRunnable implements Runnable {
    Socket clientSocket;
    public MyRunnable(Socket parameter) {
        clientSocket = parameter;
    }
 
    public void run() {
        try{
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
        } catch (IOException e) {
            System.out.println("mistake occured");
            e.printStackTrace();
        }
    }
}