package io.bekti.anubis.server.http;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class KafkaWebSocketServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(Long.MAX_VALUE);
        factory.register(KafkaWebSocketServletHandler.class);
    }

}