/* @(#)FigurePropertyChangeEvent.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.collection.Key;
import org.jhotdraw8.event.Event;

/**
 * FigurePropertyChangeEvent.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigurePropertyChangeEvent extends Event<Figure> {

    private final static long serialVersionUID = 1L;
    private final Key<?> key;
    private final Object oldValue;
    private final Object newValue;

    public enum EventType {
        WILL_CHANGE,
        CHANGED
    }
    private final EventType type;

    public <T> FigurePropertyChangeEvent(Figure source, EventType type, Key<T> key, T oldValue, T newValue) {
        super(source);
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.type = type;
    }

    public Key<?> getKey() {
        return key;
    }

    public <T> T getOldValue() {
        return (T) oldValue;
    }

    public <T> T getNewValue() {
        return (T)newValue;
    }

    public EventType getType() {
        return type;
    }
}
