package com.server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.net.ssl.SSLEngineResult.Status;

import com.client.PlayerClient;
import com.gameServerInterface.GameServerInterface;

public class GameServer implements GameServerInterface {
	
	//static HashTableAdapter htemp=null;
	public Hashtable<String, ArrayList<PlayerClient>> hashtable = new Hashtable<String, ArrayList<PlayerClient>>();
	GameServer serverNA;
	GameServer serverEU;
	GameServer serverAS;
	UDPServer udpServer;
	
	PlayerClient pClient;
	
	int portNA=1411;
	int portEU=1412;
	int portAS=1413;
	
	public GameServer(int port){
		udpServer=new UDPServer(port,this);
		udpServer.start();
		
	}
	public  GameServer() {
		
	}
	
	//creates player when called from client side
	public String playerCreation(
			String geolocation, PlayerClient player) {
		ArrayList<PlayerClient> list = new ArrayList<PlayerClient>();
		int i = 0, length = 0;
		boolean flag = true;
		String key = player.username.substring(0, 1);
		
		String filename;

		if (hashtable.containsKey(key)) {
			length = hashtable.get(key).size();
			if (length > 0) {
				for (i = 0; i < length; i++) {
					if (player.username.equalsIgnoreCase(hashtable.get(key).get(i).username)) {
						flag = false;
						// it means already registered						
						break;
					}
				}
			}
		}

		if (flag) {

			player.signInStatus = false;
			
			//synchronization used for hashtable access as well as during logging
			synchronized (hashtable) {
				
				//server's log
				filename = "log/server/"+geolocation + "-server.log";
				createLog(filename,"Player Creation Request with username: "+player.username);
				
				if (hashtable.containsKey(key)) {
					length = hashtable.get(key).size();
					for (int j = 0; j < length; j++) {
						list.add(hashtable.get(key).get(j));
					}
				}

				list.add(player);
				hashtable.put(key, list);
				
				
				//player's log
				filename = "log/client/"+player.username + geolocation + ".log";
				createLog(filename,"Account Created ");

				//server's log
				filename = "log/server/"+geolocation + "-server.log";
				createLog(filename,"Status: Account Created with username: "+player.username);
				
				return "Account Created";
			}

		} else {
			
			//server's log
			filename = "log/server/"+geolocation + "-server.log";
			createLog(filename,"Status: player creation failed");
			
			return "player creation failed";
		}

	}
	
	//private method for logging
	private void createLog(String filename, String string) {
		Logger logger = Logger.getLogger(GameServer.class.getName());
		logger.setUseParentHandlers(false); //wont print to console
		FileHandler fh = null;
		try {
			File f = new File(filename);
			if (f.exists()) {
				//if file already exists,it will append the data
				fh = new FileHandler(filename, true);
			} else {
				//will create new file 
				fh = new FileHandler(filename);
			}

			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			logger.info(string);
		} catch (SecurityException e) {
			logger.info("SecurityException : " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("IOException : " + e.getMessage());
			e.printStackTrace();
		} finally {
			fh.close();
		}
	}

	//checks username,password & finally its signIn status
	public String processSignIn(String userName, String password,
			String ipAddress) {
		
		pClient = new PlayerClient();
		String values = ipAddress.substring(0, ipAddress.indexOf("."));
		String geolocation = "";
		
		if (values.equals("132")) {
			
			geolocation = "NA";
			
		} else if (values.equals("93")) {
			
			geolocation = "EU";
	
		} else if (values.equals("182")) {
			
			geolocation = "AS";
		}
		
		//server's log
		String filename = "log/server/"+geolocation + "-server.log";
		createLog(filename,"SignIn request from username:"+userName);
		
		String key = userName.substring(0, 1);
		int status = 0;
		if (hashtable.containsKey(key)) {

			for (int i = 0; i < hashtable.get(key).size(); i++) {
				PlayerClient tempClient = hashtable.get(key).get(i);

				if (userName.equalsIgnoreCase(tempClient.username)) {
					if (password.equalsIgnoreCase(tempClient.password)) {
						pClient = tempClient;
						//syn starts
						if (!tempClient.signInStatus) {
							hashtable.get(key).get(i).signInStatus = true;
							// return "u got signed in!!";
							status = 100;
							break;
						} else {
							// return "already signed in u!!";
							status = 101;
							break;
						}
						//syn ends
					} else {
						// return "invalid password";
						status = 102;
					}
				} else {
					// return "invalid username";
					status = 103;
				}
			}

		}

		if (status == 0) {
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Status: invalid username");
			
			return "User is invalid";
		} else if (status == 100) {
			
			//player's log
			filename = "log/client/"+userName + geolocation + ".log";
			createLog(filename,"Successfully signed in");
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Status: username:"+userName+"Signed in succesfully");
			
			return "You Are Signed In";
		} else if (status == 101) {
			
			//player's log
			String fn2 = "log/client/"+userName + geolocation + ".log";
			createLog(fn2,"Already signed in");
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Status: username:"+userName+"Already Signed in");
			
			return "Already Signed In";
		} else if (status == 102) {
			
			//player's log
			String fn3 = "log/client/"+userName + geolocation + ".log";
			createLog(fn3,"Invalid password");
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Status: username:"+userName+"invalid password");
			
			return "Invalid Password";
			
		} else if (status == 103) {
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Status: invalid username");
			
			return "Invalid Username";
		}
		return "";

	}

	public String processSignOut(String userName, String ipAddress) {
		
		String values = ipAddress.substring(0, ipAddress.indexOf("."));
		
		String geolocation = "";
		if (values.equals("132")) {
			
			geolocation = "NA";
			
		} else if (values.equals("93")) {
			
			geolocation = "EU";
			
		} else if (values.equals("182")) {
			
			geolocation = "AS";
		}
		//server's log
		String filename = "log/server/"+geolocation + "-server.log";
		createLog(filename,"Signout request from username:"+userName);
		
		String key = userName.substring(0, 1);
		int status = 0;
		PlayerClient player = new PlayerClient();
		if (hashtable.containsKey(key)) {

			for (int i = 0; i < hashtable.get(key).size(); i++) {
				PlayerClient tempClient = hashtable.get(key).get(i);

				if (userName.equalsIgnoreCase(tempClient.username)) {
					player = tempClient;
					//syn starts
					if (tempClient.signInStatus) {
						hashtable.get(key).get(i).signInStatus = false;
						// return "u got signed out!!";
						status = 100;
						break;
						
					} else {
						// return "u need to sign in first!!";
						status = 101;
						break;
					
					}
					//syn ends
				} else {
					// return "invalid username";
					status = 103;
				}
			}

		}

		if (status == 0) {
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Status: invalid username");
			
			return "User is invalid";
		
		} else if (status == 100) {
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Status: username: "+userName+" signed out successfully");
			
			//player's log
			String fn3 ="log/client/"+ userName + geolocation + ".log";
			createLog(fn3,"Signed out successfully");
			
			return "You Are Signed Out";
			
		} else if (status == 101) {
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Status: username: "+userName+" already signed out");
			
			//player's log
			String fn3 = "log/client/"+userName + geolocation + ".log";
			createLog(fn3,"Signed out successfully");
			
			return "Already Signed Out!";
		
		} else if (status == 103) {
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Status: invalid username");
			return "Invalid Username";
		
		}
		return "";

	}

	public void exportSever() throws Exception {
		serverNA = new GameServer(portNA);
		serverEU = new GameServer(portEU);
		serverAS = new GameServer(portAS);

		Remote objNA = UnicastRemoteObject.exportObject(serverNA, 4444);
		Remote objEU = UnicastRemoteObject.exportObject(serverEU, 4444);
		Remote objAS = UnicastRemoteObject.exportObject(serverAS, 4444);
		Registry r = LocateRegistry.createRegistry(4444);

		r.rebind("NA", objNA);
		r.rebind("EU", objEU);
		r.rebind("AS", objAS);
	}


	public static void main(String[] args) {
		
		try {
			
			(new GameServer()).exportSever();
			System.out.println("server is running!!");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/*public String processPlayerStatus(String ip){
		return "";
	}*/
	public String processPlayerStatus(String ip) {

		String values = ip.substring(0, ip.indexOf("."));
		
		String finalresult = "", resultforudp = "";
		String geolocation = "";
		if (values.equals("132")) {
			geolocation = "NA";
			String resultforNA=calculateStatus();
			
			//server's log
			String fnn = "log/server/"+geolocation + "-server.log";
			createLog(fnn,"Player status requested from: "+geolocation);
			
			DatagramSocket aSocket = null;
			try {
				aSocket = new DatagramSocket();
				//Hashtable<String, ArrayList<PlayerClient>> data1;

				byte[] m = "call".getBytes();
				InetAddress aHost = InetAddress.getByName("localhost");
				int serverPort = portEU;
				;
				
				DatagramPacket request = new DatagramPacket(m,
						m.length, aHost, serverPort);
				aSocket.send(request);
				

				byte[] buffer = new byte[10000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				
				/*String replyfromudp = new String(reply.getData());
				finalresult = finalresult + replyfromudp;*/
				
				//server's log
				String fn1 = "log/server/"+geolocation + "-server.log";
				createLog(fn1,"From : " + geolocation + " region, result:"
						+ finalresult);
				
				//admmin's log
				String filename = "log/admin.log";
				createLog(filename,"From : " + geolocation + " region, result:"
						+ finalresult);
				return new String(reply.getData());
				//return "return is"+i;
				
			} catch (SocketException e) {
				
				System.out.println("Error in SocketException : " + e.getMessage());
			} catch (IOException e) {
				
				System.out.println("Error in IOException : " + e.getMessage());
			} catch (Exception e) {
				
				System.out.println("Error in Exception : " + e.getMessage());
			} finally {

				if (aSocket != null)
					aSocket.close();

				
			}
			
			//return "Reply : " + finalresult;
			
			
		}/* else if (values.equals("93")) {
			geolocation = "EU";
			finalresult = "EU : " + calculateStatus(htemp.dataEU);
			
			resultforudp = "\nAS : " + calculateStatus(htemp.dataAS) + "\nNA : "+ calculateStatus(htemp.dataNA);
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Player status requested from: "+geolocation);
			
			
			
		} else if (values.equals("182")) {
			geolocation = "AS";
			finalresult = "AS : " + calculateStatus(htemp.dataAS);
			resultforudp = "\nNA : " + calculateStatus(htemp.dataNA) + "\nEU : "
					+ calculateStatus(htemp.dataEU);
			
			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"Player status requested from: "+geolocation);
		}*/
		
		/*DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			Hashtable<String, ArrayList<PlayerClient>> data1;

			byte[] m = resultforNA.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			int serverPort = portEU;

			DatagramPacket request = new DatagramPacket(m,
					resultforudp.length(), aHost, serverPort);
			aSocket.send(request);
			

			byte[] buffer = new byte[10000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			String replyfromudp = new String(reply.getData());
			finalresult = finalresult + replyfromudp;

			//server's log
			String fn1 = "log/server/"+geolocation + "-server.log";
			createLog(fn1,"From : " + geolocation + " region, result:"
					+ finalresult);
			
			//admmin's log
			String filename = "log/admin.log";
			createLog(filename,"From : " + geolocation + " region, result:"
					+ finalresult);
			
		} catch (SocketException e) {
			
			System.out.println("Error in SocketException : " + e.getMessage());
		} catch (IOException e) {
			
			System.out.println("Error in IOException : " + e.getMessage());
		} catch (Exception e) {
			
			System.out.println("Error in Exception : " + e.getMessage());
		} finally {

			if (aSocket != null)
				aSocket.close();

			
		}*/
		
		return "Reply : " + finalresult;

	}

	public String calculateStatus() {
		int online = 0, offline = 0;
		Hashtable<String, ArrayList<PlayerClient>> datatemp=this.hashtable;
		Enumeration<ArrayList<PlayerClient>> e = datatemp.elements();
		
		while (e.hasMoreElements()) {
			ArrayList<PlayerClient> ar = (ArrayList<PlayerClient>) e.nextElement();
			for (int i = 0; i < ar.size(); i++) {
				if (ar.get(i).signInStatus) {
					online++;
				} else {
					offline++;
				}
			}
		}
		
		return online + " online, " + offline + " offline";
	}
}
