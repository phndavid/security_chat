package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import contoller.ConnectionThread;

public class WindowServer extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1230725763444004171L;
	private static final String SEND = "SEND";

	private JTextArea txtA_conversation;
	private JPanel typingPanel;
	private JTextField txtF_typing;
	private JButton btn_send;
	
	private ConnectionThread connectionThread;

	public WindowServer(ConnectionThread connectionThread){
		setTitle("Security Chat:: Server");
		setSize(400,240);
		setVisible(true);
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		txtA_conversation = new JTextArea(5,20);
		JScrollPane scroll = new JScrollPane (txtA_conversation, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	
		typingPanel = new JPanel();
		typingPanel.setLayout(new BorderLayout());
		txtF_typing = new JTextField();
		btn_send = new JButton("Send");
		btn_send.addActionListener(this);
		btn_send.setActionCommand(SEND);
		typingPanel.add(txtF_typing,BorderLayout.CENTER);
		typingPanel.add(btn_send, BorderLayout.EAST);
		
		this.add(scroll,BorderLayout.NORTH);
		this.add(typingPanel, BorderLayout.SOUTH);
		this.connectionThread = connectionThread;

	}
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		String command = event.getActionCommand( );
		if(SEND.equals(command) )
        {
			String oldTyping;
			String typing;
			if (!txtF_typing.getText().equals("")) {
				oldTyping = txtA_conversation.getText();
				typing = "SERVER: "+ txtF_typing.getText();
				String conversation = oldTyping+"\n"+ typing;				
				txtA_conversation.setText(conversation);
				String msg = "SERVER: "+ txtF_typing.getText();
				// Se encripta el mensaje y se envia al cliente
				ObjectOutputStream out = connectionThread.getWriter();
				connectionThread.getMsgServer().encryptAndSendMessage(msg, out);
				txtF_typing.setText("");
			}
		}
	}
	public void  readConversation(String message){
		String oldTyping = txtA_conversation.getText();
		String typing = oldTyping+"\n"+  message;
		txtA_conversation.setText(typing);
	}
}
