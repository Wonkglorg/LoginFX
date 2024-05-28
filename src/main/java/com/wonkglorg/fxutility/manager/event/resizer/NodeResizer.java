package com.wonkglorg.fxutility.manager.event.resizer;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

/**
 * ************* How to use ************************
 * <p>
 * Rectangle rectangle = new Rectangle(50, 50);
 * rectangle.setFill(Color.BLACK);
 * DragResizeMod.makeResizable(rectangle, null);
 * <p>
 * Pane root = new Pane();
 * root.getChildren().add(rectangle);
 * <p>
 * primaryStage.setScene(new Scene(root, 300, 275));
 * primaryStage.show();
 * <p>
 * ************* OnDragResizeEventListener **********
 * <p>
 * You need to override OnDragResizeEventListener and
 * 1) preform out of main field bounds check
 * 2) make changes to the node
 * (this class will not change anything in node coordinates)
 * <p>
 * There is defaultListener and it works only with Canvas nad Rectangle
 */
@SuppressWarnings("unused")
public class NodeResizer {

    private double clickX, clickY, nodeX, nodeY, nodeH, nodeW;

    private State state = State.DEFAULT;

    private final Node node;
    private OnDragResizeEventListener listener = defaultListener;

    private static final int MARGIN = 8;
    private static final double MIN_W = 30;
    private static final double MIN_H = 20;

    private NodeResizer(Node node, OnDragResizeEventListener listener) {
        this.node = node;
        if (listener != null)
            this.listener = listener;
    }

    public static void makeResizable(Node node) {
        makeResizable(node, null);
    }

    public static void makeResizable(Node node, OnDragResizeEventListener listener) {
        final NodeResizer resizer = new NodeResizer(node, listener);

        //if event does not register in correct order change this to an event filter, filters get applied before event handlers

        node.setOnMousePressed(resizer::mousePressed);
        node.setOnMouseDragged(resizer::mouseDragged);
        node.setOnMouseMoved(resizer::mouseOver);
        node.setOnMouseReleased(resizer::mouseReleased);
    }

    protected void mouseReleased(MouseEvent event) {
        node.setCursor(Cursor.DEFAULT);
        state = State.DEFAULT;
    }

    protected void mouseOver(MouseEvent event) {
        State state = currentMouseState(event);
        Cursor cursor = getCursorForState(state);
        node.setCursor(cursor);
    }

    private State currentMouseState(MouseEvent event) {
        State state = State.DEFAULT;
        boolean left = isLeftResizeZone(event);
        boolean right = isRightResizeZone(event);
        boolean top = isTopResizeZone(event);
        boolean bottom = isBottomResizeZone(event);

        if (left && top) state = State.NW_RESIZE;
        else if (left && bottom) state = State.SW_RESIZE;
        else if (right && top) state = State.NE_RESIZE;
        else if (right && bottom) state = State.SE_RESIZE;
        else if (right) state = State.E_RESIZE;
        else if (left) state = State.W_RESIZE;
        else if (top) state = State.N_RESIZE;
        else if (bottom) state = State.S_RESIZE;
        else if (isInDragZone(event)) state = State.DRAG;

        return state;
    }

    private static Cursor getCursorForState(State state) {
        return switch (state) {
            case NW_RESIZE -> Cursor.NW_RESIZE;
            case SW_RESIZE -> Cursor.SW_RESIZE;
            case NE_RESIZE -> Cursor.NE_RESIZE;
            case SE_RESIZE -> Cursor.SE_RESIZE;
            case E_RESIZE -> Cursor.E_RESIZE;
            case W_RESIZE -> Cursor.W_RESIZE;
            case N_RESIZE -> Cursor.N_RESIZE;
            case S_RESIZE -> Cursor.S_RESIZE;
            default -> Cursor.DEFAULT;
        };
    }


    protected void mouseDragged(MouseEvent event) {

        if (listener != null) {
            double mouseX = parentX(event.getX());
            double mouseY = parentY(event.getY());
            if (state == State.DRAG) {
                listener.onDrag(node, mouseX - clickX, mouseY - clickY, nodeH, nodeW);
            } else if (state != State.DEFAULT) {
                //resizing
                double newX = nodeX;
                double newY = nodeY;
                double newH = nodeH;
                double newW = nodeW;

                // Right Resize
                if (state == State.E_RESIZE || state == State.NE_RESIZE || state == State.SE_RESIZE) {
                    newW = mouseX - nodeX;
                }
                // Left Resize
                if (state == State.W_RESIZE || state == State.NW_RESIZE || state == State.SW_RESIZE) {
                    newX = mouseX;
                    newW = nodeW + nodeX - newX;
                }

                // Bottom Resize
                if (state == State.S_RESIZE || state == State.SE_RESIZE || state == State.SW_RESIZE) {
                    newH = mouseY - nodeY;
                }
                // Top Resize
                if (state == State.N_RESIZE || state == State.NW_RESIZE || state == State.NE_RESIZE) {
                    newY = mouseY;
                    newH = nodeH + nodeY - newY;
                }

                //min valid rect Size Check
                if (newW < MIN_W) {
                    if (state == State.W_RESIZE || state == State.NW_RESIZE || state == State.SW_RESIZE)
                        newX = newX - MIN_W + newW;
                    newW = MIN_W;
                }

                if (newH < MIN_H) {
                    if (state == State.N_RESIZE || state == State.NW_RESIZE || state == State.NE_RESIZE)
                        newY = newY + newH - MIN_H;
                    newH = MIN_H;
                }

                listener.onResize(node, newX, newY, newH, newW);
            }
        }
    }

    protected void mousePressed(MouseEvent event) {

        if (isInResizeZone(event)) {
            setNewInitialEventCoordinates(event);
            state = currentMouseState(event);
        } else if (isInDragZone(event)) {
            setNewInitialEventCoordinates(event);
            state = State.DRAG;
        } else {
            state = State.DEFAULT;
        }
    }

    private void setNewInitialEventCoordinates(MouseEvent event) {
        nodeX = nodeX();
        nodeY = nodeY();
        nodeH = nodeH();
        nodeW = nodeW();
        clickX = event.getX();
        clickY = event.getY();
    }

    private boolean isInResizeZone(MouseEvent event) {
        return isLeftResizeZone(event) || isRightResizeZone(event)
                || isBottomResizeZone(event) || isTopResizeZone(event);
    }

    private boolean isInDragZone(MouseEvent event) {
        double xPos = parentX(event.getX());
        double yPos = parentY(event.getY());
        double nodeX = nodeX() + MARGIN;
        double nodeY = nodeY() + MARGIN;
        double nodeX0 = nodeX() + nodeW() - MARGIN;
        double nodeY0 = nodeY() + nodeH() - MARGIN;

        return (xPos > nodeX && xPos < nodeX0) && (yPos > nodeY && yPos < nodeY0);
    }

    /**
     * Default listener implementation
     */
    private static final OnDragResizeEventListener defaultListener = new OnDragResizeEventListener() {
        @Override
        public void onDrag(Node node, double x, double y, double h, double w) {
            /*
            // TODO find generic way to get parent width and height of any node
            // can perform out of bounds check here if you know your parent size
            if (x > width - w ) x = width - w;
            if (y > height - h) y = height - h;
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            */
            setNodeSize(node, x, y, h, w);
        }

        @Override
        public void onResize(Node node, double x, double y, double h, double w) {
            /*
            // TODO find generic way to get parent width and height of any node
            // can perform out of bounds check here if you know your parent size
            if (w > width - x) w = width - x;
            if (h > height - y) h = height - y;
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            */
            setNodeSize(node, x, y, h, w);
        }

        private void setNodeSize(Node node, double x, double y, double h, double w) {
            node.setLayoutX(x);
            node.setLayoutY(y);
            // TODO find generic way to set width and height of any node
            // here we cant set height and width to node directly.
            // or i just cant find how to do it,
            // so you have to wright resize code anyway for your Nodes...
            //something like this
            if (node instanceof Canvas) {
                ((Canvas) node).setWidth(w);
                ((Canvas) node).setHeight(h);
            } else if (node instanceof Rectangle) {
                ((Rectangle) node).setWidth(w);
                ((Rectangle) node).setHeight(h);
            }
        }
    };

    private boolean isLeftResizeZone(MouseEvent event) {
        return intersect(0, event.getX());
    }

    private boolean isRightResizeZone(MouseEvent event) {
        return intersect(nodeW(), event.getX());
    }

    private boolean isTopResizeZone(MouseEvent event) {
        return intersect(0, event.getY());
    }

    private boolean isBottomResizeZone(MouseEvent event) {
        return intersect(nodeH(), event.getY());
    }

    private boolean intersect(double side, double point) {
        return side + MARGIN > point && side - MARGIN < point;
    }

    private double parentX(double localX) {
        return nodeX() + localX;
    }

    private double parentY(double localY) {
        return nodeY() + localY;
    }

    private double nodeX() {
        return node.getBoundsInParent().getMinX();
    }

    private double nodeY() {
        return node.getBoundsInParent().getMinY();
    }

    private double nodeW() {
        return node.getBoundsInParent().getWidth();
    }

    private double nodeH() {
        return node.getBoundsInParent().getHeight();
    }
}