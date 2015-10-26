package com.client;

import java.io.Serializable;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

import com.gameServerInterface.GameServerInterface;

public class PlayerClient implements Serializable,Runnable {

	public String username, firstName, lastName, password;
	public int age;
	public boolean signInStatus = false;
	public String ipAdd;
	static transient GameServerInterface server1,server2,server3;	//1-NA,2-EU,3-AS

	public static void main(String[] args) {
		try {
			server1=(GameServerInterface) Naming
					.lookup("rmi://localhost:4444/NA");
			server2=(GameServerInterface) Naming
					.lookup("rmi://localhost:4444/EU");
			server3=(GameServerInterface) Naming
					.lookup("rmi://localhost:4444/AS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Scanner in = new Scanner(System.in);
		try {
			
			String val = "y";
			while (val.equalsIgnoreCase("y")) {

				menu();
				int i = in.nextInt();
				if (i == 1) {
					(new PlayerClient()).createPlayerAccount();
				}
				if (i == 2) {
					//new PlayerClient().playerSignIn();
				}
				if (i == 3) {
					//new PlayerClient().playerSignOut();
				}
				if (i == 4){
					//new PlayerClient().multiThreading();
				
				}
				if (i==5) {
					break;
				}
				System.out.println("Do you wish to continue ? y/n");
				val = in.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//multithreading using 10 threads creating players for NA server simultaneously
	public void multiThreading(){
		Runnable run=new PlayerClient();

		Thread t1=new Thread(run);
		Thread t2=new Thread(run);
		Thread t3=new Thread(run);
		Thread t4=new Thread(run);
		Thread t5=new Thread(run);
		Thread t6=new Thread(run);
		Thread t7=new Thread(run);
		Thread t8=new Thread(run);
		Thread t9=new Thread(run);
		Thread t10=new Thread(run);
		t1.start();t2.start();t3.start();t4.start();t5.start();
		t6.start();t7.start();t8.start();t9.start();t10.start();
		
	}
	public void run(){
		PlayerClient temp=new PlayerClient();
		temp.username=Thread.currentThread().getName();
		temp.password="random";
		temp.lastName="meet";
		temp.firstName="anadkat";
		temp.ipAdd=new IpHelper().createIp(1);
		try {
			//server=(GameServerInterface)Naming.lookup("rmi://localhost:4444/NA");
			//System.out.println(temp.username+": "+server.playerCreation("NA", temp));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	private void playerSignOut() {
		Scanner in = new Scanner(System.in);
		System.out.println("Enter your region\n1-NorthAmerica\n2-Europe\n3-Asia");
		int i = in.nextInt();
		String ipAddress = new IpHelper().createIp(i);
		System.out.println("ipAddress : " + ipAddress);

		String region = new IpHelper().fetchRegion(ipAddress);
		System.out.println("region : " + region);
		selectServer(ipAddress);

		System.out.println("enter ur username");
		String userName = in.next();

		try {
			System.out.println("userName : " + userName);
			System.out.println("ipAddress : " + ipAddress);

			String value = server.processSignOut(userName, ipAddress);
			System.out.println("value : " + value);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}
	*/
	public static void menu() {
		System.out.println("Hey user!!");
		System.out.println("1. Create a new account");
		System.out.println("2. Sign in");
		System.out.println("3. Sign out");
		System.out.println("4. View multithreading demo");
		System.out.println("5. Exit");
	}

	public void createPlayerAccount() {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter your region\n1-NorthAmerica\n2-Europe\n3-Asia");
		int i = keyboard.nextInt();
		this.ipAdd = new IpHelper().createIp(i);
		System.out.println("Enter username between 6 to 15 characters");
		String tempString = keyboard.next();
		while (!(tempString.length() >= 6 && tempString.length() <= 15)) {
			System.out
					.println("Username is not proper. Enter username between 6 to 15 characters");
			tempString = keyboard.next();
		}
		this.username = tempString;
		
		System.out.println("Enter password at least 6 characters long");
		tempString = keyboard.next();
		while (!(tempString.length() >= 6)) {
			System.out
					.println("password is not proper. Enter password at least 6 characters long");
			tempString = keyboard.next();
		}
		this.password = tempString;
		
		System.out.println("Enter ur age");
		this.age = keyboard.nextInt();
		
		System.out.println("Enter fnm");
		this.firstName = keyboard.next();
		System.out.println("Enter lnm");
		this.lastName = keyboard.next();
		

		try {
			String ans = new IpHelper().fetchRegion(ipAdd);
			String answer="";
			if (ans.equals("NA")) {
				answer = server1.playerCreation(new IpHelper().fetchRegion(this.ipAdd), this);
			}else if(ans.equals("EU")){
				answer = server2.playerCreation(new IpHelper().fetchRegion(this.ipAdd), this);
			}else if(ans.equals("AS")){
				answer = server3.playerCreation(new IpHelper().fetchRegion(this.ipAdd), this);
			}
			System.out.println(answer);
			//selectServer(this.ipAdd);			
			//String answer = server.playerCreation(new IpHelper().fetchRegion(this.ipAdd), this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	

	public void playerSignIn() {
		Scanner in = new Scanner(System.in);
		System.out.println("Enter your region\n1-NorthAmerica\n2-Europe\n3-Asia");
		int i = in.nextInt();
		String ipAddress = new IpHelper().createIp(i);
		System.out.println("ipAddress : " + ipAddress);

		String region = new IpHelper().fetchRegion(ipAddress);
		System.out.println("region : " + region);
		selectServer(ipAddress);

		System.out.println("enter ur username");
		String userName = in.next();
		System.out.println("enter ur password");
		String password = in.next();
		try {
			System.out.println("userName : " + userName);
			System.out.println("password : " + password);
			System.out.println("ipAddress : " + ipAddress);

			//String value = server.processSignIn(userName, password, ipAddress);
			//System.out.println("value : " + value);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	public void selectServer(String ip) {
		String ans = new IpHelper().fetchRegion(ip);
		try {
			//server = (GameServerInterface) Naming.lookup("rmi://localhost:4444/" + ans);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
}
