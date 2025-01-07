import io.javalin.websocket.WsContext;
import okhttp3.internal.ws.RealWebSocket;

import java.net.Socket;

public class User {

    private String id;
    public WsContext connectCxt;

    public User(String id, WsContext socket) {
        this.id = id;
        this.connectCxt = socket;
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WsContext getConnectCxt() {
        return connectCxt;
    }

    public void setConnectCxt(WsContext connectCxt) {
        this.connectCxt = connectCxt;
    }
}
