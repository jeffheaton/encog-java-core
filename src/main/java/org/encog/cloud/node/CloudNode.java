package org.encog.cloud.node;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.cloud.basic.CommunicationLink;
import org.encog.cloud.basic.CloudError;

public class CloudNode implements Runnable
{
	private int port;
	private ServerSocket listenSocket;
	private Thread thread;
	private Map<String,String> accounts = new HashMap<String,String>();
	private Map<String,List<CloudSignal>> signals = new HashMap<String,List<CloudSignal>>();
	
	
	public CloudNode(int p) {
		this.port = p;
	}
	
	public void addUser(String uid, String pwd) {
		this.accounts.put(uid.toLowerCase(), CommunicationLink.simpleHash(pwd));
	}
	
	public CloudNode() {
		this(7500);
	}
	
	public Map<String,String> getAccounts() {
		return this.accounts;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				System.out.println("Begin listen");
				Socket connectionSocket = listenSocket.accept();
				CommunicationLink link = new CommunicationLink(connectionSocket);
				HandleClient hc = new HandleClient(this,link);
				Thread t = new Thread(hc);
				t.start();				
			} catch(IOException ex) {
				throw new CloudError(ex);
			}
		}
	}
	
	public void start() {
		try {
			this.listenSocket = new ServerSocket(port);
			this.thread = new Thread(this);
			this.thread.start();
		} catch(IOException ex) {
			throw new CloudError(ex);
		}
	}
	
	public void addNeededSignal(String clientName, String signalName) {
		String cname = clientName.toLowerCase();
		CloudSignal signal = new CloudSignal(cname,signalName);
		
		if( this.signals.containsKey(cname) ) {
			List<CloudSignal> list = this.signals.get(cname);
			list.add(signal);			
		} else {
			List<CloudSignal> list = new ArrayList<CloudSignal>();
			list.add(signal);
		}
		
		
	}
	
   public static void main(String argv[]) throws Exception
   {
	   CloudNode server = new CloudNode(7500);
	   server.addUser("test","test");
	   server.start();
   }
}