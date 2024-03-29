package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    //saves the response from the server
    public byte[] buffer;

    //Output means output from this socket
    //input means input to this socket
    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        
        Socket clientSocket = new Socket(hostname, port); //client connects to the server


        OutputStream clientOutput =  clientSocket.getOutputStream(); //client's output
        clientOutput.write(toServerBytes); //write the input to the server

        InputStream clientInput = clientSocket.getInputStream(); //client's input
        ByteArrayOutputStream tempBuffer = new ByteArrayOutputStream(); //where we will store the server's output
        int temp = clientInput.read();

        while (temp != -1){ //.reads the server's output. returns -1 if we reach the end of the data stream
            tempBuffer.write(temp);
            temp = clientInput.read();
        }

        buffer = tempBuffer.toByteArray();
        return buffer;
    }

    public byte[] askServer(String hostname, int port) throws IOException {
        
        Socket clientSocket = new Socket(hostname, port);

        InputStream clientInput = clientSocket.getInputStream();
        ByteArrayOutputStream tempBuffer = new ByteArrayOutputStream();
        int temp = 0;

        while (temp != -1){
            temp = clientInput.read();
            tempBuffer.write(temp);
        }

        buffer = tempBuffer.toByteArray();
        return buffer;
    }
}
