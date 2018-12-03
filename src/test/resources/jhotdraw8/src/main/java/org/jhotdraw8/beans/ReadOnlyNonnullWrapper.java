/* @(#)ReadOnlyNonnullWrapper.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * ReadOnlyNonnullWrapper.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ReadOnlyNonnullWrapper<T> extends ReadOnlyObjectWrapper<T> {

    public ReadOnlyNonnullWrapper(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
    }

    @Override
    protected void fireValueChangedEvent() {
        if (get() == null) {
            throw new NullPointerException("newValue is null");
        }
        super.fireValueChangedEvent();
    }

}
