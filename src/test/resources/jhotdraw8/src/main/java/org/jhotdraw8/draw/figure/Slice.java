/* @(#)Slice.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javax.annotation.Nullable;

/**
 * Defines a slice of a drawing.
 * <p>
 * The parent of a slice must be a {@link Layer} or a {@link Clipping} .
 * <p>
 * A slice may not have children.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Slice extends Figure {

    @Override
    default boolean isSuitableParent(@Nullable Figure newParent) {
        return newParent == null || (newParent instanceof Layer) || (newParent instanceof Clipping);
    }

    @Override
    default boolean isAllowsChildren() {
        return false;
    }

    /**
     * For vector graphics output. Specifies where the origin of the coordinate
     * system in the exported slice is. By default, this is the top left corner
     * of the slice.
     *
     * @return origin of coordinate system
     */
        default Point2D getSliceOrigin() {
        final Bounds b = getBoundsInLocal();
        return new Point2D(b.getMinX(), b.getMinY());
    }
}
