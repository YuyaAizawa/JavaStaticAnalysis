/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

/**
 * Handle for the point which defines a control point  for a point.
 * The coordinates of the control point are relative to the point.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RelativeControlPointHandle extends AbstractHandle {

    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Rectangle REGION_SHAPE = new Rectangle(7, 7);
    @Nonnull
    private final Region node;
    /** Relative origin.
     * <ul>
     * <li>0,0=top left</li>
     * <li>1,0=top right</li>
     * <li>0,1=bottom left</li>
     * <li>1,1=bottom right</li>
     * </ul>
     */
@Nonnull
private Point2D origin=new Point2D(0,0);
    private Point2D pickLocation;
    private final MapAccessor<Point2D> pointKey;
    private final MapAccessor<Point2D> controlPointKey;
    private final String styleclass;

    public RelativeControlPointHandle(Figure figure, MapAccessor<Point2D> pointKey, MapAccessor<Point2D> controlPointKey) {
        this(figure, STYLECLASS_HANDLE_POINT, pointKey, controlPointKey);
    }

    public RelativeControlPointHandle(Figure figure, String styleclass, MapAccessor<Point2D> pointKey,MapAccessor<Point2D> controlPointKey) {
        super(figure);
        this.pointKey = pointKey;
        this.controlPointKey = controlPointKey;
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);

        node.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        Point2D p = getLocationInView();
        return Geom.length2(x, y, p.getX(), p.getY()) <= tolerance;
    }

    @Override
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    @Nonnull
    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void handleMouseDragged(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), newPoint);
        }
        final Point2D localPoint = getOwner().worldToLocal(newPoint);

        Point2D point = getOwner().get(pointKey);
        Point2D controlPoint = localPoint.subtract(point);
        
        view.getModel().set(getOwner(), controlPointKey, controlPoint);
    }

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
    }

    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Point2D point = f.get(pointKey);
        Point2D controlPoint = f.get(controlPointKey);
        Point2D p = point.add(controlPoint);
        pickLocation = t == null ? p : t.transform(p);
        node.relocate(pickLocation.getX() - 5, pickLocation.getY() - 5);
        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }

}
