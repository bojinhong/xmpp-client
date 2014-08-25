package com.bojin.xmpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import javax.security.sasl.SaslException;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class XmppManager {
    
    private static final int packetReplyTimeout = 500; // millis
    
    private String server;
    private int port;
    
    private ConnectionConfiguration config;
    private XMPPTCPConnection connection;

    private ChatManager chatManager;
    private MessageListener messageListener;
    
    public XmppManager(String server, int port) {
        this.server = server;
        this.port = port;
    }
    
    public void init() throws XMPPException {
        
        System.out.println(String.format("Initializing connection to server %1$s port %2$d", server, port));

        //SmackConfiguration.setDefaultPacketReplyTimeout(packetReplyTimeout);
        
        config = new ConnectionConfiguration(server, port);
        //config.setSASLAuthenticationEnabled(false);
        config.setCompressionEnabled(true);
        config.setSecurityMode(SecurityMode.disabled);
        
        connection = new XMPPTCPConnection(config);
        try {
			connection.connect();
		} catch (SmackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("Connected: " + connection.isConnected());
        
        //chatManager = ChatManager.getInstanceFor(connection);
        chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(new ChatManagerListener(){
        	public void chatCreated(final Chat chat, final boolean createdLocally){
        		chat.addMessageListener(new MessageListener(){
        			public void processMessage(Chat chat,Message message){
        				System.out.println("Received Message:"+message.getBody());
        				/*  try {
        		            	Process p = null;
        		            	if(message.getBody().equals("relay_on"))
        		            	{
        		            		p = Runtime.getRuntime().exec("gpio write 2 1");
        		            		
        		            		System.out.println("Received Message:"+message.getBody());
        		            	}
        		            	if(message.getBody().equals("relay_off"))
        		            	{
        		            		p = Runtime.getRuntime().exec("gpio write 2 0");
        		            		System.out.println("Received Message:"+message.getBody());
        		            	}
        		            	String line;
        		                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        		                while ((line = input.readLine()) != null) {
        		                    System.out.println(line);
        		                }
        		                input.close();
        				
        				    } catch (IOException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}*/
        			}
        		});
        	}
        });
        //messageListener = new MyMessageListener();
        
    }
    
    public void performLogin(String username, String password) throws XMPPException {
        if (connection!=null && connection.isConnected()) {
            try {
				connection.login(username, password);
			} catch (SaslException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SmackException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public void setStatus(boolean available, String status) {
        
        Presence.Type type = available? Type.available: Type.unavailable;
        Presence presence = new Presence(type);
        
        presence.setStatus(status);
        try {
			connection.sendPacket(presence);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public void getStatus(String user)
    {
    	Roster roster = connection.getRoster();
    	Collection<RosterEntry> entries = roster.getEntries();
    	Presence presence;
    	System.out.println("get user status.");
    	for(RosterEntry entry : entries) {
    	    presence = roster.getPresence(entry.getUser());
    	    //System.out.println("222");
    	    if((user+"@61.222.127.184").equals(entry.getUser())||(user.equals("all")))
    	    {
    	    	System.out.println(entry.getUser());
    	    	System.out.println(presence.getType().name());
    	    }
    	    //System.out.println(presence.getStatus());
    	}
    }
    
    public void destroy() {
        if (connection!=null && connection.isConnected()) {
            try {
				connection.disconnect();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    public boolean isConnect()
    {
    	return connection.isConnected();
    }
    
    public void sendMessage(String message, String buddyJID) throws XMPPException {
        System.out.println(String.format("Sending mesage '%1$s' to user %2$s", message, buddyJID));
        Chat chat = chatManager.createChat(buddyJID, messageListener);
    
        try {
			chat.sendMessage(message);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void createEntry(String user, String name) throws Exception {
        System.out.println(String.format("Creating entry for buddy '%1$s' with name %2$s", user, name));
        Roster roster = connection.getRoster();
        roster.createEntry(user, name, null);
    }
    
    class MyMessageListener implements MessageListener {

        @Override
        public void processMessage(Chat chat, Message message) {
            String from = message.getFrom();
            String body = message.getBody();
            System.out.println(String.format("Received message '%1$s' from %2$s", body, from));
           
           /* try {
            	Process p = null;
            	if(body.equals("relay_on"))
            	{
            		p = Runtime.getRuntime().exec("gpio write 2 1");
            	}
            	if(body.equals("relay_off"))
            	{
            		p = Runtime.getRuntime().exec("gpio write 2 0");
            	}
            	String line;
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                }
                input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
        }
        
    }
    
}
