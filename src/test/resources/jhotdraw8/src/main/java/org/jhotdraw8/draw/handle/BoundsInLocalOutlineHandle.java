/* @(#)BoundsInLocalOutlineHandle.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.Transforms;

/**
 * Draws the {@code boundsInLocal} of a {@code Figure}, but does not provide any
 * interactions.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BoundsInLocalOutlineHandle extends AbstractHandle {

    private Polygon node;
    private double[] points;
    private String styleclass;

    public BoundsInLocalOutlineHandle(Figure figure) {
        this(figure, STYLECLASS_HANDLE_SELECT_OUTLINE);
    }

    public BoundsInLocalOutlineHandle(Figure figure, String styleclass) {
        super(figure);

        points = new double[8];
        node = new Polygon(points);
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

    protected void initNode(@Nonnull Polygon r) {
        r.setFill(null);
        r.setStroke(Color.BLUE);
        r.getStyleClass().setAll(styleclass, STYLECLASS_HANDLE);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        t = Transforms.concat(Transform.translate(0.5, 0.5), t);
        Bounds b = f.getBoundsInLocal();
        points[0] = b.getMinX();
        points[1] = b.getMinY();
        points[2] = b.getMaxX();
        points[3] = b.getMinY();
        points[4] = b.getMaxX();
        points[5] = b.getMaxY();
        points[6] = b.getMinX();
        points[7] = b.getMaxY();
        if (t != null && t.isType2D()) {
            t.transform2DPoints(points, 0, points, 0, 4);
        }

        ObservableList<Double> pp = node.getPoints();
        for (int i = 0; i < points.length; i++) {
            pp.set(i, points[i]);
        }
    }

}
