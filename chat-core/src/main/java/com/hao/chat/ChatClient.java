package com.hao.chat;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient extends Frame{
	
	private static final String SERVER_IP="127.0.0.1";
	private static final int SERVER_PORT=8888;
	
	private TextField tfTxt=new TextField();
	private TextArea taContent=new TextArea();
	
	private Socket socket=null;
	private DataOutputStream dos=null;
	
	
	
	public void lauchFrame(){
		setLocation(300,300);
		setSize(300, 300);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
			
		});
		tfTxt.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String s=tfTxt.getText();
				taContent.setText(taContent.getText()+"\r\n"+s);
				tfTxt.setText("");
				try {
					dos.writeUTF(s);
					dos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		add(tfTxt,BorderLayout.SOUTH);
		add(taContent,BorderLayout.NORTH);
		pack();
		
		setVisible(true);
	}
	
	
	
	public void connectToServer() throws UnknownHostException, IOException{
		socket=new Socket(SERVER_IP, SERVER_PORT);
		dos=new DataOutputStream(socket.getOutputStream());
System.out.println("connected to server.");
		
	}
	
	public void disConnect(){
		if(socket!=null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(dos!=null){
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		ChatClient chatClient=new ChatClient();
		chatClient.lauchFrame();
		chatClient.connectToServer();
	}
	
	private class ReadTask implements Runnable{
		public void run() {
			
		}
	}
	
	private class WriteTask implements Runnable{
		public void run() {
			
		}
	}
}
