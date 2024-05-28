package com.wonkglorg.fxutility.manager.event.resizer;

import javafx.scene.Node;

/**
 * Drag Resize interface handles how the node resizing is calculated
 */
public interface OnDragResizeEventListener {
    void onDrag(Node node, double x, double y, double h, double w);

    void onResize(Node node, double x, double y, double h, double w);
}
