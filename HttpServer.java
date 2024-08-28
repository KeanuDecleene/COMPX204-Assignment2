//Name: Keanu De Cleene
//ID: 1626997
import java.net.*;
import java.io.*;

/*
 * A console application that starts an HTTP server, listens for connections,
 * and handles requests. Chooses a specific port for the ServerSocket
 */
class HttpServer{
    private static final int PORT = 50000; //default port for the server socket
    public static void main(String args[]){
        System.out.println("Web server starting...");

        try (ServerSocket serverSock = new ServerSocket(PORT)){ //trys to create server using hardcoded port
            while (true){
                Socket clientSocket = serverSock.accept(); //waiting for client socket
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress()); //prints ip address that made the connection

                //creates a thread for the new client and its session
                HttpServerSession session = new HttpServerSession(clientSocket); 
                session.start(); 
        } 
    }catch(IOException e){ //error handling using io exception
            System.err.println("error in server: " + e.getMessage());
        }
        
    }
}

/*
 * This class represents a session for a clients HTTP request. It connect to the web server,
 * processes request and send the appropriate response in HTTP format.
 */
class HttpServerSession extends Thread {
    private Socket clientSocket;

    //construcor initialises the session with the clients socket
    public HttpServerSession(Socket socket) {
        this.clientSocket = socket;
    }

    /*
     * processes the clients request,has error handling for error 404, sends file to the web server
     */
    public void run() {
        System.out.println("Processing request from " + clientSocket.getInetAddress());

        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedOutputStream output = new BufferedOutputStream(clientSocket.getOutputStream());
            HttpServerRequest req = new HttpServerRequest();
            String inputLine, reqFile, reqHost;
            //reads and processes input lines until request is done
            while(!req.isDone()){
                inputLine = input.readLine(); 
                req.process(inputLine); //processes each line
            }
            //gets file and host
            reqFile = req.getFile();
            reqHost = req.getHost();

            //file object representing the requested file on the server
            File file = new File(reqHost + "/" + reqFile);
            if(!file.exists()){ //if the file doesn't exist send 404 not found error
                println(output,"HTTP/1.1 404 Not Found");
                println(output, "");
                println(output, reqFile + " not found");
            } else{ //send 200 OK 
                println(output,"HTTP/1.1 200 OK");
                println(output,"Content-Type: text/html");
                println(output,"");

                //sending file input to client
                try (FileInputStream fis = new FileInputStream(file)){
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1){ //reads and sends file content 
                        output.write(buffer, 0, bytesRead);

                        //Uncomment to simulate a slow network
                        // try{
                        //     Thread.sleep(1000); //simulates slow network
                        // }catch (InterruptedException e){
                        //     e.printStackTrace();
                        // }

                    }
                    fis.close(); //closes file input stream
                } catch (IOException e) {
                    System.err.println("Error reading file: " + e.getMessage());
                }
            }
            output.flush(); //ensures all data is sent
            clientSocket.close(); //closes connection with client

        } catch (IOException e) {
            System.err.println("Session exception: " + e.getMessage());
        }
    }

    /*
     * method to print a line with CRLF as required by HTTP
     * 
     * @param bos Output Stream to send the data to
     * @param s The String to send, chich will need CRLF
     * @return true if the operation was successful, false if and exception occured
     */
    private boolean println(BufferedOutputStream bos, String s){
    String news = s + "\r\n";
    byte[] array = news.getBytes();
    try {
        bos.write(array, 0, array.length);
    } catch(IOException e) {
        return false;
    }
    return true;
} 
}

