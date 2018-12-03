/* @(#)BezierNodePath.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.FillRule;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

/**
 * BezierNodePath.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierNodePath implements Shape {

    private boolean closed;
    private List<BezierNode> nodes;
    private int windingRule;

    public BezierNodePath() {
        this(new ArrayList<>(), false, PathIterator.WIND_EVEN_ODD);
    }

    public BezierNodePath(@Nonnull Iterable<BezierNode> nodes) {
        this(nodes, false, PathIterator.WIND_EVEN_ODD);
    }

    public BezierNodePath(@Nonnull Iterable<BezierNode> nodes, boolean closed, FillRule windingRule) {
        this(nodes, closed, windingRule == FillRule.EVEN_ODD ? PathIterator.WIND_EVEN_ODD : PathIterator.WIND_NON_ZERO);

    }

    public BezierNodePath(@Nonnull Iterable<BezierNode> nodes, boolean closed, int windingRule) {
        this.nodes = new ArrayList<>();
        for (BezierNode n: nodes) this.nodes.add(n);
        this.closed = closed;
        this.windingRule = windingRule;
    }

    @Override
    public boolean contains(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(@Nonnull Point2D p) {
        return contains(p.getX(), p.getY());
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(@Nonnull Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    @Override
    public Rectangle getBounds() {
        return getBounds2D().getBounds();
    }

    @Nonnull
    @Override
    public Rectangle2D getBounds2D() {
        double x1 = Double.POSITIVE_INFINITY, y1 = Double.POSITIVE_INFINITY,
                x2 = Double.NEGATIVE_INFINITY, y2 = Double.NEGATIVE_INFINITY;
        for (BezierNode n : nodes) {
            double y = n.getY0();
            double x = n.getX0();
            if (x < x1) {
                x1 = x;
            }
            if (y < y1) {
                y1 = y;
            }
            if (x > x2) {
                x2 = x;
            }
            if (y > y2) {
                y2 = y;
            }
            if (n.isC1()) {
                y = n.getY1();
                x = n.getX1();
                if (x < x1) {
                    x1 = x;
                }
                if (y < y1) {
                    y1 = y;
                }
                if (x > x2) {
                    x2 = x;
                }
                if (y > y2) {
                    y2 = y;
                }
            }
            if (n.isC2()) {
                y = n.getY2();
                x = n.getX2();
                if (x < x1) {
                    x1 = x;
                }
                if (y < y1) {
                    y1 = y;
                }
                if (x > x2) {
                    x2 = x;
                }
                if (y > y2) {
                    y2 = y;
                }
            }
        }
        return new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
    }

    public List<BezierNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<BezierNode> nodes) {
        this.nodes = nodes;
    }

    @Nonnull
    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return new BezierNodePathIterator(nodes, closed, windingRule, at);
    }

    @Nonnull
    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new FlatteningPathIterator(getPathIterator(at), flatness);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean intersects(@Nonnull Rectangle2D r) {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public boolean pathIntersects(double x, double y, double tolerance) {
        Intersection isect = Intersections.intersectPathIteratorPoint(getPathIterator(null), x, y, tolerance);
        return !isect.isEmpty();
    }

    public boolean split(double x, double y, double tolerance) {
        Intersection isect = Intersections.intersectPathIteratorPoint(getPathIterator(null), x, y, tolerance);
        if (isect.size() == 1) {
            int segment = (int) isect.getFirstT();
            final BezierNode middle;
            javafx.geometry.Point2D p = isect.getPoints().get(0);
            final int prevSegment = (segment - 1 + nodes.size()) % nodes.size();
            BezierNode prev = nodes.get(prevSegment);
            BezierNode next = nodes.get(segment);
            double t = isect.getFirstT() - segment;
            boolean pc2 = prev.isC2();
            boolean nc1 = next.isC1();
            if (pc2) {
                if (nc1) {
                    // cubic curve
                    middle = new BezierNode(BezierNode.C1C2_MASK, true, true, p.getX(), p.getY(), p.getX(), p.getY(), p.getX(), p.getY());
                    nodes.add(segment, middle);
                    Beziers.splitCubicCurve(prev.getX0(), prev.getY0(), prev.getX2(), prev.getY2(),
                            next.getX1(), next.getY1(), next.getX0(), next.getY0(), t,
                            (x1, y1, x2, y2, x3, y3) -> {
                                nodes.set(prevSegment, prev.setX2(x1).setY2(y1));
                                nodes.set(segment, nodes.get(segment).setX1(x2).setY1(y2));
                            },
                            (x1, y1, x2, y2, x3, y3) -> {
                                nodes.set(segment, nodes.get(segment).setX2(x1).setY2(y1));
                                nodes.set(segment + 1, next.setX1(x2).setY1(y2));
                            }
                    );
                } else {
                    // quadratic curve controlled by prev
                    middle = new BezierNode(BezierNode.C2_MASK, true, true, p.getX(), p.getY(), p.getX(), p.getY(), p.getX(), p.getY());
                    prev.setColinear(true);
                    nodes.add(segment, middle);
                    Beziers.splitQuadCurve(prev.getX0(), prev.getY0(),
                            next.getX1(), next.getY1(), next.getX0(), next.getY0(), t,
                            (x1, y1, x2, y2) -> {
                                nodes.set(prevSegment, middle.setX2(x1).setY2(y1));
                                nodes.set(segment, nodes.get(segment).setX0(x2).setY0(y2));
                            },
                            (x1, y1, x2, y2) -> {
                                nodes.set(segment, nodes.get(segment).setX2(x1).setY2(y1));
                            }
                    );
                }
            } else if (nc1) {
                // quadratic curve controlled by next
                middle = new BezierNode(BezierNode.C1_MASK, true, true, p.getX(), p.getY(), p.getX(), p.getY(), p.getX(), p.getY());
                nodes.add(segment, middle);
                Beziers.splitQuadCurve(prev.getX0(), prev.getY0(),
                        next.getX1(), next.getY1(), next.getX0(), next.getY0(), t,
                        (x1, y1, x2, y2) -> {
                            nodes.set(segment, middle.setX1(x1).setY1(y1).setX0(x2).setY0(y2));
                        },
                        (x1, y1, x2, y2) -> {
                            nodes.set(segment + 1, next.setX1(x1).setY1(y1).setColinear(true));
                        }
                );
            } else {
                // line
                middle = new BezierNode(BezierNode.C0_MASK, true, true, p.getX(), p.getY(), p.getX(), p.getY(), p.getX(), p.getY());
                nodes.add(segment, middle);
            }

            return true;
        }
        return false;
    }

    public void join(int segment, double tolerance) {
        final int prevSegment = (segment - 1 + nodes.size()) % nodes.size();
        final int nextSegment = (segment + 1) % nodes.size();
        BezierNode prev = nodes.get(prevSegment);
        BezierNode middle = nodes.get(segment);
        BezierNode next = nodes.get(nextSegment);
        boolean pc2 = prev.isC2();
        boolean mc2 = middle.isC2();
        boolean mc1 = middle.isC1();
        boolean nc1 = next.isC1();
        if (!pc2 && mc1 && nc1) {
            javafx.geometry.Point2D p = Beziers.mergeQuadCurve(
                    prev.getX0(), prev.getY0(), middle.getX1(), middle.getY1(), middle.getX0(), middle.getY0(),
                    next.getX1(), next.getY1(), next.getX0(), next.getY0(), tolerance);
            if (p != null) {
                nodes.set(nextSegment, next.setX1(p.getX()).setY1(p.getY()));
            }
        } else if (pc2 && mc2 && !nc1) {
            javafx.geometry.Point2D p = Beziers.mergeQuadCurve(
                    prev.getX0(), prev.getY0(), prev.getX2(), prev.getY2(), middle.getX0(), middle.getY0(),
                    middle.getX2(), middle.getY2(), next.getX0(), next.getY0(), tolerance);
            if (p != null) {
                nodes.set(prevSegment, prev.setX2(p.getX()).setY2(p.getY()));
            }
        } else if (pc2&&mc1&&mc2&&nc1) {
            javafx.geometry.Point2D[] p = Beziers.mergeCubicCurve(
                    prev.getX0(), prev.getY0(), prev.getX2(), prev.getY2(), middle.getX1(), middle.getY1(), middle.getX0(), middle.getY0(),
                    middle.getX2(), middle.getY2(), next.getX1(), next.getY1(), next.getX0(), next.getY0(), tolerance);
            if (p != null) {
                javafx.geometry.Point2D p1=p[0];
                javafx.geometry.Point2D p2=p[1];
                nodes.set(prevSegment, prev.setX2(p1.getX()).setY2(p1.getY()));
                nodes.set(nextSegment, next.setX1(p2.getX()).setY1(p2.getY()));
            }
        }
        nodes.remove(segment);

    }
}
