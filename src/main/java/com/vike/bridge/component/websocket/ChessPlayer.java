package com.vike.bridge.component.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: lsl
 * @createDate: 2019/12/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChessPlayer {

    private String token;

    private String name;
}
