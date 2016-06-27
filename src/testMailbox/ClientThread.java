package testMailbox;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Robert on 6/27/16.
 */
public class ClientThread implements Runnable{

        private Server server;
        private Socket client;

        public ClientThread(Server server, Socket client) {
            this.server = server;
            this.client = client;
        }

        @Override
        public void run() {

                try {
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String userInput;

                    while (true) {
                        if((userInput = reader.readLine()) != null){
                            try {
                                Gson gson = new Gson();
                                Request request = gson.fromJson(userInput,
                                        Request.class);
                                Response response = server.handleRequest(
                                        request, client);
                                out.println(response.json());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }