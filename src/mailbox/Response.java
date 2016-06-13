package mailbox;

import com.google.gson.Gson;

public class Response {

    private int status;
    private int sequence;
    private String[] data;

    public Response(int status, int sequence, String[] data) {
        this.status = status;
        this.sequence = sequence;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public int getSequence() {
        return sequence;
    }

    public String[] getData() {
        return data;
    }

    public String json(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
