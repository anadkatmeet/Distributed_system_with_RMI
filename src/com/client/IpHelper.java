package com.client;

import java.rmi.Naming;
import java.util.Random;

import com.gameServerInterface.GameServerInterface;

public class IpHelper {
	
	public String fetchRegion(String ip) {
		String values = ip.substring(0, ip.indexOf("."));
	  if (values.equals("132")){
	    return "NA";
	  }else if (values.equals("93")){
		return "EU";
	  }else if (values.equals("182")){
	    return "AS";
	  }
	return "";
	}
	public String createIp(int region){
		String prefix = "";
		if (region==1){
			prefix = "132";
		}else if (region==2){
			prefix = "93";
		}else if (region==3){
			prefix = "182";
		}
		for (int i = 0; i < 3; i++) {
			prefix = prefix + "." + new Random().nextInt(255);
		}
		return prefix.substring(0, prefix.length()-1);
		
	}
	
}
