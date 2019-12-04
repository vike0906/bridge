package com.vike.bridge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: lsl
 * @createDate: 2019/12/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TreeDataVo {

    private List<TreeNodeVo> tree;

    private String[] expandedKeys;

    private String[] CheckedKeys;
}
