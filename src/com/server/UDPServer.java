package com.server;

import java.net.*;
import java.io.*;

import org.omg.CORBA.Request;

public class UDPServer extends Thread{

	int portNumber;
	GameServer server=null;
	
	public UDPServer(int port,GameServer server) {
		portNumber=port;
		this.server=server;
	}
	

	public void run() {
		DatagramSocket aSocket = null;
		try {
			aSocket=new DatagramSocket(portNumber);
			/*aSocketNA = new DatagramSocket(1411);
			aSocketEU = new DatagramSocket(1412);
			aSocketAS = new DatagramSocket(1413);*/
			byte[] bufferNA = new byte[10000];
			byte[] bufferEU = new byte[10000];
			byte[] bufferAS = new byte[10000];
			
			while(true){
					
					/*DatagramPacket requestNA = new DatagramPacket(bufferNA,bufferNA.length);
					aSocketNA.receive(requestNA);*/
			
					DatagramPacket requestEU = new DatagramPacket(bufferEU,bufferEU.length);
					aSocket.receive(requestEU);
					String ans="";
					String temp=new String(requestEU.getData());
					temp=temp.trim();
					if (temp.equals("call")) {
						//bufferEU="from EU!".getBytes();
						bufferEU=server.calculateStatus().getBytes();
						
					}
					else{
						System.out.println(temp);
						bufferEU="Not from EU!".getBytes();
						
					}
					
					//bufferEU=server.calculateStatus(server.hashtable).getBytes();
					DatagramPacket replyEU = new DatagramPacket(bufferEU, bufferEU.length, requestEU.getAddress(), requestEU.getPort());
					aSocket.send(replyEU);
				
				//if (server!=null) {
					//String ansfromserver=server.calculateStatus(server.hashtable);
					
					/*DatagramPacket requestNA = new DatagramPacket(bufferNA,bufferNA.length);
					aSocketNA.receive(requestNA);*/
					
					
					
					/*DatagramPacket requestAS = new DatagramPacket(bufferAS,bufferAS.length);
					aSocketAS.receive(requestAS);*/
					
					
					
					
					/*DatagramPacket replyNA = new DatagramPacket(bufferNA, bufferNA.length, requestNA.getAddress(), requestNA.getPort());
					aSocketNA.send(replyNA);*/
					
					
					
					/*DatagramPacket replyAS = new DatagramPacket(bufferAS, bufferAS.length, requestAS.getAddress(), requestAS.getPort());
					aSocketAS.send(replyAS);*/
					
				//}
				//String ansfromserver=server.calculateStatus(server.hashtable);
				//buffer=ansfromserver.getBytes();
				/*
				DatagramPacket request = new DatagramPacket(buffer,buffer.length);
				aSocket.receive(request);
				
				DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), port);
				aSocket.send(reply);*/
			}
		} catch (SocketException e) {
			System.out.println("Error in SocketException : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error in IOException : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error in Exception : " + e.getMessage());
		}
	
	}
	public void callUDP(int port, GameServer server){
	}
}
