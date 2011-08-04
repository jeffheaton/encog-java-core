package org.encog.cloud.basic;

public class CloudPacket {
	
	public static final int PACKET_STATUS = 0;
	public static final int PACKET_LOGIN = 1;
	public static final int PACKET_LOGOUT = 2;
	public static final int PACKET_IDENTIFY = 3;
	
	private final int command;
	private final String[] args;
	
	public CloudPacket(int c, String[] a) {
		this.command = c;
		
		if( a==null ) {
			this.args = new String[0];
		} else {
			this.args = new String[a.length];
		}
		
		for(int i=0;i<this.args.length;i++) {
			this.args[i] = a[i];
		}
	}

	public int getCommand() {
		return command;
	}

	public String[] getArgs() {
		return args;
	}
	
	
	
}
