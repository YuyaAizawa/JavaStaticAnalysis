/* @(#)ResizeButton.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javax.annotation.Nonnull;

/**
 * ResizeButton.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ResizeButton extends Region {

    private Point2D pressed;
    private Point2D size;
    private final ObjectProperty<Region> target = new SimpleObjectProperty<>();

    public ResizeButton() {
        setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, null, null)));
        setOnMousePressed(this::mousePressed);
        setOnMouseDragged(this::mouseDragged);
        setCursor(Cursor.V_RESIZE);
    }

    public Region getTarget() {
        return target.get();
    }

    public void setTarget(Region value) {
        target.set(value);
    }

    private void mouseDragged(@Nonnull MouseEvent evt) {
        final Region t = getTarget();
        if (t != null && pressed != null) {
            Point2D current = new Point2D(evt.getSceneX(), evt.getSceneY());
            Point2D delta = current.subtract(pressed);
            t.setPrefWidth(size.getX() + delta.getX());
            t.setPrefHeight(size.getY() + delta.getY());
        }
    }

    private void mousePressed(@Nonnull MouseEvent evt) {
        final Region t = getTarget();
        if (t != null) {
            pressed = new Point2D(evt.getSceneX(), evt.getSceneY());
            size = new Point2D(getTarget().getWidth(), t.getHeight());
        }
    }

    @Nonnull
    public ObjectProperty<Region> targetProperty() {
        return target;
    }

}
