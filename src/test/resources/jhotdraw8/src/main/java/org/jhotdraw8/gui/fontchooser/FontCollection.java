/* @(#)FontCollection.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.fontchooser;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.annotation.Nonnull;

/**
 * FontCollection.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FontCollection {

    private final StringProperty name = new SimpleStringProperty();

    private final ObservableList<FontFamily> families=FXCollections.observableArrayList();
    private final BooleanProperty smartCollection = new SimpleBooleanProperty();

    public FontCollection() {
    }
    public FontCollection(String name, @Nonnull List<FontFamily> families) {
        this(name,false,families);
    }
    public FontCollection(String name, boolean isSmart, @Nonnull List<FontFamily> families) {
        setName(name);
        setSmartCollection(isSmart);
        this.families.addAll(families);
    }

    @Nonnull
    public ObservableList<FontFamily> getFamilies() {
        return families;
    }
    
    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public boolean isSmartCollection() {
        return smartCollection.get();
    }

    public void setSmartCollection(boolean value) {
        smartCollection.set(value);
    }

    @Nonnull
    public StringProperty nameProperty() {
        return name;
    }

    @Nonnull
    public BooleanProperty smartCollectionProperty() {
        return smartCollection;
    }

    @Override
    public String toString() {
        return getName();
    }

}
