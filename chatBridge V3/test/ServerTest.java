
import static org.junit.Assert.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import Server.*;
import org.junit.jupiter.api.AfterAll;

public class ServerTest {

    private Thread serverThread;
    private Socket clientSocket;
    private Socket clientSocket2;
    private String clientOutput;

    @Before
    public void setUp() throws Exception {
        Server.getInstance();
        serverThread = new Thread();
        serverThread.start();
        // System.out.println("Server started");
    }

    @AfterAll
    public void tearDown() throws Exception {
        clientSocket.close();
        clientSocket2.close();
        serverThread.interrupt();
    }

    @Test
    public void testBroadCastUpdates() throws IOException {
        // Arrange
        clientOutput = "-";
        clientSocket = new Socket("localhost", 9999);
        clientSocket2 = new Socket("localhost", 9999);
//    out = new DataOutputStream(clientOutput);
        ServerUtility client = new ServerUtility(clientSocket, 1);
        ServerUtility client2 = new ServerUtility(clientSocket2, 2);
//    client.isCoordinator =  true;
        Server.clients.add(client);
        Server.clients.add(client2);
        Server.updateCoordinator();
        for (int i = 0; i < Server.clients.size(); i++) {
            clientOutput += "" + Server.clients.get(i) + ",";
        }
        for (int i = 0; i < Server.clients.size(); i++) {
            if (Server.clients.get(i).isCoordinator) {
                clientOutput += Server.clients.get(i).getClientId();
            }
        }
//    System.out.println(clientOutput);
        // Act
        Server.broadCastUpdates();
        String result = clientOutput.toString().trim();

        // Assert
        assertEquals("-client 1,client 2,1", result);
    }

    @Test
    public void testRemoveNotRespondingClients() throws IOException {
        clientSocket = new Socket("localhost", 9999);
        ServerUtility client = new ServerUtility(clientSocket, 1);
        ArrayList<ServerUtility> clients = new ArrayList<>();
        clients.add(client);
        Server.clients = clients;

        Server.removeNotRespondingClients(clientSocket.getRemoteSocketAddress().toString());
        boolean result = Server.clients.isEmpty();

        // Assert
        assertTrue(result);
    }

    @Test
    public void testUpdateCoordinator() throws IOException {
        // Arrange
        clientSocket = new Socket("localhost", 9999);
        clientSocket2 = new Socket("localhost", 9999);
        ServerUtility client1 = new ServerUtility(clientSocket, 1);
        ServerUtility client2 = new ServerUtility(clientSocket2, 2);
        Server.clients.add(client1);
        Server.clients.add(client2);
        //System.out.println("clients "+Server.clients);
        Server.updateCoordinator();
        //Assert
        for (int i = 0; i < Server.clients.size(); i++) {
            if (Server.clients.get(i).getClientId() == 1) {
                assertTrue(true);
            } else {
                assertFalse(false);
            }
        }

    }

    @Test
    public void testSendMessageTo() throws IOException {
//    // Arrange
        Server.clients = new ArrayList<>();
        clientSocket = new Socket("localhost", 9999);
        clientSocket2 = new Socket("localhost", 9999);
        new DataOutputStream(clientSocket.getOutputStream());
        ServerUtility client1 = new ServerUtility(clientSocket, 1);
        ServerUtility client2 = new ServerUtility(clientSocket2, 2);

        Server.clients.add(client1);
        Server.clients.add(client2);
        ///127.0.0.1:40730---client 1---fdf // Message format
        String message = client1.getSocket().getLocalSocketAddress().toString() + "---client 2---Hello";
//    System.out.println(message);
        // Act
        Server.sendMessageTo(message); //message sent
        String token[] = message.split("---");
        String token2[] = token[1].split(" ");
        int id = Integer.parseInt(token2[1]);
        for (int i = 0; i < Server.clients.size(); i++) {
            if (Server.clients.get(i).getClientId() == id) {
                if (Server.clients.get(i).messages.get(Server.clients.get(i).messages.size() - 1).contains(token[2])) {
                    assertTrue(true);
//                   System.out.println("Printing true");
                } else {
                    assertFalse(true);
                }
            }
        }

    }

    @Test
    public void testBroadCastMessage() throws IOException {
        Server.clients = new ArrayList<>();
        clientSocket = new Socket("localhost", 9999);
        clientSocket2 = new Socket("localhost", 9999);
        new DataOutputStream(clientSocket.getOutputStream());
        ServerUtility client1 = new ServerUtility(clientSocket, 1);
        ServerUtility client2 = new ServerUtility(clientSocket2, 2);

        Server.clients.add(client1);
        Server.clients.add(client2);
        ///127.0.0.1:40730---client 1---fdf // Message format
        String message = client1.getSocket().getLocalSocketAddress().toString() + "---B---Hello";
//    System.out.println(message);
        // Act
        Server.sendBroadCastMessage(message); //message sent
        String token[] = message.split("---B---");

        if (Server.clients.get(1).messages.contains(token[0]));
        for (int i = 0; i < Server.clients.size(); i++) {
            // System.out.println(Server.clients.get(i).getSocket().getLocalSocketAddress().toString()+" \t "+(token[0]));
            if (!Server.clients.get(i).getSocket().getLocalSocketAddress().toString().equals(token[0])) {
                //System.out.println("Here");
                if (Server.clients.get(i).messages.toString().contains("Hello")) {
                    assertTrue(true); //found send message
                } else {
                    assertFalse(true); //not found
                }
            }

        }
    }

    @Test
    public void testSingeltonPattern() {
        Server s1 = Server.getInstance();
        Server s2 = Server.getInstance();
        assertSame(s1, s2);

    }

}//end test class

