package rmiPin;

import java.rmi.RemoteException;

public class PinnwandClient {

	public static void main(String[] args) {
		Pinnwand pinnwand = new PinnwandIpml(20,60000,160,"pinnwand");
		try{
			pinnwand.putMessage("My first message");
			pinnwand.putMessage("My second message!!");
			System.out.println(pinnwand.getMessageCount());
			System.out.println(pinnwand.getMessage(1));
		} catch(RemoteException e){
			e.printStackTrace();
		}




	}

}
