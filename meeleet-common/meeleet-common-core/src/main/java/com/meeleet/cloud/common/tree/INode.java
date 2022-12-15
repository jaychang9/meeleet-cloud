package com.meeleet.cloud.common.tree;

import java.io.Serializable;
import java.util.List;

/**
 * 节点接口
 */
public interface INode extends Serializable {

    Long getId();

    Long getParentId();

    List<INode> getChildren();

    default Boolean getHasChildren() {
        return false;
    }
}
