/*
*AOS Project 1 : Class : Advanced Operating Systems 
*Designer : Harsh Desai
*Net id : hbd140030 
*Module : ClientT
* Description : This is Client. It is always called by the server. It is used for inputting messages and also for forwarding purposes. It *initiates the broadcast.
*/


import java.io.*;
import java.net.*;
import com.sun.nio.sctp.*;
import java.nio.*;
import java.io.IOException;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
public class ClientT implements Runnable
{

SctpChannel sctpChannel;
public static final int MESSAGE_SIZE = 5000;
ByteBuffer byteBuffer = ByteBuffer.allocate(60);
CharBuffer cbuf = CharBuffer.allocate(60);
Charset charset = Charset.forName("ISO-8859-1");
public static Enumeration ports;
CharsetEncoder encoder = charset.newEncoder();

CharsetDecoder decoder = charset.newDecoder();
String message;
String sendmessage;
int portNo;
String s;
Scanner in = new Scanner(System.in);
int flagtostopconnect = 0;
int i=0;
String hostname = null;
String ackmessage;
String ack;
int flagToTerminateTheLoop=0;

public String mainMessage = null;
	public ClientT()
	{
	}

	public void run()
	{
		
		try {	
			sctpChannel = null;
			//sctpChannel = SctpChannel.open();
			//sctpChannel.bind(new InetSocketAddress(Node.portServer));
		//	System.out.println("Checkpoint for Bind Client");	
			while(flagToTerminateTheLoop<1)
			{
				message="connect";
				sendmessage="send";
				ackmessage="ack";
				int a;
				Node.timer = Node.pc*Node.n*1000;
				while(ServerT.count<ServerT.numberOfNodesToConnect)
				{
					portNo=Node.arrayOfNodes[Node.connectedNode[ServerT.count]][1];
					hostname=Node.host_names[Node.connectedNode[ServerT.count]] + ".utdallas.edu";
				//	System.out.println("here is the neighbor Node's port I'm gonna connect to :" + portNo);
				InetSocketAddress serverAddr = new InetSocketAddress(hostname,portNo);				
					//sctpChannel.connect(socketAddress);
					String nodename = Integer.toString(Node.pc);
					sctpChannel = SctpChannel.open(serverAddr, 0, 0);					
					char[] today=new char[1024];
					today=nodename.toCharArray();
					cbuf.put(today).flip();
					encoder.encode(cbuf, byteBuffer, true);
					byteBuffer.flip();
					//System.out.println(byteBuffer.toString());
					MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);
					sctpChannel.send(byteBuffer,messageInfo);
					cbuf.clear();
					byteBuffer.clear();					
					
					today=message.toCharArray();		// message
					cbuf.put(today).flip();	
					encoder.encode(cbuf, byteBuffer, true);
					byteBuffer.flip();
					//System.out.println(byteBuffer.toString());
					messageInfo = MessageInfo.createOutgoing(null,0);
					sctpChannel.send(byteBuffer,messageInfo);
					cbuf.clear();
					byteBuffer.clear();
				
				//	System.out.println("So , the data is sent");
					String lala=null;

					messageInfo = sctpChannel.receive(byteBuffer,System.out,null);
					byteBuffer.flip();
					if (byteBuffer.remaining() > 0 ) {
                				lala=decoder.decode(byteBuffer).toString();
               				//	 System.out.println("MSG RECEIVED: " +lala );
            					}
           			 	byteBuffer.clear();
					

					if(lala.equals("nack"))
					{
					//	System.out.println("Negative acknowledgement is received");
						Node.replyCount++;
					}
					else
					{
						a = Integer.parseInt(lala);
            					//System.out.println(a);
						Node.treeneighbors[Node.z]=a;
						int x = Node.z+1;
			System.out.println("Node "+ Node.pc+"'s tree neighbor(received as an acknowledgement) is : " + Node.treeneighbors[Node.z] + " Node "+ Node.pc + " now has "+ x + "neighbours" );				
						Node.z++;
						Node.replyCount++;
					}
					try 
					{
  						Thread.sleep(1000);
					} 
					catch (InterruptedException ie) 
					{
   					}
					ServerT.count++;
					sctpChannel.close();
				}


				// For forwarding the message to it's tree neighbours
				if(ServerT.count==ServerT.numberOfNodesToConnect)
				{
				//	System.out.println("Spanning Tree Termination Reached");		
					ServerT.flagToPassOnTheMessage=1;
					//
					
					//System.out.println("Number of tree neighbors check : "+Node.z);
					//System.out.println("Enter message : ");
					
								
					try 
					{
  						Thread.sleep(Node.timer);
					} 
					catch (InterruptedException ie) 
					{
   						 //Handle exception
					}
					mainMessage="HELLO THIS IS NODE : " + Node.pc;		// Enter the MAIN MESSAGE HERE 	
					System.out.println("Node "+ Node.pc +"sends " + mainMessage);					
					flagToTerminateTheLoop=1;		
					Node.timer=Node.timer+100000;	
					int number = 0;
					ServerT.prevNode=0;
					ServerT.messagePass = null;
					flagToTerminateTheLoop=1;
					ServerT.numberOfReplies=0;
					while(number<Node.z)
					{
						portNo=Node.arrayOfNodes[Node.treeneighbors[number]][1];
						hostname=Node.host_names[Node.treeneighbors[number]] + ".utdallas.edu";
		InetSocketAddress serverAddr = new InetSocketAddress(hostname,portNo);
						sctpChannel = SctpChannel.open(serverAddr, 0, 0);
						//Socket client = new Socket("127.0.0.1", portNo);
						String nodename = Integer.toString(Node.pc);
						char[] today=new char[1024];
						today=nodename.toCharArray();
						cbuf.put(today).flip();
						encoder.encode(cbuf, byteBuffer, true);
						byteBuffer.flip();
						//System.out.println(byteBuffer.toString());
						MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);
						sctpChannel.send(byteBuffer,messageInfo);
						cbuf.clear();
						byteBuffer.clear();

						today=sendmessage.toCharArray();
						cbuf.put(today).flip();
						encoder.encode(cbuf, byteBuffer, true);
						byteBuffer.flip();
						//System.out.println(byteBuffer.toString());
						messageInfo = MessageInfo.createOutgoing(null,0);
						sctpChannel.send(byteBuffer,messageInfo);
						cbuf.clear();
						byteBuffer.clear();

						today=mainMessage.toCharArray();
						cbuf.put(today).flip();
						encoder.encode(cbuf, byteBuffer, true);
						byteBuffer.flip();
						//System.out.println(byteBuffer.toString());
						messageInfo = MessageInfo.createOutgoing(null,0);
						sctpChannel.send(byteBuffer,messageInfo);
						cbuf.clear();
						byteBuffer.clear();
							
					//	System.out.println("Message is sent after entering");
						sctpChannel.close();
						number++;
					}
				}
	
				if(ServerT.count>(ServerT.numberOfNodesToConnect))
				{	
				  if(ServerT.blah==0)
				  {
					int number = 0;
					while(number<Node.z)
					{
						portNo=Node.arrayOfNodes[Node.treeneighbors[number]][1];
						if(ServerT.prevNode != Node.treeneighbors[number])
						{	
							hostname=Node.host_names[Node.treeneighbors[number]] + ".utdallas.edu";

				InetSocketAddress serverAddr = new InetSocketAddress(hostname,portNo);
							sctpChannel = SctpChannel.open(serverAddr, 0, 0);
							//Socket client = new Socket("127.0.0.1", portNo);
							String nodename = Integer.toString(Node.pc);
							char[] today=new char[1024];
							today=nodename.toCharArray();
							cbuf.put(today).flip();
							encoder.encode(cbuf, byteBuffer, true);
							byteBuffer.flip();
							//System.out.println(byteBuffer.toString());
							MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);
							sctpChannel.send(byteBuffer,messageInfo);
							cbuf.clear();
							byteBuffer.clear();

							today=sendmessage.toCharArray();
							cbuf.put(today).flip();
							encoder.encode(cbuf, byteBuffer, true);
							byteBuffer.flip();
							//System.out.println(byteBuffer.toString());
							messageInfo = MessageInfo.createOutgoing(null,0);
							sctpChannel.send(byteBuffer,messageInfo);
							cbuf.clear();
							byteBuffer.clear();
	
							today=ServerT.messagePass.toCharArray();	// This is the message forwarded
							cbuf.put(today).flip();
							encoder.encode(cbuf, byteBuffer, true);
							byteBuffer.flip();
							//System.out.println(byteBuffer.toString());
							messageInfo = MessageInfo.createOutgoing(null,0);
							sctpChannel.send(byteBuffer,messageInfo);
							cbuf.clear();
							byteBuffer.clear();
	
							ServerT.flagToConvergeCast=1;	
							flagToTerminateTheLoop=1;		
System.out.println("Message is received from "+ServerT.prevNode + " and received at "+Node.pc+" and sent to "+ Node.treeneighbors[number]);
							sctpChannel.close(); 
						}
						number++;
						
					}
					


					if(ServerT.flagToConvergeCast==0)
					{
						number = 0;
						while(number<Node.z)
						{
						
							portNo=Node.arrayOfNodes[Node.treeneighbors[number]][1];
							if(ServerT.prevNode == Node.treeneighbors[number])
							{
								hostname=Node.host_names[Node.treeneighbors[number]] + ".utdallas.edu";

							InetSocketAddress serverAddr = new InetSocketAddress(hostname,portNo);
								sctpChannel = SctpChannel.open(serverAddr, 0, 0);
								//Socket client = new Socket("127.0.0.1", portNo);
								String nodename = Integer.toString(Node.pc);
								char[] today=new char[1024];
								today=nodename.toCharArray();
								cbuf.put(today).flip();
								encoder.encode(cbuf, byteBuffer, true);
								byteBuffer.flip();
								//System.out.println(byteBuffer.toString());
								MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);
								sctpChannel.send(byteBuffer,messageInfo);
								cbuf.clear();
								byteBuffer.clear();

								today=ackmessage.toCharArray();
								cbuf.put(today).flip();
								encoder.encode(cbuf, byteBuffer, true);
								byteBuffer.flip();
								//System.out.println(byteBuffer.toString());
								messageInfo = MessageInfo.createOutgoing(null,0);
								sctpChannel.send(byteBuffer,messageInfo);
								cbuf.clear();
								byteBuffer.clear();
								
								ack="It has reached an end node i.e. Node "+ Integer.toString(Node.pc);
								today=ack.toCharArray();	// This is the message forwarded
								cbuf.put(today).flip();
								encoder.encode(cbuf, byteBuffer, true);
								byteBuffer.flip();
								//System.out.println(byteBuffer.toString());
								messageInfo = MessageInfo.createOutgoing(null,0);
								sctpChannel.send(byteBuffer,messageInfo);
								cbuf.clear();
								byteBuffer.clear();
							

								System.out.println("Node "+Node.pc +" says this is the END NODE");
						//		System.out.println("Acknowledgement is sent");
								sctpChannel.close(); 
								flagToTerminateTheLoop=1;
								
								
							}
							number++;
						}
					}
					ServerT.count=ServerT.numberOfNodesToConnect;
				  }

				  if(ServerT.blah==1)
			  	  {
					if(ServerT.prevNode>0)
					{
						
							hostname=Node.host_names[ServerT.prevNode] + ".utdallas.edu";
							portNo=Node.arrayOfNodes[ServerT.prevNode][1];
				InetSocketAddress serverAddr = new InetSocketAddress(hostname,portNo);
							sctpChannel = SctpChannel.open(serverAddr, 0, 0);
							//Socket client = new Socket("127.0.0.1", portNo);
							String nodename = Integer.toString(Node.pc);
							char[] today=new char[1024];
							today=nodename.toCharArray();
							cbuf.put(today).flip();
							encoder.encode(cbuf, byteBuffer, true);
							byteBuffer.flip();
							//System.out.println(byteBuffer.toString());
							MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);
							sctpChannel.send(byteBuffer,messageInfo);
							cbuf.clear();
							byteBuffer.clear();

							today=ackmessage.toCharArray();
							cbuf.put(today).flip();
							encoder.encode(cbuf, byteBuffer, true);
							byteBuffer.flip();
							//System.out.println(byteBuffer.toString());
							messageInfo = MessageInfo.createOutgoing(null,0);
							sctpChannel.send(byteBuffer,messageInfo);
							cbuf.clear();
							byteBuffer.clear();
	
							today=ServerT.messagePass.toCharArray();	// This is the message forwarded
							cbuf.put(today).flip();
							encoder.encode(cbuf, byteBuffer, true);
							byteBuffer.flip();
							//System.out.println(byteBuffer.toString());
							messageInfo = MessageInfo.createOutgoing(null,0);
							sctpChannel.send(byteBuffer,messageInfo);
							cbuf.clear();
							byteBuffer.clear();
							ServerT.count=ServerT.numberOfNodesToConnect;
							ServerT.numberOfReplies++;
							if(ServerT.numberOfReplies==(Node.z-1))
							{
								flagToTerminateTheLoop=1;

							}
							else 
							{
								flagToTerminateTheLoop=1;
							}
	System.out.println("Message is received from "+ServerT.a +" and received at " +Node.pc +" and sent to "+ ServerT.prevNode);
							sctpChannel.close(); 
					}
					else
					{
						ServerT.numberOfReplies++;
							if(ServerT.numberOfReplies==(Node.z))
							{
								flagToTerminateTheLoop=1;

							}
							else 
							{
								flagToTerminateTheLoop=1;
							}
						ServerT.count=ServerT.numberOfNodesToConnect;
					}

				ServerT.blah=0;

				  }







					
				}

			
				



			}
		}
	
		catch(IOException e)
		{
			e.printStackTrace();
		}
		

	}


	





}
