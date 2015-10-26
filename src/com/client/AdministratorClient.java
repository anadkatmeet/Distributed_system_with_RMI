package com.client;
import java.rmi.Naming;
import java.util.Random;
import java.util.Scanner;

import com.gameServerInterface.GameServerInterface;

public class AdministratorClient {
	GameServerInterface server;
	public static void main(String[] args) {
		
		Scanner in=new Scanner(System.in);
		try {
			System.out.println("Enter your region\n1-NorthAmerica\n2-Europe\n3-Asia");
			int i=in.nextInt();
			String ipAddress=new IpHelper().createIp(i);
			
			String region=new IpHelper().fetchRegion(ipAddress);
			
			/*System.out.println("Enter username");
			String unm=in.next();
			while (!(unm.equals("admin"))) {
				System.out.println("wrong username, please reenter");
				unm = in.next();
			}
			System.out.println("Enter username");
			String pwd=in.next();
			while (!(pwd.equals("admin"))) {
				System.out.println("wrong password, please reenter");
				pwd = in.next();
			}*/
			
			new AdministratorClient().selectServer(ipAddress);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public void selectServer(String ip) {
		  String ans = new IpHelper().fetchRegion(ip);
		  try {
			  
			  server=(GameServerInterface)Naming.lookup("rmi://localhost:4444/"+ans);
			  System.out.println(server.processPlayerStatus(ip));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	

}
