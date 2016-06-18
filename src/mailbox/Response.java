package mailbox;

import com.google.gson.Gson;

/**
 * @author anwender
 *
 */
public class Response {
    private final int status;
    private final int sequence;
    private final String[] data;
    
    /**	An Answer of a request to a Mailserver 
     * @param status statuscodes representing the success of the request
     * @param sequence the identifier on the client-side for the request
     * @param data the additional, optional data to the response
     */
    public Response(final int status, final int sequence, final String[] data) {
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
        final Gson gson = new Gson();
        return gson.toJson(this);
    }
}
