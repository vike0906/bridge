package com.vike.bridge.component.websocket;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: lsl
 * @createDate: 2019/12/24
 */
public class ChessHandler {

    /**玩家对局排队队列*/
    private static Queue<ChessPlayer> PLAYER_MATCH_QUEUE = new LinkedBlockingQueue<>(100);

    /**消息处理*/
    public void handler(){

    }
}
