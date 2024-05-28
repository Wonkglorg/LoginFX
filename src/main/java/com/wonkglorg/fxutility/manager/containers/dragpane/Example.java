package com.wonkglorg.fxutility.manager.containers.dragpane;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Example {
    public PannableCanvas getDataBaseView(Node node) {

        //group of items to be added to the node
        Group group = new Group();

        // create canvas
        PannableCanvas canvas = new PannableCanvas();

        // create nodes which can be dragged using this class
        NodeGestures nodeGestures = new NodeGestures(canvas);

        //creates an example object
        Circle circle1 = new Circle(300, 300, 50);
        circle1.setStroke(Color.ORANGE);
        circle1.setFill(Color.ORANGE.deriveColor(1, 1, 1, 1));
        //always add the corresponding 2 events to and the node gesture consumer to the node
        circle1.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        circle1.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Rectangle rect1 = new Rectangle(100, 100);
        rect1.setTranslateX(450);
        rect1.setTranslateY(450);
        rect1.setStroke(Color.BLUE);
        rect1.setFill(Color.BLUE.deriveColor(1, 1, 1, 1));
        //always add the corresponding 2 events to and the node gesture consumer to the node
        rect1.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        rect1.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        canvas.getChildren().addAll(circle1, rect1);

        group.getChildren().add(canvas);

        // creates the scene which registers the panning and zooming events
        SceneGestures sceneGestures = new SceneGestures(canvas);
        //register all 3 listeners to the node
        node.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        node.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        node.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
        //canvas.addGrid();
        return canvas;
    }
}
