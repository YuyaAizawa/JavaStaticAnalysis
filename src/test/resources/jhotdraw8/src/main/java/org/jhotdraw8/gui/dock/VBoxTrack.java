/* @(#)VBoxTrack.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.dock;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javax.annotation.Nonnull;

/**
 * VBoxTrack.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class VBoxTrack extends VBox implements Track {

    public VBoxTrack() {
getItems().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> c) {
                while (c.next()) {
                    for (Node remitem : c.getRemoved()) {
                        if (remitem instanceof Dock) {
                            Dock d=(Dock)remitem;
                            d.setTrack(null);
                        }
                    }
                    for (Node additem : c.getAddedSubList()) {
                        if (additem instanceof Dock) {
                            Dock d=(Dock)additem;
                            d.setTrack(VBoxTrack.this);
                        }
                    }
                }

                //updateResizableWithParent();
            }

        });
    }

    @Override
    public ObservableList<Node> getItems() {
        return getChildren();
    }

    @Nonnull
    @Override
    public Orientation getOrientation() {
        return Orientation.VERTICAL;
    }

}
