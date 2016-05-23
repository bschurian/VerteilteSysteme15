package rmiPin;

import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class PinnwandClient {

	public static void main(String[] args) {

		Pinnwand pinnwand;
		try{
			pinnwand = 	(PinnwandIpml) Naming.lookup("rmi://localhost/pinnwand");

			pinnwand.putMessage("My first message");
			pinnwand.putMessage("My second message!!");
			System.out.println(pinnwand.getMessageCount());
			System.out.println(pinnwand.getMessage(1));
		} catch(MalformedURLException | RemoteException | NotBoundException e){
			e.printStackTrace();
		}




	}

}
