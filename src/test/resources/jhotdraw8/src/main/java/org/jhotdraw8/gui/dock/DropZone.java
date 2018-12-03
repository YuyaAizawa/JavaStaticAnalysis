/* @(#)DropZone.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

/**
 * Defines a drop zone for the drag and drop operation of a DockItem.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public enum DropZone {
    /**
     * Denotes the left edge of a Dock.
     */
    TOP,
    /**
     * Denotes the left edge of a Dock.
     */
    LEFT,
    /**
     * Denotes the right edge of a Dock.
     */
    RIGHT,
    /**
     * Denotes the bottom edge of a Dock.
     */
    BOTTOM,
    /**
     * Denotes the center of a Dock.
     */
    CENTER
}
