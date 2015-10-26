package com.gameServerInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.client.PlayerClient;

public interface GameServerInterface extends Remote {
	
	public String playerCreation(String region,PlayerClient client) throws RemoteException;
	public String processSignIn(String userName,String password,String ipAddress) throws RemoteException;
	public String processSignOut(String userName,String ipAddress) throws RemoteException;
	public String processPlayerStatus(String ipAddress) throws RemoteException;
}
