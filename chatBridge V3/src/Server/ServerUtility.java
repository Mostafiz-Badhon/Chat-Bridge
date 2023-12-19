package Server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerUtility extends Thread {

    private Socket socket;
    public DataInputStream in;
    public DataOutputStream out;
    private int clientId;
    public boolean isCoordinator;
    public ArrayList<String> messages;
    public String message;

    public ServerUtility(Socket socket, int clientId) throws IOException {
        this.socket = socket;
        this.clientId = clientId;
        this.isCoordinator = false;
        this.messages = new ArrayList<>();
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//
                    while (true) {

                        //System.out.println(in.readUTF());
                        message = in.readUTF();
                        if (!message.equals("")) {
                            received(message);
                        }
                        message = "";
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Client lost");
                    try {
                        Server.broadCastUpdates();
                    } catch (IOException ex) {
                        Logger.getLogger(ServerUtility.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
        receiveThread.start();
    }

    public void received(String rec_Message) throws IOException {
        if (rec_Message.substring(0, 2).equals("--")) { //client left Handler
            
            Server.removeNotRespondingClients("/" + rec_Message.substring(3, rec_Message.length()));

        } //client left handler
        else if (rec_Message.contains("---B---")) { //its a broadcast message
            //S        System.out.println("Client connected with id: "+Server.clients.get(Server.clients.size()-1).clientId);
            System.out.println("Broadcast message request received");
            Server.sendBroadCastMessage(rec_Message);
//            System.out.println("");
        } //broadcast message
        else if (rec_Message.contains("Suggested id:")) { //id set request
            String t[] = rec_Message.split(":");
            int id = Integer.parseInt(t[1]);
            id = Server.resolveIdClash(id);
            
            Server.Id = id; //id set against client
            Server.clients.get(Server.clients.size() - 1).clientId = id; //setting new id
            //Server.Id= -1;
            System.out.println("Client added with id : " + id);
            System.out.print(Server.clients.size()+" Connected client(s) is/are: " + Server.clients + "\n"); //checking the clients
            out.writeUTF("Your id is:" + Server.clients.get(Server.clients.size() - 1).clientId); //Sending client its Id
//            System.out.println("Client connected with id: " + Server.clients.get(Server.clients.size() - 1).clientId);

        } else { //individual message
            System.out.println(rec_Message);
            System.out.println("Message request Generated");
            Server.sendMessageTo(rec_Message);
            
        }

    }

    public Socket getSocket() {
        return socket;
    }

    public int getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "client " + clientId;
    }
   
}
