package Client;
import Client.ClientGUI;
import java.io.IOException;

public class StartClient {
    public static void main(String[] args) throws IOException, InterruptedException {
      ClientGUI c =  new ClientGUI();
       c.setVisible(true);
      // c.connect();
    }
    
}
