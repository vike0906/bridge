package com.vike.bridge.component.websocket.chess;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vike.bridge.component.websocket.WebSocketPool;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: lsl
 * @createDate: 2019/12/24
 */
@Slf4j
public class ChessHandler {

    private static Gson gson = new Gson();
    
    private static JsonParser JSON_PARSER = new JsonParser();

    /**玩家对局排队队列*/
    private static Queue<String> PLAYER_MATCH_QUEUE = new LinkedBlockingQueue<>(100);

    /**玩家对手信息集合*/
    public static java.util.Map<String,String> PLAYER_MATCH_MAP = new HashMap<>();

    /**电脑标识*/
    private static final String COMPUTER_TAG = "COMPUTER";

    private static Chess chess = new ChessPlayer();

    /**消息处理*/
    public static void handler(String token, String message){
        if(!message.contains("type")){
            return;
        }
        gson.toJson(message);
        JsonObject jsonObject = JSON_PARSER.parse(message).getAsJsonObject();
        int type = jsonObject.getAsJsonPrimitive("type").getAsInt();
        switch (type){
            case 1:
                //初始化 加入队列
                match(token,jsonObject);
                break;
            case 2:
                PLAYER_MATCH_QUEUE.remove(token);
                break;
            case 3:
                //退出游戏
                chess.exit(token,message);
                break;
            case 4:
                //请求重新开局
                chess.refresh(token,message);
                break;
            case 5:
                //认输
                chess.giveUp(token,message);
                break;
            case 6:
                //走棋
                chess.setUp(token,message);
                break;
            case 7:
                //赢棋
                chess.win(token,message);
                break;
            default:
                String errorMessage = "{\"type\":100,\"content\":0}";
                sendMessage(token,errorMessage);
                break;
        }
    }

    private static void match(String token, JsonObject message){
        int content = message.getAsJsonPrimitive("content").getAsInt();
        if(content==1){
            //玩家对战
            ChessMessage chessMessage = new ChessMessage();
            if(PLAYER_MATCH_QUEUE.size()>0){

                String poll = PLAYER_MATCH_QUEUE.poll();

                chess.init(token,poll,true);

                chessMessage.setType(ChessConstant.MATCH_SUCCESS);

                chessMessage.setIsFirst(ChessConstant.FIRST);
                sendMessage(token,gson.toJson(chessMessage));

                chessMessage.setIsFirst(ChessConstant.SECOND);
                sendMessage(poll,gson.toJson(chessMessage));

            }else {

                chessMessage.setType(ChessConstant.MATCHING);
                sendMessage(token,gson.toJson(chessMessage));
                PLAYER_MATCH_QUEUE.offer(token);
            }
        }else if(content==2){
            //人机对战
            chess.init(token, COMPUTER_TAG,false);
        }

    }

    protected  static void close(String token){
        PLAYER_MATCH_QUEUE.remove(token);
        chess.exit(token,null);
    }

    /**发送消息*/
    protected static void sendMessage(String token, String msg){
        Session session = WebSocketPool.gainSession(ChessWebSocketEndPoint.WEB_SOCKET_TAG + token);
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
}
