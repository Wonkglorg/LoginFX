package com.wonkglorg.fxutility.manager;

import javafx.scene.Node;
import javafx.scene.Parent;

public class NodeUtil {
    public static <T extends Node> T findParentOfType(Node node, Class<T> targetType) {
        if (node == null) {
            return null;
        }

        if (targetType.isInstance(node)) {
            return targetType.cast(node);
        }

        Parent parent = node.getParent();
        if (parent != null) {
            return findParentOfType(parent, targetType);
        }

        return null;
    }

    public static boolean hasParentOfType(Node node, Class<? extends Node> targetType) {
        return findParentOfType(node, targetType) != null;
    }
}
