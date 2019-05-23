package io.sanwishe.wsdemo.handler;

import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomHandler extends TextWebSocketHandler {
    private static Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private static String getUserName(WebSocketSession session) {
        String name = session.getUri().getQuery();

        if (StringUtils.isEmpty(name)) {
            name = session.getRemoteAddress().toString();
        }

        return name;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userName = getUserName(session);

        if (sessions.containsKey(userName)) {
            session.sendMessage(new TextMessage(String.format("### User :%s is exist, please pick a different username and try again.", userName)));
            session.close();
            return;
        }
        sessions.put(userName, session);

        for (Map.Entry<String, WebSocketSession> sessionEntry : sessions.entrySet()) {
            if (userName.equals(sessionEntry.getKey())) {
                continue;
            }

            sessionEntry.getValue().sendMessage(new TextMessage(String.format("### %s is on line.", userName)));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userName = getUserName(session);

        for (Map.Entry<String, WebSocketSession> sessionEntry : sessions.entrySet()) {
            WebSocketSession userSession = sessionEntry.getValue();
            if (sessionEntry.getKey().equals(userName)) {
                userSession.sendMessage(new TextMessage(String.format("%s : You say: \n%s", new Date().toString(), message.getPayload())));
                continue;
            }

            userSession.sendMessage(new TextMessage(String.format("%s : %s say: \n%s", new Date().toString(), userName, message.getPayload())));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.sendMessage(new TextMessage("Sorry that your message do not successfully send to all peers, please try later"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userName = getUserName(session);
        sessions.remove(userName);

        for (Map.Entry<String, WebSocketSession> sessionEntry : sessions.entrySet()) {
            WebSocketSession userSession = sessionEntry.getValue();

            if (userSession.isOpen()) {
                userSession.sendMessage(new TextMessage(String.format("### %s has going offline.", userName)));
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }
}
