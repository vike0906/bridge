package com.vike.bridge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: lsl
 * @createDate: 2019/11/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TreeNodeVo {
    private String title;
    private String key;
    private Long parentId;
    private List<TreeNodeVo> children;
}
