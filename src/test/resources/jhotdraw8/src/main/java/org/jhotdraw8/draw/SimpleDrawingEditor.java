/* @(#)SimpleDrawingEditor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import java.util.HashSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.draw.tool.ToolEvent;
import org.jhotdraw8.event.Listener;

/**
 * SimpleDrawingEditor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawingEditor implements DrawingEditor {

    private final ChangeListener<Boolean> focusListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (newValue) {
            setActiveDrawingView((DrawingView) ((ReadOnlyProperty) observable).getBean());
        }
    };

    private final SetProperty<DrawingView> drawingViews = new SimpleSetProperty<>(this, DRAWING_VIEWS_PROPERTY, FXCollections.observableSet(new HashSet<>()));

    {
        drawingViews.addListener((SetChangeListener.Change<? extends DrawingView> change) -> {
            if (change.wasRemoved()) {
                change.getElementRemoved().focusedProperty().removeListener(focusListener);
                if (getActiveDrawingView() != null) {
                    if (getActiveDrawingView() == change.getElementRemoved()) {
                        setActiveDrawingView(drawingViews.isEmpty() ? null : drawingViews.get().iterator().next());
                    }
                }
            } else if (change.wasAdded()) {
                change.getElementAdded().focusedProperty().addListener(focusListener);
                if (drawingViews.size() == 1) {
                    setActiveDrawingView(change.getElementAdded());
                }
            }

        });
    }

    @Nullable
    private final Listener<ToolEvent> defaultToolActivator = (event) -> {
        switch (event.getEventType()) {
            case TOOL_DONE:
                if (getDefaultTool() != event.getSource() && getDefaultTool() != null) {
                    setActiveTool(getDefaultTool());
                }
                break;
            default:
                break;
        }
    };

    private final ObjectProperty<DrawingView> activeDrawingView = new SimpleObjectProperty<>(this, ACTIVE_DRAWING_VIEW_PROPERTY);
    private final ObjectProperty<Tool> activeTool = new SimpleObjectProperty<>(this, ACTIVE_TOOL_PROPERTY);

    {
        activeTool.addListener((o, oldValue, newValue) -> {

            if (getActiveDrawingView() != null) {
                getActiveDrawingView().setTool(newValue);
            }
            if (oldValue != null) {
                oldValue.deactivate(this);
            }
            if (newValue != null) {
                newValue.setDrawingEditor(this);
                newValue.activate(this);
            }
        });
    }

    {
        activeTool.addListener((o, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeToolListener(defaultToolActivator);
            }
            if (newValue != null) {
                newValue.addToolListener(defaultToolActivator);
            }
        });
    }

    private final ObjectProperty<Tool> defaultTool = new SimpleObjectProperty<Tool>(this, DEFAULT_TOOL_PROPERTY) {

    };

    @Nonnull
    @Override
    public SetProperty<DrawingView> drawingViewsProperty() {
        return drawingViews;
    }

    @Nonnull
    @Override
    public ObjectProperty<DrawingView> activeDrawingViewProperty() {
        return activeDrawingView;
    }

    @Nonnull
    @Override
    public ObjectProperty<Tool> activeToolProperty() {
        return activeTool;
    }

    @Nonnull
    @Override
    public ObjectProperty<Tool> defaultToolProperty() {
        return defaultTool;
    }
}
