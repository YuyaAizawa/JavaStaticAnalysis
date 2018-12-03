/* @(#)EntityAttributesInspector.java
 * Copyright (c) systransis Ltd.

 */
package org.jhotdraw8.draw.inspector;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import org.jhotdraw8.css.text.CssIdentConverter;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.gui.PlatformUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * The help inspector displays the current help text of the drawing view.
 *
 * @author werni
 */
public class HelpTextInspector implements Inspector {

    @FXML
    private TextArea textArea;

    private Node node;

    public HelpTextInspector() {
        this(HelpTextInspector.class.getResource("HelpTextInspector.fxml"));
    }

    public HelpTextInspector(URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void init(URL fxmlUrl) {
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene. 
        PlatformUtil.invokeAndWait(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(Labels.getBundle());
            loader.setController(this);

            try ( InputStream in = fxmlUrl.openStream()) {
                node = loader.load(in);
            } catch (IOException ex) {
                throw new InternalError(ex);
            }
        });
    }

    private DrawingView view;

    @Override
    public void setDrawingView(@Nullable DrawingView newValue) {
        if (view != null) {
            textArea.textProperty().unbind();
        }
        view = newValue;
        if (view != null) {
            textArea.textProperty().bind(view.helpTextProperty());
        }
    }

    @Override
    public Node getNode() {
        return node;
    }

}
