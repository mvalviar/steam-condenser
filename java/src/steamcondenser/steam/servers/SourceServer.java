/** 
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 */

package steamcondenser.steam.servers;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import steamcondenser.RCONNoAuthException;
import steamcondenser.SteamCondenserException;
import steamcondenser.steam.packets.rcon.RCONAuthRequestPacket;
import steamcondenser.steam.packets.rcon.RCONAuthResponse;
import steamcondenser.steam.packets.rcon.RCONExecRequestPacket;
import steamcondenser.steam.packets.rcon.RCONExecResponsePacket;
import steamcondenser.steam.packets.rcon.RCONPacket;
import steamcondenser.steam.sockets.RCONSocket;
import steamcondenser.steam.sockets.SourceSocket;

/**
 * A Source game server.
 * @author Sebastian Staudt
 * @version $Id$
 */
public class SourceServer extends GameServer
{
    	protected RCONSocket rconSocket;
    
	/**
	 * @param ipAddress The IP of the server to connect to
	 * @param portNumber The port number of the server
	 */
	public SourceServer(InetAddress ipAddress, int portNumber)
		throws IOException
	{
		super(portNumber);
		this.rconSocket = new RCONSocket(ipAddress, portNumber);
		this.socket = new SourceSocket(ipAddress, portNumber);
	}
	
	/**
	 * Authenticate via RCON
	 * @throws IOException 
	 * @throws SteamCondenserException 
	 * @throws TimeoutException 
	 */
	public boolean rconAuth(String password)
		throws IOException, TimeoutException, SteamCondenserException
	{
		this.rconRequestId = new Random().nextInt();
		
		this.rconSocket.send(new RCONAuthRequestPacket(this.rconRequestId, password));
		this.rconSocket.getReply();
		RCONAuthResponse reply = (RCONAuthResponse) this.rconSocket.getReply();
		return (reply.getRequestId() == this.rconRequestId);
	}
	
	/**
	 * Execute a command on the server via RCON
	 * @throws IOException 
	 * @throws SteamCondenserException 
	 * @throws TimeoutException 
	 */
	public String rconExec(String command) 
		throws IOException, TimeoutException, SteamCondenserException
	{
		this.rconSocket.send(new RCONExecRequestPacket(this.rconRequestId, command));
		RCONPacket reply = this.rconSocket.getReply();
		if(reply instanceof RCONAuthResponse)
		{
			throw new RCONNoAuthException();
		}
		
		return ((RCONExecResponsePacket) reply).getResponse();
	}
}