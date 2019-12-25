package com.vike.bridge.component.websocket;

import lombok.*;

/**
 * @author: lsl
 * @createDate: 2019/12/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChessMessage{

    private Integer type;

    private Integer content;

    private String name;

    private Integer isFirst;

    private Integer x;

    private Integer y;

}
