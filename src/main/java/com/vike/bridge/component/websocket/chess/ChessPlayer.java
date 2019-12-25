package com.vike.bridge.component.websocket.chess;

/**
 * @author: lsl
 * @createDate: 2019/12/25
 */
public class ChessPlayer implements Chess {

    @Override
    public void exit(String token,String message) {
        if(message == null){
            message = "{\"type\":3}";
        }
        forward(token,message);
    }

    @Override
    public void refresh(String token,String message) {
        forward(token,message);
    }

    @Override
    public void giveUp(String token,String message) {
        forward(token,message);
    }

    @Override
    public void setUp(String token, String message) {
        forward(token,message);
    }

    @Override
    public void win(String token, String message) {
        forward(token,message);
    }

    private void forward(String token, String message){
        String playerToken = ChessHandler.PLAYER_MATCH_MAP.get(token);
        ChessHandler.sendMessage(playerToken,message);
    }
}
