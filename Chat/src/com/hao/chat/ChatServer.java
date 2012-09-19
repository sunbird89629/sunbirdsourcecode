package com.hao.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ChatServer {
	
	private static final int PORT=8888;
	
	private ServerSocket serverSocket;
	
	private List<ClientConnection> clientConnections=new LinkedList<ClientConnection>();
	
	public ChatServer() {
		
	}
	
	
	
	public void start() throws IOException{
		serverSocket=new ServerSocket(PORT);
		System.out.println("server listen at port:"+PORT);
		
		while(true){
			Socket socket=serverSocket.accept();
			ClientConnection clientConnection=new ClientConnection(socket);
			clientConnections.add(clientConnection);
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		ChatServer chatServer=new ChatServer();
		chatServer.start();
	}
	
	
	
	private class ClientConnection{
		private Socket socket;

		private DataOutputStream dos;
		
		private DataInputStream dis;
		
		private boolean start;
		
		
		
		public ClientConnection(Socket socket) {
			this.socket = socket;
			try {
				dos=new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dis=new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			start=true;
			new Thread(new ReadTask()).start();
		}
		
		public void close(){
			start=false;
		}
		
		private class ReadTask implements Runnable{

			public void run() {
				while(start){
					try {
						String s=dis.readUTF();
System.out.println(s);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			
			
		}
		
		public void sendMessage(String msg) throws IOException{
			dos.writeUTF(msg);
		}
		
	}
	
}
