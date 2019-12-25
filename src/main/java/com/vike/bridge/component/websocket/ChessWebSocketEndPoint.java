package com.vike.bridge.component.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;

import static com.vike.bridge.component.websocket.WebSocketPool.*;

/**
 * @author: lsl
 * @createDate: 2019/12/23
 */
@Slf4j
@Component
@ServerEndpoint("/webSocket/chess/{token}")
public class ChessWebSocketEndPoint {

    private static final String WEB_SOCKET_TAG = "chess";

    private void sendMessage(Session session, String msg){
        if (session == null)
            return;
        final RemoteEndpoint.Basic basic = session.getBasicRemote();
        if (basic == null)
            return;
        try {
            basic.sendText(msg);
        } catch (IOException e) {
            log.error("sendText Exception: {}", e);
        }
    }

    @OnOpen
    public void onOpen(@PathParam("token") String token, Session session){

        log.info("有新的连接：{}",token);

        String key = WEB_SOCKET_TAG+token;

        add(key, session);
        String initMessage = "{\"type\":1,\"content\":0}";
        sendMessage(session, initMessage);

        log.info("在线人数：{}",count());
    }

    @OnMessage
    public void onMessage(String message, Session session){
        log.info("有新消息： {},{}", message,session);
    }

    @OnClose
    public void onClose(@PathParam("token") String token,Session session){
        log.info("连接关闭： {}", token);
        String key = WEB_SOCKET_TAG+token;
        try {
            session.close();
        } catch (IOException e) {
            log.error("onError Exception: {}", e);
        }
        remove(key);
        log.info("在线人数：{}",count());
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        try {
            session.close();
        } catch (IOException e) {
            log.error("onError Exception: {}", e);
        }
        log.info("连接出现异常： {}", throwable);
    }
}
