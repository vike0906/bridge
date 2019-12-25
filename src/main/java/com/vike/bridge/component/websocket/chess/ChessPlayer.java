package com.vike.bridge.component.websocket.chess;

/**
 * @author: lsl
 * @createDate: 2019/12/25
 */
public class ChessPlayer implements Chess {

    @Override
    public void exit(String token) {

    }

    @Override
    public void refresh(String token) {

    }

    @Override
    public void giveUp(String token) {

    }

    @Override
    public void setUp(String token, String message) {
        String playerToken = ChessHandler.PLAYER_MATCH_MAP.get(token);
        ChessHandler.sendMessage(playerToken,message);
    }
}
