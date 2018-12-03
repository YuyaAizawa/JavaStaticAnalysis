/* @(#)CutEndPathBuilder.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 * CutEndPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CutEndPathBuilder extends AbstractPathBuilder {

    private PathBuilder out;
    private final double radius;
    private double cx;
    private double cy;

    private Path2D.Double path;

    public CutEndPathBuilder(PathBuilder out, double radius) {
        this.out = out;
        this.radius = radius;
        path = new Path2D.Double();
    }

    @Override
    protected void doClosePath() {
        path.closePath();
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        path.curveTo(x1, y1, x2, y2, x3, y3);
    }

    @Override
    protected void doPathDone() {
        if (path != null) {
            Point2D currentPoint = path.getCurrentPoint();
            cx = currentPoint.getX();
            cy = currentPoint.getY();
            double[] seg = new double[6];
            double x = 0, y = 0;
            Loop:
            for (PathIterator i = path.getPathIterator(null); !i.isDone(); i.next()) {
                switch (i.currentSegment(seg)) {
                    case PathIterator.SEG_CLOSE:
                        out.closePath();
                        break;
                    case PathIterator.SEG_CUBICTO: {
                        Intersection isect = Intersections.intersectCubicCurveCircle(x, y, seg[0], seg[1], seg[2], seg[3], seg[4], seg[5], cx, cy, radius);
                        if (isect.getStatus() == Intersection.Status.NO_INTERSECTION_INSIDE) {
                            // break Loop;
                        } else if (isect.isEmpty()) {
                            out.curveTo(seg[0], seg[1], seg[2], seg[3], seg[4], seg[5]);
                        } else {
                            Beziers.splitCubicCurve(x, y, seg[0], seg[1], seg[2], seg[3], seg[4], seg[5], isect.getLastT(),
                                    out::curveTo, null);
                            //  break Loop;
                        }
                        x = seg[4];
                        y = seg[5];
                        break;
                    }
                    case PathIterator.SEG_LINETO: {
                        Intersection isect = Intersections.intersectLineCircle(x, y, seg[0], seg[1], cx, cy, radius);
                        if (isect.getStatus() == Intersection.Status.NO_INTERSECTION_INSIDE) {
                            //         break Loop;
                        } else if (isect.isEmpty()) {
                            out.lineTo(seg[0], seg[1]);
                        } else {
                            Geom.splitLine(x, y, seg[0], seg[1], isect.getLastT(),
                                    out::lineTo, null);
                            //   break Loop;
                        }
                        x = seg[0];
                        y = seg[1];
                        break;
                    }
                    case PathIterator.SEG_MOVETO: {
                        Intersection isect = Intersections.intersectLineCircle(x, y, seg[0], seg[1], cx, cy, radius);
                        if (isect.getStatus() == Intersection.Status.NO_INTERSECTION_INSIDE) {
                            //            break Loop;
                        } else if (isect.isEmpty()) {
                            out.moveTo(seg[0], seg[1]);
                        } else {
                            Geom.splitLine(x, y, seg[0], seg[1], isect.getLastT(),
                                    out::moveTo, null);
                            //   break Loop;
                        }
                        x = seg[0];
                        y = seg[1];
                        break;
                    }
                    case PathIterator.SEG_QUADTO: {
                        Intersection isect = Intersections.intersectQuadraticCurveCircle(x, y, seg[0], seg[1], seg[2], seg[3], cx, cy, radius);
                        if (isect.getStatus() == Intersection.Status.NO_INTERSECTION_INSIDE) {
                            //               break Loop;
                        } else if (isect.isEmpty()) {
                            out.quadTo(seg[0], seg[1], seg[2], seg[3]);
                        } else {
                            Beziers.splitQuadCurve(x, y, seg[0], seg[1], seg[2], seg[3], isect.getLastT(),
                                    out::quadTo, null);
                            //   break Loop;
                        }
                        x = seg[2];
                        y = seg[3];
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("illegal path command:" + i.currentSegment(seg));
                }
            }

            path = null;
        }
        out.pathDone();
    }

    @Override
    protected void doLineTo(double x, double y) {
        path.lineTo(x, y);
    }

    @Override
    protected void doMoveTo(double x, double y) {
        path.moveTo(x, y);
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        path.quadTo(x1, y1, x2, y2);
    }

}
