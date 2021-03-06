package com.vike.bridge.component.websocket.chess;

/**
 * @author: lsl
 * @createDate: 2019/12/24
 */
public interface Chess {

    /**初始化*/
    default void init(String firstToken, String secondToken, boolean isPlayer){
        if(isPlayer){
            ChessHandler.PLAYER_MATCH_MAP.put(firstToken,secondToken);
            ChessHandler.PLAYER_MATCH_MAP.put(secondToken,firstToken);
        }else {
            ChessHandler.PLAYER_MATCH_MAP.put(firstToken,secondToken);
        }
    }

    /**退出游戏*/
    void exit(String token,String message);

    /**重新开始*/
    void refresh(String token,String message);

    /**认输*/
    void giveUp(String token,String message);

    /**走棋*/
    void setUp(String token, String message);

    void win(String token,String message);

}
