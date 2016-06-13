package mailbox;

import com.google.gson.Gson;

public class Request{

    int sequence;
    String command;
    String[] params;

    public Request(int sequence, String command, String[] params){
        this.sequence = sequence;
        this.command = command;
        this.params = params;
    }

    public int getSequence() {
        return sequence;
    }

    public String getCommand() {
        return command;
    }

    public String[] getParams() {
        return params;
    }

    public String json(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}