package com.bojin.xmpp;

public class client {
public static void main(String[] args) throws Exception {
        
        String username = "window_curtain1";
        String password = "window_curtain1";
        
        XmppManager xmppManager = new XmppManager("61.222.127.184", 5222);      
        xmppManager.init();
        xmppManager.performLogin(username, password);             
        xmppManager.setStatus(true, "Window Curtain");
        
      //  String buddyJID = "paul_hung@61.222.127.184";
      //  String buddyName = "paul_hung";
      //  xmppManager.createEntry(buddyJID, buddyName);
       
       // xmppManager.sendMessage("Hello mate", "paul_hung@61.222.127.184");
        	xmppManager.sendMessage(args[0], "smartengine@61.222.127.184");
        
        boolean isRunning = true;
        
        while (isRunning) {
        	//xmppManager.getStatus("smartengine");
            Thread.sleep(10*1000);
           /* if(!xmppManager.isConnect())
            {
            	System.out.println("XMPP disconnect");
            	//xmppManager.destroy();
            	xmppManager.performLogin(username, password);
            	xmppManager.setStatus(true, "Window Curtain");
            	xmppManager.sendMessage("Window Curtain online", "smartengine@61.222.127.184");
            }
            else
            {
            	System.out.println("XMPP connected");
            }*/
        }
        
        xmppManager.destroy();
        
    }

}