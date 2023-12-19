package Server;

import static Server.Server.Id;
import static Server.Server.out;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


public class Server {
    private static Server instance;
    static public ServerUtility coordinator;
    static public ArrayList<ServerUtility> clients = new ArrayList<>();
    static public int Id;
    static DataOutputStream out;
    private ServerSocket serverSocket;
    
    public static Server getInstance() {
        if (instance == null) {
            try {
                instance = new Server();
            } catch (IOException e) {
            } catch (InterruptedException ee) {
            }
        }
        return instance;
    }
    private Server()throws IOException, InterruptedException{
        
        serverSocket = new ServerSocket(9999);
        
        System.out.println("Server Started");

        while (true) {
            Socket socket = serverSocket.accept(); //connection accetped
            out = new DataOutputStream(socket.getOutputStream());
            ServerUtility newClient = new ServerUtility(socket, Id); //creating new client
            clients.add(newClient); //adding them to list
            newClient.start(); //start client 
            Thread.sleep(500);
            broadCastUpdates(); //braodcast message to new and old clients
            updateCoordinator(); //update coordinator

        }
    }

    public static void broadCastUpdates() throws IOException, SocketException {
        String clientString = "-";
        updateCoordinator(); //update coordinator if some client lost
        //System.out.println("Total Clients: " + clients.size());// diplaying total number of clients
        if (clients.size() > 0) {
            for (int j = 0; j < clients.size(); j++) { // iteratre over the clients to find the clients
                clientString += "client " + clients.get(j).getClientId() + ",";
            } //end of for
        }
        try {
            clientString += coordinator.getClientId();      //Broadcast message ready to sent to client
        } catch (NullPointerException nn) {
            //no coordinator now
        }
        /*Broacast message sending loop*/
        if (clients.size() > 0) {
            for (int counter = 0; counter < clients.size(); counter++) { // iteratre over the clients to find the clients
                // clientString += "client " + clients.get(j).getClientId() + ",";
                try {
                    clients.get(counter).out.writeUTF(clientString);
                } catch (SocketException e) {
                    System.out.println("Client already removed");
                }
            } //end of for

        } //end if

    } //end funciton broadcast

    public static void removeNotRespondingClients(String address) throws IOException, SocketException {

        for (int i = 0; i < clients.size(); i++) {
            //System.out.println(address +"\t"+ clients.get(i).getSocket().getRemoteSocketAddress().toString());
            if (clients.get(i).getSocket().getRemoteSocketAddress().toString().equals(address)) {
                clients.remove(i);
                  //System.out.println("Removed");
                break;
            }
        }
        broadCastUpdates();
        updateCoordinator();
    }

    public static void updateCoordinator() {
        if (clients.size() != 0) {
            clients.get(0).isCoordinator = true;
            coordinator = clients.get(0);
        } else {
            coordinator = null;
        }
    }

    public static void sendMessageTo(String recived) throws IOException {
        String from, message ,toSend="";
        int to;
        StringBuilder builder = new StringBuilder("");
        String token[] = recived.split("---");
        message = token[2];
        from = token[0].substring(1, token[0].length());
        String senderId = "";
        String[] tok = token[1].split(" ");
        to = Integer.parseInt(tok[1]);
       // System.out.println(from + " " + to + " " + message);
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getSocket().getRemoteSocketAddress().toString().subSequence(1, clients.get(i).getSocket().getRemoteSocketAddress().toString().length()).equals(from)) {
                senderId = "Client " + clients.get(i).getClientId();

            }
        }
        for (int i = 0; i < clients.size(); i++) {
            ServerUtility selected = clients.get(i);
            if (selected.getClientId() == to && !from.equals(selected.getSocket().getRemoteSocketAddress().toString().subSequence(1, selected.getSocket().getRemoteSocketAddress().toString().length()))) {
                toSend = senderId + ": " + message;
                selected.out.writeUTF(toSend);
                selected.messages.add(toSend);
            }
        }


    }
    public static void sendBroadCastMessage(String message) throws IOException{
    String from, actualMessage, toSend="";
    int idOfSender = 0;
    String[] temp = message.split("---");
    from = temp[0].substring(1,temp[0].length());
    actualMessage = temp[2];
        for (int i = 0; i < clients.size(); i++) {
            if(clients.get(i).getSocket().getRemoteSocketAddress().toString().subSequence(1, clients.get(i).getSocket().getRemoteSocketAddress().toString().length()).equals(from)){
             idOfSender = clients.get(i).getClientId();           
            }
        }//id found
        for (int i = 0; i < clients.size(); i++) {
            if(clients.get(i).getClientId()!= idOfSender)
                toSend = "Client "+idOfSender+": "+actualMessage;
                clients.get(i).out.writeUTF(toSend);
                clients.get(i).messages.add(toSend);
        }
        
        
    }
    public static int resolveIdClash(int id){
    int finalid = id, max = -1;
    
    boolean duplicate= false;
        for (int i = 0; i < clients.size(); i++) {
            if (id == clients.get(i).getClientId())
            {
            duplicate = true;
                //System.out.println("Duplicate!!!!!!!!!!!!!!!!!!!");
             break;
            }
        }
    if(!duplicate){
       // System.out.println("Not duplicate");
        return finalid;
    
    
    }
    else{
        System.out.println("Duplicate id, resolving clash");
      return maxID();
    
    }
    }
    public static  int maxID(){
    int max = -1;
        for (int i = 0; i < clients.size(); i++) {
            if(clients.get(i).getClientId()> max)
                max = clients.get(i).getClientId();
        }

return max+1;    
    }
}
