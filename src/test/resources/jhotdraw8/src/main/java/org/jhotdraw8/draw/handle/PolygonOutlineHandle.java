/* @(#)BoundsInLocalHandle.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.SimplePolylineFigure;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Intersection;
import org.jhotdraw8.geom.Intersections;
import org.jhotdraw8.geom.Transforms;

/**
 * Draws the {@code wireframe} ofCollection a {@code PolygonFigure}.
 * <p>
 * The user can insert a new point by double clicking the line.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PolygonOutlineHandle extends AbstractHandle {

    private boolean editable;
    private final MapAccessor<ImmutableList<Point2D>> key;

    private Polygon node;
    private String styleclass;

    public PolygonOutlineHandle(Figure figure, MapAccessor<ImmutableList<Point2D>> key) {
        this(figure, key, true, STYLECLASS_HANDLE_MOVE_OUTLINE);
    }

    public PolygonOutlineHandle(Figure figure, MapAccessor<ImmutableList<Point2D>> key, boolean editable, String styleclass) {
        super(figure);
        this.key = key;
        this.editable = editable;
        node = new Polygon();
        this.styleclass = styleclass;
        initNode(node);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        return false;
    }

    @Nullable
    @Override
    public Cursor getCursor() {
        return null;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void handleMouseClicked(@Nonnull MouseEvent event, @Nonnull DrawingView dv) {

        if (editable && key != null && event.getClickCount() == 2) {
            ImmutableList<Point2D> points = owner.get(key);

            Point2D pInDrawing = dv.viewToWorld(new Point2D(event.getX(), event.getY()));
            Point2D pInLocal = owner.worldToLocal(pInDrawing);

            double tolerance = Transforms.deltaTransform(owner.getWorldToLocal(), Transforms.deltaTransform(dv.getViewToWorld(), dv.getTolerance(), dv.getTolerance())).getX();
            double px = pInLocal.getX();
            double py = pInLocal.getY();

            int insertAt = -1;
            Point2D insertLocation = null;
            for (int i = 0, n = points.size(); i < n; i++) {
                Point2D p1 = points.get((n + i - 1) % n);
                Point2D p2 = points.get(i);

                Intersection result = Intersections.intersectLineCircle(p1.getX(), p1.getY(), p2.getX(), p2.getY(), px, py, tolerance);
                if (result.getTs().size() == 2) {
                    insertLocation = Geom.lerp(p1, p2, (result.getFirstT() + result.getLastT()) / 2);
                    insertAt = i;
                    break;
                }
            }
            if (insertAt != -1 && insertLocation != null) {
                dv.getModel().set(owner, key, ImmutableList.add(owner.get(key), insertAt, insertLocation));
                dv.recreateHandles();
            }
        }
    }

    protected void initNode(@Nonnull Polygon r) {
        r.setFill(null);
        r.setStroke(Color.BLUE);
        r.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = getOwner().getBoundsInLocal();
        double[] points = SimplePolylineFigure.toPointArray(f, key);
        if (t != null) {
            t.transform2DPoints(points, 0, points, 0, points.length / 2);
        }
        ObservableList<Double> pp = node.getPoints();
        pp.clear();
        for (int i = 0; i < points.length; i++) {
            pp.add(i, points[i]);
        }
    }
}
