package com.vike.bridge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: lsl
 * @createDate: 2019/11/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ActionVo {

    private long id;

    private long parentId;

    private String name;

    private String icon;

    private String url;

    private ActionVo[] subAction;
}
