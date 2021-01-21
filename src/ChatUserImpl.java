import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Vector;
import javax.swing.*;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class ChatUserImpl {
    private String title = "Logiciel de discussion en ligne";
    private String pseudo = null;
    private XmlRpcClient server=null;
    private JFrame window = new JFrame(this.title);
    private JTextArea txtOutput = new JTextArea();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Envoyer");
    public void connexion(){
    	try {
			this.server = new XmlRpcClient("http://localhost:8787/RPC2");			
		}catch (Exception exception) { 
			System.err.println("Client: " + exception); 
		}
    }
    public ChatUserImpl() {
        this.createIHM();
        connexion();
        this.requestPseudo();
	}


    public void createIHM() {
        // Assemblage des composants
        JPanel panel = (JPanel)this.window.getContentPane();
        JScrollPane sclPane = new JScrollPane(txtOutput);
        panel.add(sclPane, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(this.txtMessage, BorderLayout.CENTER);
        southPanel.add(this.btnSend, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);
        // Gestion des évènements
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                window_windowClosing(e);
            }
        });
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnSend_actionPerformed(e);
            }
        });
   
	
	txtMessage.addKeyListener(new KeyAdapter() {
	    public void keyReleased(KeyEvent event) {
		if (event.getKeyChar() == '\n')
		    btnSend_actionPerformed(null);
	    }
	});

        // Initialisation des attributs
        this.txtOutput.setBackground(new Color(220,220,220));
        this.txtOutput.setEditable(false);
        this.window.setSize(500,400);
        this.window.setVisible(true);
        this.txtMessage.requestFocus();
    }

    public void requestPseudo() {
        this.pseudo = JOptionPane.showInputDialog(
                this.window, "Entrez votre pseudo : ",
                this.title,  JOptionPane.OK_OPTION
        );
        if (this.pseudo == null) System.exit(0);
        Vector<String> addUser = new Vector<String>();
		addUser.add(this.pseudo);		
		boolean isAdded;
		try {
				isAdded = (boolean)this.server.execute("sample.subscribe", addUser);
				if(!isAdded){
				
					JOptionPane.showMessageDialog(this.window, "Pseudo exists !");
					requestPseudo();
				}
			} 
			catch (XmlRpcException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
       }

    public void window_windowClosing(WindowEvent e) {
    	Vector<String> deconnect = new Vector<String>();
		deconnect.add(this.pseudo);		
		boolean deconnectOk;
		try {
			deconnectOk = (boolean)this.server.execute("sample.unsubscribe", deconnect);
			if(deconnectOk){
				System.exit(-1);
			}
		} catch (XmlRpcException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
    }

    public void btnSend_actionPerformed(ActionEvent e) {
    	 
    	Vector<String> message = new Vector<String>();
		message.add(this.pseudo);
		message.add(this.txtMessage.getText());
		try {
			this.server.execute("sample.postMessage", message);
		} 
		catch (XmlRpcException e1) {
			e1.printStackTrace();
			} 
		catch (IOException e1) {
			e1.printStackTrace();
		}
		
    	this.txtMessage.setText("");
        this.txtMessage.requestFocus();
    }

    public static void main(String[] args) {
        ChatUserImpl chatUserImpl = new ChatUserImpl();
        boolean recu = false;
        String message = "";
        String lastMessageSent = "";
        boolean isFirstMessage = false;
        while(true){
        	try {
				message = (String)chatUserImpl.server.execute("sample.getMessage", new Vector<String>());
				recu = true;
				if(isFirstMessage == false){
					lastMessageSent = message;
				}
			}catch(Exception e){
				recu = false;
			}
        	finally {
				if(recu){
					if(isFirstMessage){
						if(!lastMessageSent.equals(message)){
							chatUserImpl.txtOutput.append(message +" \n");
							lastMessageSent = message;
						}
					}else{
						
						chatUserImpl.txtOutput.append(message +" \n");
						lastMessageSent = message;
						isFirstMessage = true;
					}	
				}
        	}
        }
    }
}
