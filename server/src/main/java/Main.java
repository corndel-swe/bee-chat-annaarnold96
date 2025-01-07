import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {

        Javalin app = Javalin.create(javalinConfig -> {
            // Modifying the WebSocketServletFactory to set the socket timeout to 120 seconds
            javalinConfig.jetty.modifyWebSocketServletFactory(jettyWebSocketServletFactory ->
                    jettyWebSocketServletFactory.setIdleTimeout(Duration.ofSeconds(120))
            );
        });

        List<User> users = new ArrayList<>();

        app.ws("/", wsConfig -> {

            wsConfig.onConnect((connectContext) -> {
                System.out.println("Connected: " + connectContext.sessionId());
                User user = new User(String.valueOf(users.size()+1),connectContext);
                users.add(user);
                System.out.println("Currently connected: " + users.size());

            });

            wsConfig.onMessage((messageContext) -> {
                System.out.println("Message sent by: " + messageContext.sessionId());

                ObjectMapper mapper = new ObjectMapper();
                JsonNode messageTree = mapper.readTree(messageContext.message());
                String rId = messageTree.get("recipientId").asText();
                String messageContent = messageTree.get("content").asText();

                if (Objects.equals(rId, "")) {
                    for (User i : users) {
                        String userSessionId = i.connectCxt.sessionId();
                        if (userSessionId == messageContext.sessionId()) {
                            i.connectCxt.send(Map.of("content", "You said: " + messageContent));
                        }else {
                            i.connectCxt.send(Map.of("content", "They said: " + messageContent));
                        }
                    }
                }
                else {
                    for (User i : users){
                        String userSessionId = i.connectCxt.sessionId();
                        if (userSessionId == messageContext.sessionId()) {
                            i.connectCxt.send(Map.of("content", "You said: " + messageContent));
                        } else if (Objects.equals(i.getId(), rId)){
                            i.connectCxt.send(Map.of("content", "They said: " + messageContent));
                        }
                    }

                }

            });

            wsConfig.onClose((closeContext) -> {
                System.out.println("Closed: " + closeContext.sessionId());
            });

            wsConfig.onError((errorContext) -> {
                System.out.println("Error: " + errorContext.sessionId());
            });

        });

        app.start(5001);
    }
}
