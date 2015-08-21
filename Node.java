/*
*AOS Project 1 : Class : Advanced Operating Systems 
*Designer : Harsh Desai
*Net id : hbd140030 
*Module : Node 
* Description : This is Node. It reads the configuration file and sets many parameters to make the program function properly
*/
import java.io.*;
import java.net.*;
import com.sun.nio.sctp.*;
import java.nio.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.String.*;
import java.lang.Integer.*;

	
public class Node implements Runnable
{	
		
	public static int n;						// Number of nodes
	protected boolean      isStopped    = false;
	protected Thread       runningThread= null;
	public static int portServer,portClient; 			// Server's port
	public static int portCltoServ;
	private static final int MYTHREADS = 2;
	public static Hashtable<Integer, ArrayList<Integer>> portInfo= new Hashtable<Integer, ArrayList<Integer>>();
	public static Enumeration ports;
	public static Integer keyValue;
	public static int connectedNode[] = new int[5];
	public static int pc;
	public static int arrayOfNodes[][] = new int[50][50];
	public static int visited=0;					// Variable to tell if node is visited or not
	public static int treeneighbors[]=new int[15];			// Stores it's tree neighbours
	public static int z=0;						// Stores the node counter
	public static int replyCount=0;					// Stores the number of reply messages received
	public static String[] host_names = new String[20];		// Stores the host names
	public static int timer;					// timer to call the client periodically
	public Node()
	{
		
	}


	public void run(){
				new Thread(new ServerT()).start();
				
	}
        /*
	*This code reads the Config file.It also stores the necessary variables. 
	*/
    	public void readFromFile(String pcnumber)
	{

		int pc = Integer.parseInt(pcnumber);
		String fileName = "Config";
		String line = null;
		try 
		{
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) 
			{
				line.trim();
				if(line.equals(""))
				{
				}
				else if(line.equals("# Number of nodes"))
				{
					line = bufferedReader.readLine();
					line.replaceAll("\\s+","");
					n=Integer.parseInt(line);
				}
				else if(line.contains("# Hostname"))
				{
					for(int i=1;i<=n;i++)
					{
						line = bufferedReader.readLine();
						String delims = "[\\s]+";
						String[] tokens = line.split(delims);
						host_names[i]=tokens[0];	// From 1 to n
						for (int j = 1; j < tokens.length; j++)
						{	
							arrayOfNodes[i][j]=Integer.parseInt(tokens[j]);
    						}							
						if(i==pc)
						{
							System.out.println(line);
							ServerT.numberOfNodesToConnect=tokens.length-2;		
						}
					}
				}
				else if(line.charAt(0)=='#')
				{
				}
			}    
			bufferedReader.close();            
		}
		catch(FileNotFoundException ex) 
		{
			System.out.println("Unable to open file '" + fileName + "'");                
		}
		catch(IOException ex) 
		{
			System.out.println("Error reading file '" + fileName + "'");  
		}
	}

	//Main function
	public static void main(String args[])
	{
		//Constructor for the node
		Node node = new Node();

		//pc is the current node
		pc = Integer.parseInt(args[0]);
	
		//calls to read the file
		node.readFromFile(args[0]);
		int i=1;

		//stores what can connect to the current node
		while(i<=pc)
		{
			if(i==pc)
			{
				portServer=arrayOfNodes[i][1];
			
				for(int j=0,k=2;j<ServerT.numberOfNodesToConnect;j++,k++)
				{
					connectedNode[j]=arrayOfNodes[i][k];
					System.out.println(connectedNode[j] + " can connect");

				}
			}
			i++;
		}
		
		//Starts that node's server
		new Thread(node).start();
	}
}
