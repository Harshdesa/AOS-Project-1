/*
*AOS Project 1 : Class : Advanced Operating Systems 
*Designer : Harsh Desai
*Net id : hbd140030 
*Module : Server 
* Description : This is the server and it will keep on running till the program terminates 
*/
import java.io.*;
import java.net.*;
import com.sun.nio.sctp.*;
import java.nio.*;
import java.util.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;

public class ServerT implements Runnable
{

public static int flagToConvergeCast=0;
public static final int MESSAGE_SIZE = 100;
ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);
Charset charset = Charset.forName("ISO-8859-1");
CharsetDecoder decoder = charset.newDecoder();
public static int numberOfNodesToConnect;
CharBuffer cbuf = CharBuffer.allocate(60);
String message;
String s;
Scanner in = new Scanner(System.in);
int flagToBootClient = 0;	// once server accepts connection, it will set the flag to 1 and now the node can boot it's own client
int flagToRunClient = 1;
public static int prevNode=0;
public static String messagePass=null;
public static int count=0;
public static int flagToPassOnTheMessage=0;
public static int countToChangeServerLoop=0;
CharsetEncoder encoder = charset.newEncoder();
public static int numberOfReplies=0;
public static int a;
public static int blah=0;
	public ServerT()
	{
	}

	public void run()
	{
		try
		{
			
			
			SctpServerChannel sctpServerChannel = null;
			sctpServerChannel = SctpServerChannel.open();
			InetSocketAddress serverAddr = new InetSocketAddress(Node.portServer);
			sctpServerChannel.bind(serverAddr);
		
			System.out.println("Awaiting connection");
				

			/*
			*	If it is the 1st pc then it will start the client first and initiate the minimum spanning tree algorithm
			*
			*
			*

			*/
			
			if(Node.pc==1)
			{
				System.out.println("Do you think all Servers are ready and it's safe to start the client?(y only)");
				s="y";		// CHANGE HERE FOR AUTOMATED
				if(s.equals("y"))
				{
					Node.visited=1;
					new Thread(new ClientT()).start();
				}
			}
			String msgIn=null;
			MessageInfo messageInfo = null;


			/*
			*This is the actual running of the server 
			*
			*
			*
			*
			*/
			
			while(true)
			{

				try 
				{
  					Thread.sleep(1000);
				} 
				catch (InterruptedException ie) 
				{
   					 //Handle exception
				}
				SctpChannel sctpChannel = sctpServerChannel.accept();
				messageInfo = sctpChannel.receive(byteBuffer,System.out,null);
				byteBuffer.flip();
				if (byteBuffer.remaining() > 0 ) {
                		msgIn=decoder.decode(byteBuffer).toString();
               			 //System.out.println("MSG RECEIVED: " +msgIn );
            			}
           			 byteBuffer.clear();
				a=Integer.parseInt(msgIn);

				messageInfo = sctpChannel.receive(byteBuffer,System.out,null);
				byteBuffer.flip();
				
				/*
				*
				*THree types of messages may come : 
				* 1. connect 
				* 2. send 
				* 3. acknowedgement
				*/


				if (byteBuffer.remaining() > 0 ) {
                		msgIn=decoder.decode(byteBuffer).toString();
               			// System.out.println("MSG RECEIVED: " +msgIn );
            			}
           			 byteBuffer.clear();
					
				if(msgIn.equals("connect"))
				{
					flagToPassOnTheMessage=0;
				}
				if(msgIn.equals("send"))
				{
					flagToPassOnTheMessage=1;

				}
				if(msgIn.equals("ack"))
				{
					flagToPassOnTheMessage=2;

				}
				/*
				*This part is used only for creation of the minimum spanning tree 
				*	
				*/
				if(flagToPassOnTheMessage==0)
				{
					if((Node.visited==0))		// If node is not visited 
					{
						Node.visited=1;
					//	System.out.println("It is accepted here");
						Node.treeneighbors[Node.z]=a;		
			System.out.println("Node "+ Node.pc+"'s tree neighbor is : " + Node.treeneighbors[Node.z]);				
						Node.z++;

						char[] today=new char[1024];
						today=Integer.toString(Node.pc).toCharArray();
						cbuf.put(today).flip();
						encoder.encode(cbuf, byteBuffer, true);
						byteBuffer.flip();
						//System.out.println(byteBuffer.toString());
						messageInfo = MessageInfo.createOutgoing(null,0);
						sctpChannel.send(byteBuffer,messageInfo);
						cbuf.clear();
						byteBuffer.clear();
					
						new Thread(new ClientT()).start();				
					}
					if(Node.visited==1)		// If node is visited 
					{
						String nack = "nack";
										
						char[] today=new char[1024];
						today=nack.toCharArray();
						cbuf.put(today).flip();
						encoder.encode(cbuf, byteBuffer, true);
						byteBuffer.flip();
						//System.out.println(byteBuffer.toString());
						messageInfo = MessageInfo.createOutgoing(null,0);
						sctpChannel.send(byteBuffer,messageInfo);
						cbuf.clear();
						byteBuffer.clear();
					}
				}

				/*
				*This part is used only for forwarding messages to its tree neighbours 
				*	
				*/

				else if(flagToPassOnTheMessage==1)	
				{
					count++;				
					
					messagePass = null;			
					prevNode=a;					
					System.out.println();
					messageInfo = sctpChannel.receive(byteBuffer,System.out,null);
					byteBuffer.flip();
					if (byteBuffer.remaining() > 0 ) {
                			messagePass=decoder.decode(byteBuffer).toString();
               	System.out.println("Received from : " + prevNode + "  Received at :  " + Node.pc + "  MSG RECEIVED:  " +messagePass );
            				}
           			 	byteBuffer.clear();
		
					//messagePass = in.readUTF();
					//System.out.println(messagePass);	//Print the message received
					
					new Thread(new ClientT()).start();

					
				}
				

				/*
				*This part is used only for forwarding messages as acknowedgements  
				*	
				*/

				else if(flagToPassOnTheMessage==2)
				{
					count++;
					blah=1;				
					
					messagePass = null;			
					//prevNode=a;			// Not the previous node, we have to send to prevNode		
					
					messageInfo = sctpChannel.receive(byteBuffer,System.out,null);
					byteBuffer.flip();
					if (byteBuffer.remaining() > 0 ) {
                			messagePass=decoder.decode(byteBuffer).toString();
               	System.out.println(" Received from :  " + a + "   Received at :  " + Node.pc +"  MSG RECEIVED:  " +messagePass );
            				}
           			 	byteBuffer.clear();
		
					//messagePass = in.readUTF();
					//System.out.println(messagePass);	//Print the message received
					
					new Thread(new ClientT()).start();
					
					



				}

				sctpChannel.close();
			}	
  	}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	
	}

}
