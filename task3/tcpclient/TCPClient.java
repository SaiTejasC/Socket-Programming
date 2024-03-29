package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    //saves the response from the server
    public byte[] buffer;
    boolean shutdown;
    Integer timeout;
    Integer limit;

    public TCPClient(boolean shutdown2, Integer timeout2, Integer limit2) {
        shutdown = shutdown2;
        if (timeout2==null){
            timeout = null;
        }
        else{
            timeout = timeout2;
        }
        limit = limit2;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        
        Socket clientSocket = new Socket(hostname, port); //client connects to the server
        OutputStream clientOutput =  clientSocket.getOutputStream(); //client's output
        clientOutput.write(toServerBytes); //write the input to the server

        if (shutdown){
            clientSocket.shutdownOutput();
        }

        InputStream clientInput = clientSocket.getInputStream(); //client's input
        ByteArrayOutputStream tempBuffer = new ByteArrayOutputStream(); //where we will store the server's output
        ByteArrayOutputStream prevBuffer = new ByteArrayOutputStream(); //where we will store the server's output

        int tempLimit;
        if(limit == null){
            tempLimit = Integer.MAX_VALUE;
        }
        else{
            tempLimit = limit;
        }

        int tempTimeout; //not needed
        if(timeout == null){
            tempTimeout = Integer.MAX_VALUE; // not needed
        }
        else{
            tempTimeout = timeout; //not needed
            clientSocket.setSoTimeout(timeout);
        }
        
        long timerStart = System.currentTimeMillis(); //not needed
        
        try{
        int temp = clientInput.read();
        while (temp != -1 && tempBuffer.size() < tempLimit){ //.reads the server's output. returns -1 if we reach the end of the data stream  && (System.currentTimeMillis() - timerStart) < tempTimeout
            prevBuffer = tempBuffer;
            tempBuffer.write(temp);
            temp = clientInput.read();
        }}
        catch(Exception SocketTimeoutException){
        }

        if (tempBuffer.size() < tempLimit){
            buffer = tempBuffer.toByteArray();
        }
        else{
            buffer = prevBuffer.toByteArray();
        }
        clientSocket.close();
        return buffer;
    }    
 }
