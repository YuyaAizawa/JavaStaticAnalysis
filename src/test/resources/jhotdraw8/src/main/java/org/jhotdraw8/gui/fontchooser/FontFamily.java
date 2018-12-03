/* @(#)FontFamily.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.fontchooser;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.Nonnull;

/**
 * FontFamily.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FontFamily {
  private final StringProperty name = new SimpleStringProperty();

    private final ObservableList<FontTypeface> typefaces=FXCollections.observableArrayList();

    @Nonnull
    public ObservableList<FontTypeface> getTypefaces() {
        return typefaces;
    }
    
    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    @Nonnull
    public StringProperty nameProperty() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
}
