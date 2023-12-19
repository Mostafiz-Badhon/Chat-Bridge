

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import Client.ClientGUI;
import Server.Server;
public class ClientGUITest {
    Thread serverThread;

    @Before
    public void setUp() throws IOException {
        Server.getInstance();
        serverThread = new Thread();
        serverThread.start();
//    System.out.println("Server started");

    }

     @Test
    public void testBtnConnectActionPerformed_InvalidInput() throws IOException {

        ClientGUI c1 = new ClientGUI();
        c1.connect("localhost", 9999, 1);
        assertTrue(isInteger(c1.port));
        assertTrue(isInteger(c1.myID));

    }

    private boolean isInteger(Object obj) {
        try {
            Integer.parseInt(obj.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Test
    public void testBtnConnectActionPerformed_ValidInput() throws IOException {
        ClientGUI c1 = new ClientGUI();
        c1.connect("localhost", 9999, 1);
        assertNotNull(c1.socket);
    }

 

    @Test
    public void testSetMyID() throws IOException, InterruptedException {
        String message = "Your id is:1";
        String tok[] = message.split(":");

        ClientGUI ui = new ClientGUI();
        ClientGUI ui2 = new ClientGUI();
        ui2.connect("localhost", 9999, Integer.parseInt(tok[1]));
        ui.connect("localhost", 9999, Integer.parseInt(tok[1]));
        Thread.sleep(2000);
        assertNotSame(ui.myID, ui2.myID);
    }



}

