package rmiPin;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class PinnwandClient {

	public static String serviceName = "pinnwand";

	public static void main(String[] args) {

		Registry registry;

		Pinnwand pinnwand;

		try{
			registry = LocateRegistry.getRegistry("localhost");

			pinnwand = 	(PinnwandImpl) registry.lookup(serviceName);

			pinnwand.putMessage("My first message");
			pinnwand.putMessage("My second message!!");
			System.out.println(pinnwand.getMessageCount());
			System.out.println(pinnwand.getMessage(1));
		} catch(RemoteException | NotBoundException e){
			e.printStackTrace();
		}


		try{
			pinnwand = (PinnwandImpl) Naming.lookup("rmi://localhost/pinnwand");
		} catch(RemoteException | NotBoundException | MalformedURLException e){
			e.printStackTrace();
		}




	}

}
