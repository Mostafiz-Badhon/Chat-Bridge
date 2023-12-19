package Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ClientGUI extends JFrame implements WindowListener {
    public String availableClients;
    DataOutputStream out;
    DataInputStream in;
    public Socket socket;
    public String myID;
    public String port;
    private JButton btnConnect;
    private JButton btnSend;
    private JComboBox<String> cmbMessageType;
    private JButton jButton2;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel9;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel4;
    private JScrollPane jScrollPane1;
    private JLabel lblMyID;
    private JLabel lblStatus;
    private JLabel lblcoordinator;
    private JTextArea txtChat;
    public JTextField txtID;
    public JTextField txtIP;
    public JTextField txtPort;
    private JTextField txtSendMessage;

    public ClientGUI() {
        this.initComponents();
        this.addWindowListener(this);
        this.txtIP.setText("localhost");
        this.txtPort.setText("9999");
        this.lblMyID.setText("MY ID: " + this.myID);
        this.cmbMessageType.setEnabled(false);
        this.btnSend.setEnabled(false);
        this.txtChat.setEnabled(false);
        this.txtSendMessage.setEnabled(false);
        this.myID = "-1";
    }

    public void connect(String ip, int port, int id) throws IOException {
        try {
            this.port = Integer.toString(port);
            this.socket = new Socket(ip, port);
            this.lblStatus.setText("Connected");
            this.in = new DataInputStream(this.socket.getInputStream());
            this.out = new DataOutputStream(this.socket.getOutputStream());
            this.out.writeUTF("Suggested id:" + id);
            this.cmbMessageType.setEnabled(true);
            this.btnSend.setEnabled(true);
            this.txtChat.setEnabled(false);
            this.txtSendMessage.setEnabled(true);
            this.btnConnect.setEnabled(false);
            this.txtIP.setEnabled(false);
            this.txtPort.setEnabled(false);
            this.txtID.setEnabled(false);
        } catch (ConnectException var5) {
            JOptionPane.showMessageDialog(this, "Server not running");
        } catch (UnknownHostException var6) {
            JOptionPane.showMessageDialog(this.btnConnect, "Ip address wrong");
        }

        this.lblMyID.setText("MY ID: " + this.myID);
        Thread receiveThread = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        String msg = ClientGUI.this.in.readUTF();
                        if (!msg.equals("")) {
                            ClientGUI.this.actOnReceivedMessage(msg);
                        }
                    } catch (IOException var2) {
                    } catch (NullPointerException var3) {
                    }
                }
            }
        });
        receiveThread.start();
    }

    public void actOnReceivedMessage(String message) throws IOException {
        if (message.charAt(0) == '-') {
            this.clientListUpdate(message);
        } else if (message.contains("Your id is:")) {
            this.setMyID(message);
        } else {
            this.txtChat.append(message + "\n");
        }

    }

    public void setMyID(String message) {
        String[] tok = message.split(":");
        this.myID = tok[1];
        this.lblMyID.setText("MY ID: " + this.myID);
    }

    public void clientListUpdate(String message) {
        String list = message.substring(1, message.length() - 2);
        this.cmbMessageType.removeAllItems();
        String[] clients = list.split(",");

        for(int i = 0; i < clients.length; ++i) {
            this.cmbMessageType.addItem(clients[i]);
        }

        this.cmbMessageType.addItem("Broadcast");
        this.lblcoordinator.setText("Client " + message.substring(message.length() - 1, message.length()));
    }

    private void initComponents() {
        this.jPanel1 = new JPanel();
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.txtIP = new JTextField();
        this.txtPort = new JTextField();
        this.jLabel9 = new JLabel();
        this.btnConnect = new JButton();
        this.jButton2 = new JButton();
        this.jLabel4 = new JLabel();
        this.txtID = new JTextField();
        this.jPanel2 = new JPanel();
        this.jLabel5 = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.txtChat = new JTextArea();
        this.jLabel3 = new JLabel();
        this.lblStatus = new JLabel();
        this.jLabel6 = new JLabel();
        this.lblcoordinator = new JLabel();
        this.lblMyID = new JLabel();
        this.jPanel4 = new JPanel();
        this.txtSendMessage = new JTextField();
        this.cmbMessageType = new JComboBox();
        this.btnSend = new JButton();
        this.setDefaultCloseOperation(3);
        this.setBackground(new Color(255, 255, 255));
        this.setResizable(false);
        this.jLabel1.setFont(new Font("DejaVu Sans", 1, 18));
        this.jLabel1.setText("Ip Address");
        this.jLabel2.setFont(new Font("DejaVu Sans", 1, 18));
        this.jLabel2.setText("Port");
        this.jLabel9.setFont(new Font("DejaVu Sans", 1, 28));
        this.jLabel9.setText("Client Console");
        this.btnConnect.setFont(new Font("DejaVu Sans", 1, 20));
        this.btnConnect.setText("Connect");
        this.btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ClientGUI.this.btnConnectActionPerformed(evt);
            }
        });
        this.jButton2.setFont(new Font("DejaVu Sans", 1, 20));
        this.jButton2.setText("X");
        this.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ClientGUI.this.jButton2ActionPerformed(evt);
            }
        });
        this.jLabel4.setFont(new Font("DejaVu Sans", 1, 18));
        this.jLabel4.setText("ID");
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(19, 19, 19).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel9).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jLabel1).addGap(18, 18, 18).addComponent(this.txtIP, -2, 192, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jLabel2, -2, 58, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.txtPort, -2, 113, -2).addGap(18, 18, 18).addComponent(this.jLabel4).addGap(18, 18, 18).addComponent(this.txtID, -2, 87, -2).addGap(18, 18, 18).addComponent(this.btnConnect).addGap(18, 18, 18).addComponent(this.jButton2))).addContainerGap(-1, 32767)));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addGap(19, 19, 19).addComponent(this.jLabel9, -1, 53, 32767).addGap(7, 7, 7).addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.txtID, -2, 32, -2).addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.txtIP, -2, 34, -2).addComponent(this.jLabel2, -1, -1, 32767).addComponent(this.txtPort, -2, 34, -2).addComponent(this.jLabel1).addComponent(this.jLabel4).addComponent(this.btnConnect, -1, 35, 32767).addComponent(this.jButton2))).addContainerGap()));
        this.jLabel5.setFont(new Font("DejaVu Sans", 1, 18));
        this.jLabel5.setText("Message's History");
        this.txtChat.setColumns(20);
        this.txtChat.setFont(new Font("DejaVu Sans", 1, 18));
        this.txtChat.setRows(5);
        this.jScrollPane1.setViewportView(this.txtChat);
        this.jLabel3.setFont(new Font("DejaVu Sans", 1, 18));
        this.jLabel3.setText("Status");
        this.lblStatus.setFont(new Font("DejaVu Sans", 1, 26));
        this.lblStatus.setText("Not Connected");
        this.jLabel6.setFont(new Font("DejaVu Sans", 1, 18));
        this.jLabel6.setText("Coordinator:");
        this.lblcoordinator.setFont(new Font("DejaVu Sans", 1, 26));
        this.lblcoordinator.setText("-1");
        this.lblMyID.setFont(new Font("DejaVu Sans", 1, 26));
        this.lblMyID.setText("-1");
        GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
        this.jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGap(14, 14, 14).addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel5).addComponent(this.jScrollPane1, -2, 605, -2)).addGap(39, 39, 39).addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel3).addComponent(this.jLabel6).addGroup(jPanel2Layout.createSequentialGroup().addGap(6, 6, 6).addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addComponent(this.lblcoordinator).addComponent(this.lblMyID))).addComponent(this.lblStatus)).addContainerGap(41, 32767)));
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGap(15, 15, 15).addComponent(this.jLabel5).addGap(18, 18, 18).addComponent(this.jScrollPane1, -2, 178, -2)).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel3).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.lblStatus).addGap(18, 18, 18).addComponent(this.jLabel6).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.lblcoordinator).addGap(32, 32, 32).addComponent(this.lblMyID))).addContainerGap(15, 32767)));
        this.cmbMessageType.setFont(new Font("DejaVu Sans", 1, 20));
        this.btnSend.setFont(new Font("DejaVu Sans", 1, 20));
        this.btnSend.setText("Send");
        this.btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ClientGUI.this.btnSendActionPerformed(evt);
            }
        });
        GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
        this.jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addGap(14, 14, 14).addComponent(this.txtSendMessage, -2, 613, -2).addGap(18, 18, 18).addComponent(this.cmbMessageType, -2, -1, -2).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.btnSend, -2, 119, -2).addContainerGap(109, 32767)));
        jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, jPanel4Layout.createSequentialGroup().addContainerGap(18, 32767).addGroup(jPanel4Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.txtSendMessage, -2, 37, -2).addComponent(this.cmbMessageType, -2, 37, -2).addComponent(this.btnSend, -2, 37, -2)).addGap(31, 31, 31)));
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel4, -2, -1, -2).addGroup(layout.createParallelGroup(Alignment.TRAILING, false).addComponent(this.jPanel1, Alignment.LEADING, -1, -1, 32767).addComponent(this.jPanel2, Alignment.LEADING, -1, -1, 32767))).addContainerGap(15, 32767)));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jPanel1, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jPanel2, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jPanel4, -1, -1, 32767).addContainerGap()));
        this.pack();
    }

    public void btnSendActionPerformed(ActionEvent evt) {
        String mineAddress = this.socket.getLocalSocketAddress().toString();
        String actualMessageToSend = "";
        String msg = this.txtSendMessage.getText();
        String sendTo = (String)this.cmbMessageType.getSelectedItem();
        if (sendTo.equals("Broadcast")) {
            msg = mineAddress + "---B---" + msg;
        } else if (sendTo.charAt(0) == 'c') {
            msg = mineAddress + "---" + sendTo + "---" + msg;
        }

        try {
            if (!this.txtSendMessage.getText().equals("")) {
                this.out.writeUTF(msg);
            } else {
                JOptionPane.showMessageDialog(this.txtChat, "Enter message first");
            }
        } catch (IOException var7) {
        } catch (NullPointerException var8) {
            JOptionPane.showMessageDialog(this.btnConnect, "Connection not established");
        }

    }

    public void btnConnectActionPerformed(ActionEvent evt) {
        String ip;
        if (!this.txtIP.getText().equals("") && !this.txtPort.getText().equals("") && !this.txtID.getText().equals("")) {
            try {
                ip = this.txtIP.getText();
                int port = Integer.parseInt(this.txtPort.getText());
                int id = Integer.parseInt(this.txtID.getText());
                this.connect(ip, port, id);
            } catch (IOException var6) {
                Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, (String)null, var6);
            } catch (NumberFormatException var7) {
            }
        } else {
            ip = "Ip address and port number must not be empty";
            JOptionPane.showMessageDialog(this.btnConnect, ip);
        }

    }

    public void jButton2ActionPerformed(ActionEvent evt) {
        this.windowClosing();
        System.exit(0);
    }

    public void windowClosing() {
        try {
            this.out.writeUTF("--" + this.socket.getLocalSocketAddress());
            this.out.flush();
            this.socket.close();
        } catch (IOException var2) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, (String)null, var2);
        } catch (NullPointerException var3) {
            System.exit(0);
        }

    }

    public void windowOpened(WindowEvent we) {
    }

    public void windowClosing(WindowEvent we) {
        this.windowClosing();
    }

    public void windowClosed(WindowEvent we) {
    }

    public void windowIconified(WindowEvent we) {
    }

    public void windowDeiconified(WindowEvent we) {
    }

    public void windowActivated(WindowEvent we) {
    }

    public void windowDeactivated(WindowEvent we) {
    }
}
