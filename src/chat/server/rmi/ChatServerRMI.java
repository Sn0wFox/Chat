package chat.server.rmi;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

import chat.client.rmi.RemotableChatClientItf;
import chat.protocol.Message;

public class ChatServerRMI implements ChatServerItf
{
	private LinkedList<RemotableChatClientItf> clientList;
	private Registry registry;
	public final int SERVER_PORT = 1099;

	/**
	 * 
	 * @throws RemoteException
	 */
	public ChatServerRMI() throws RemoteException
	{
		clientList = new LinkedList<RemotableChatClientItf>();
		LocateRegistry.createRegistry(SERVER_PORT);
		registry = LocateRegistry.getRegistry();
	}

	public static void main(String[] args)
	{
		try
		{
			ChatServerRMI cs = new ChatServerRMI();

			// Launching and getting RMI register
			ChatServerItf chi = (ChatServerItf) UnicastRemoteObject.exportObject(cs, 0);
			cs.registry.bind("server", chi);

			System.out.println("Server ready on port " + cs.SERVER_PORT);
		} catch (Exception e)
		{
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessageToAll(Message msg) throws RemoteException, NotBoundException
	{
		// Log du message
		try
		{
			if(!msg.isHistorique())
			{
				FileWriter writer = new FileWriter("rmichatlogfile.txt", true);
				writer.write(msg + "\n");
				writer.flush();
			}
		} catch (IOException e)
		{
			System.err.println("Error while opening log file");
			e.printStackTrace();
		}
		
		// Envoie a tout les clients
		System.out.println("sendMessageToAll recois le message : " + msg);
		System.out.println("Il va etre transmis a " + clientList.size() + " clients");
		for (RemotableChatClientItf user : clientList)
		{
			user.printMessage(msg.toString());
		}
	}

	@Override
	public void addChatClient(RemotableChatClientItf user)
	{
		clientList.add(user);
	}

	@Override
	public void removeChatClient(RemotableChatClientItf user)
	{
		clientList.remove(user);
	}

}
