/* @(#)SimplePropertyBean.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import java.util.LinkedHashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javax.annotation.Nonnull;
import org.jhotdraw8.collection.Key;

/**
 * SimplePropertyBean is a simple implementation of the {@code PropertyBean}
 * interface.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimplePropertyBean implements PropertyBean {

    /**
     * Holds the properties.
     */
    protected final ObservableMap<Key<?>, Object> properties = FXCollections.observableMap(new LinkedHashMap<>());

    @Nonnull
    @Override
    public final ObservableMap<Key<?>, Object> getProperties() {
        return properties;
    }
}
