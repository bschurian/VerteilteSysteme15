package testMailbox;

import com.google.gson.Gson;
import com.sun.xml.internal.ws.api.message.Packet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert on 6/20/16.
 */
public class Server {

    private Server server;
    private final int MAXUSERS = 5;
    private final int PORTNUMBER = 8090;
    private List<Note> notes;
    private List<User> users;
    private ServerSocket serverSocket;
    private int notesDuration;

    public Server(){
        try {
            this.serverSocket = new ServerSocket(this.PORTNUMBER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        notes = new ArrayList<>();
        users = new ArrayList<>();
        this.notesDuration = 60;
        this.server = this;
    }

    public Server getServer() {
        return server;
    }

    public int getMAXUSERS() {
        return MAXUSERS;
    }

    public int getPORTNUMBER() {
        return PORTNUMBER;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public List<User> getUsers() {
        return users;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public boolean checkUsernameFree(String username){
        for(User user : users){
            if(user.getUsername().equals(username)){
                return false;
            }
        }
        return true;
    }

    public boolean checkLoggedIn(Socket socket){
        for(User user : users){
            if(user.getClientSocket().equals(socket)){
                return true;
            }
        }
        return false;
    }

    public Response handleRequest(Request request, Socket client){
        String command = request.getCommand();
        StatusAndData info = null;

        switch(command){
            case "login":
                Command login = new Command(){

                    @Override
                    public StatusAndData execute(String[] para, Socket client) {
                        StatusAndData information;
                        if(server.getUsers().size() >= server.getMAXUSERS()){
                            information = new StatusAndData(StatusCodes.SERVICEUNAVAILABLE, new String[]{"Maximal allowed count of users reached."});
                            return information;
                        }
                        if(!checkUsernameFree(para[0])){
                            information = new StatusAndData(StatusCodes.SERVICEUNAVAILABLE, new String[]{"Username already taken"});
                            return information;
                        }
                        users.add(new User(para[0], client));
                        return new StatusAndData(StatusCodes.OKMITANTWORT, new String[]{"Welcome to the server"});
                    }
                };
                info = login.execute(request.getParams(), client);
                break;

            case "logout":
                Command logout = new Command(){

                    @Override
                    public StatusAndData execute(String[] para, Socket client) {
                        StatusAndData information;
                        if(!checkLoggedIn(client)){
                            information = new StatusAndData(StatusCodes.NICHTEINGELOGGT, new String[]{"You are not logged in."});
                            return information;
                        }
                        User toBeRemoved = null;
                        for(User user : users){
                            if(user.getClientSocket().equals(client)){
                                toBeRemoved = user; break;
                            }
                        }
                        if(toBeRemoved != null){
                            users.remove(toBeRemoved);
                        }
                        return new StatusAndData(StatusCodes.OKMITANTWORT, new String[]{"You've been successfully logged out"});
                    }
                };
                info = logout.execute(request.getParams(), client);
                break;

            case "who":
                Command who = new Command(){

                    @Override
                    public StatusAndData execute(String[] para, Socket client) {
                        StatusAndData information;
                        if(!checkLoggedIn(client)){
                            information = new StatusAndData(StatusCodes.NICHTEINGELOGGT, new String[]{"You are not logged in."});
                            return information;
                        }
                        List<String> usernames = new ArrayList<>();
                        List<User> users = server.getUsers();
                        for(User user : users){
                            usernames.add(user.getUsername());
                        }
                        return new StatusAndData(StatusCodes.OKMITANTWORT, usernames.toArray(new String[usernames.size()]));
                    }
                };
                info = who.execute(request.getParams(), client);
                break;

            case "ls":
                Command ls = new Command(){

                    @Override
                    public StatusAndData execute(String[] para, Socket client) {
                        StatusAndData information;
                        if(!checkLoggedIn(client)){
                            information = new StatusAndData(StatusCodes.NICHTEINGELOGGT, new String[]{"You are not logged in."});
                            return information;
                        }
                        File file = new File(para[0]);
                        if(file.list() == null){
                            return new StatusAndData(StatusCodes.FEHLRHAFTEANFRAGE, new String[]{"File with this path doesn't exist"});
                        }
                        return new StatusAndData(StatusCodes.OKMITANTWORT, file.list());
                    }
                };
                info = ls.execute(request.getParams(), client);
                break;

            case "time":
                Command time = new Command() {

                    @Override
                    public StatusAndData execute(String[] para, Socket client) {
                        StatusAndData information;
                        if (!checkLoggedIn(client)) {
                            information = new StatusAndData(StatusCodes.NICHTEINGELOGGT, new String[]{"You are not logged in."});
                            return information;
                        }
                        return new StatusAndData(StatusCodes.OKMITANTWORT, new String[]{java.time.LocalDateTime.now().toString()});
                    }
                };
                info = time.execute(request.getParams(), client);
                break;

            case "chat":
                Command chat = new Command() {

                    @Override
                    public StatusAndData execute(String[] para, Socket client) {
                        StatusAndData information;
                        if(!checkLoggedIn(client)){
                            information = new StatusAndData(StatusCodes.NICHTEINGELOGGT, new String[]{"You are not logged in."});
                            return information;
                        }
                        // check if there is a user with this name
                        if(checkUsernameFree(para[0])){
                            information = new StatusAndData(StatusCodes.FEHLRHAFTEANFRAGE, new String[]{"There isn't a user with this username"});
                            return information;
                        }
                        User contact = null;
                        for(User user : server.getUsers()){
                            if(user.getUsername().equals(para[0])){
                                contact = user;
                            }
                        }
                        String message = "";
                        for(int i = 1; i < para.length; i++){
                            message += para[i] + " ";
                        }
                        System.out.println(message);
                        contact.getOut().println(message);
                        return new StatusAndData(StatusCodes.OKOHNEANTWORT, null);
                    }
                };
                info = chat.execute(request.getParams(), client);
                break;
            case "notify":
                Command notify = new Command(){

                    @Override
                    public StatusAndData execute(String[] para, Socket client){
                        StatusAndData information;
                        if(!checkLoggedIn(client)){
                            information = new StatusAndData(StatusCodes.NICHTEINGELOGGT, new String[]{"You are not logged in."});
                            return information;
                        }
                        String message = "";

                        for(int i = 0; i < para.length; i++){
                            message += para[i] + " ";
                        }
                        for(User user : server.getUsers()){
                            user.getOut().println(message);
                        }
                        return new StatusAndData(StatusCodes.OKOHNEANTWORT, null);
                    }
                };
                info = notify.execute(request.getParams(), client);
                break;
            case "note":
                Command note = new Command(){

                    @Override
                    public StatusAndData execute(String[] para, Socket client) {
                        StatusAndData information;
                        if(!checkLoggedIn(client)){
                            information = new StatusAndData(StatusCodes.NICHTEINGELOGGT, new String[]{"You are not logged in."});
                            return information;
                        }
                        String message = "";
                        for(int i = 0; i < para.length; i++){
                            message += para[i] + " ";
                        }
                        server.getNotes().add(new Note(message));
                        return new StatusAndData(StatusCodes.OKOHNEANTWORT, null);
                    }
                };
                info = note.execute(request.getParams(), client);
                break;
            case "notes":
                Command notes = new Command(){

                    @Override
                    public StatusAndData execute(String[] para, Socket client) {
                        StatusAndData information;
                        if(!checkLoggedIn(client)){
                            information = new StatusAndData(StatusCodes.NICHTEINGELOGGT, new String[]{"You are not logged in."});
                            return information;
                        }
                        List<String> noteMessages = new ArrayList<>();
                        for(Note note : server.notes){
                            noteMessages.add(note.getMessage());
                        }
                        return new StatusAndData(StatusCodes.OKMITANTWORT, noteMessages.toArray(new String[noteMessages.size()]));
                    }
                };
                info = notes.execute(request.getParams(), client);
                break;
            default:
                info = new StatusAndData(StatusCodes.NOTIMPLEMENTED, new String[]{"Command not supported"});

        }
        return new Response(info.getStatus(), request.getSequence(), info.getData());

    }

    public void cullNotes(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    List<Note> notes = server.getNotes();
                    int maxIndex = notes.size() - 1;
                    for (int i = maxIndex; i >= 0; i--) {
                        Note note = notes.get(i);
                        if (System.nanoTime() > note.getCreationTime()
                                + server.notesDuration * 1000000 * 1000
                                ) {
                            notes.remove(i);
                        }
                    }
                    try {
                        synchronized (this) {//this bezieht sich hier auf das runnable
                            this.wait(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }




    public static void main(String[] args) {
        Server server = new Server();
        server.cullNotes();

        connectionBuild: while (true) {
            try {
                final Socket client = server.getServerSocket().accept();
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                String userInput;
                // calculation and sending of fib(n)
                while ((userInput = new BufferedReader(new InputStreamReader(client.getInputStream())).readLine()) != null) {
                    try {
                        Gson gson = new Gson();
                        Request request = gson.fromJson(userInput, Request.class);
                        Response response = server.handleRequest(request, client);
                        out.println(response.json());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }



    private class Note{
        private long creationTime;
        private String message;

        Note(String message) {
            creationTime = System.nanoTime();
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public long getCreationTime() {
            return creationTime;
        }
    }

    private class User{
        private String username;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public User(String username, Socket clientSocket) {
            this.username = username;
            this.clientSocket = clientSocket;
            try {
                this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getUsername() {
            return username;
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        public PrintWriter getOut() {
            return out;
        }

        public BufferedReader getIn() {
            return in;
        }
    }

    private class StatusAndData{
        private final int status;
        private final String[] data;

        public StatusAndData(int status, String[] data) {
            super();
            this.status = status;
            this.data = data;
        }

        public int getStatus() {
            return this.status;
        }

        public String[] getData() {
            return this.data;
        }
    }

    private abstract class Command{
        private String name;
        public abstract StatusAndData execute(String[] para, Socket client);
    }

}
