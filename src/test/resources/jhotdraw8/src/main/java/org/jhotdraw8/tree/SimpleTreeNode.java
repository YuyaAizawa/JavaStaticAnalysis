package org.jhotdraw8.tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleTreeNode<V> implements TreeNode<SimpleTreeNode<V>> {
    private List<SimpleTreeNode<V>> children;
    @Nullable
    private SimpleTreeNode<V> parent;
    private V value;

    public SimpleTreeNode() {
    }

    public SimpleTreeNode(V value) {
        this.value = value;
    }

    public void addChild(@Nonnull SimpleTreeNode<V> child) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        if (children == null) children = new ArrayList<>();
        children.add(child);
        child.parent = this;
    }

    public void removeChild(@Nonnull SimpleTreeNode<V> child) {
        if (children != null) {
            children.remove(child);
            child.parent = null;
        }
    }

    @Nonnull
    @Override
    public List<SimpleTreeNode<V>> getChildren() {
        return children == null ? Collections.emptyList() : Collections.unmodifiableList(children);
    }

    @Nullable
    @Override
    public SimpleTreeNode<V> getParent() {
        return null;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
