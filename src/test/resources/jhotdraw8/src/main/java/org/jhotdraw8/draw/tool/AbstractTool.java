/* @(#)AbstractAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import java.util.LinkedList;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.app.AbstractDisableable;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.util.Resources;

/**
 * AbstractAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractTool extends AbstractDisableable implements Tool {

    // ---
    // Fields
    // ---
    /**
     * The getProperties.
     */
    private ObservableMap<Key<?>, Object> properties;
    /**
     * The active view.
     */
    private final ObjectProperty<DrawingView> drawingView = new SimpleObjectProperty<>(this, DRAWING_VIEW_PROPERTY);
    /**
     * The active editor.
     */
    private final ObjectProperty<DrawingEditor> drawingEditor = new SimpleObjectProperty<>(this, DRAWING_EDITOR_PROPERTY);

    {
        drawingView.addListener((ObservableValue<? extends DrawingView> observable, DrawingView oldValue, DrawingView newValue) -> {
            stopEditing();
        });
    }


    private class EventPane extends BorderPane implements EditableComponent {

        public EventPane() {
            setId("toolEventPane");
        }

        private EditableComponent getEditableParent() {

            DrawingView dv = getDrawingView();
            if (dv != null) {
                if (dv.getNode() instanceof EditableComponent) {
                    return (EditableComponent) dv.getNode();
                }
            }
            return null;
        }

        @Override
        public void selectAll() {
            EditableComponent p = getEditableParent();
            if (p != null) {
                p.selectAll();
            }
        }

        @Override
        public void clearSelection() {
            EditableComponent p = getEditableParent();
            if (p != null) {
                p.clearSelection();
            }
        }

        @Nullable
        @Override
        public ReadOnlyBooleanProperty selectionEmptyProperty() {
            EditableComponent p = getEditableParent();
            if (p != null) {
                return p.selectionEmptyProperty();
            }
            return null;
        }

        @Override
        public void deleteSelection() {
            EditableComponent p = getEditableParent();
            if (p != null) {
                p.deleteSelection();
            }
        }

        @Override
        public void duplicateSelection() {
            EditableComponent p = getEditableParent();
            if (p != null) {
                p.duplicateSelection();
            }
        }

        @Override
        public void cut() {
            EditableComponent p = getEditableParent();
            if (p != null) {
                p.cut();
            }
        }

        @Override
        public void copy() {
            EditableComponent p = getEditableParent();
            if (p != null) {
                p.copy();
            }
        }

        @Override
        public void paste() {
            EditableComponent p = getEditableParent();
            if (p != null) {
                p.paste();
            }
        }

    }

    protected final BorderPane eventPane = new EventPane();
    protected final BorderPane drawPane = new BorderPane();
    protected final StackPane node = new StackPane();


    {
        eventPane.addEventHandler(MouseEvent.ANY, (MouseEvent event) -> {
            try {
               DrawingView dv = drawingView.get();
            if (dv!= null) {
                    EventType<? extends MouseEvent> type = event.getEventType();
                    if (type == MouseEvent.MOUSE_MOVED) {
                        handleMouseMoved(event, dv);
                    } else if (type == MouseEvent.MOUSE_DRAGGED) {
                        handleMouseDragged(event, dv);
                    } else if (type == MouseEvent.MOUSE_EXITED) {
                        handleMouseExited(event, dv);
                    } else if (type == MouseEvent.MOUSE_ENTERED) {
                        handleMouseEntered(event, dv);
                    } else if (type == MouseEvent.MOUSE_RELEASED) {
                        handleMouseReleased(event, dv);
                    } else if (type == MouseEvent.MOUSE_PRESSED) {
                        handleMousePressed(event, dv);
                    } else if (type == MouseEvent.MOUSE_CLICKED) {
                        handleMouseClicked(event, dv);
                    }
                    event.consume();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
        eventPane.addEventHandler(KeyEvent.ANY, (KeyEvent event) -> {
           DrawingView dv = drawingView.get();
            if (dv!= null) {
                EventType<? extends KeyEvent> type = event.getEventType();
                if (type == KeyEvent.KEY_PRESSED) {
                    handleKeyPressed(event, dv);
                } else if (type == KeyEvent.KEY_RELEASED) {
                    handleKeyReleased(event, dv);
                } else if (type == KeyEvent.KEY_TYPED) {
                    handleKeyTyped(event, dv);
                }
                event.consume();
            }
        });
        eventPane.addEventHandler(ZoomEvent.ANY, (ZoomEvent event)->{DrawingView dv = drawingView.get();
            if (dv!= null) {
                EventType<? extends ZoomEvent> type = event.getEventType();
                if (type == ZoomEvent.ZOOM) {
                    handleZoom(event, dv);
                } else if (type == ZoomEvent.ZOOM_STARTED) {
                    handleZoomStarted(event, dv);
                } else if (type == ZoomEvent.ZOOM_FINISHED) {
                    handleZoomFinished(event, dv);
                }
                event.consume();
            
            }
            
        });
        
    }
    /**
     * Listeners.
     */
    private final LinkedList<Listener<ToolEvent>> toolListeners = new LinkedList<>();

    // ---
    // Constructors
    // ---
    /**
     * Creates a new instance.
     */
    public AbstractTool() {
        this(null, null);

    }

    /**
     * Creates a new instance.
     *
     * @param name the id of the tool
     * @param rsrc iff nonnull, the resource is applied to the tool
     */
    public AbstractTool(String name, @Nullable Resources rsrc) {
        set(NAME, name);
        if (rsrc != null) {
            applyResources(rsrc);
        }

        node.getChildren().addAll(drawPane, eventPane);
    }

    // ---
    // Properties
    // ---
    @Override
    public final ObservableMap<Key<?>, Object> getProperties() {
        if (properties == null) {
            properties =FXCollections.observableHashMap();
        }
        return properties;
    }

    @Nonnull
    @Override
    public ObjectProperty<DrawingView> drawingViewProperty() {
        return drawingView;
    }

    @Nonnull
    @Override
    public ObjectProperty<DrawingEditor> drawingEditorProperty() {
        return drawingEditor;
    }

    // ---
    // Behaviors
    // ---
    protected void applyResources(@Nonnull Resources rsrc) {
        String name = get(NAME);
        set(LABEL, rsrc.getTextProperty(name));
        set(LARGE_ICON_KEY, rsrc.getLargeIconProperty(name, getClass()));
        set(SHORT_DESCRIPTION, rsrc.getToolTipTextProperty(name));
    }

    @Nonnull
    @Override
    public Node getNode() {
        return node;
    }

    protected void stopEditing() {
    }

    /**
     * Deletes the selection. Depending on the tool, this could be selected
     * figures, selected points or selected text.
     */
    @Override
    public void editDelete() {
        if (getDrawingView() != null) {
            DrawingView v = getDrawingView();
            v.getDrawing().getChildren().removeAll(v.getSelectedFigures());
        }
    }

    /**
     * Cuts the selection into the clipboard. Depending on the tool, this could
     * be selected figures, selected points or selected text.
     */
    @Override
    public void editCut() {
    }

    /**
     * Copies the selection into the clipboard. Depending on the tool, this
     * could be selected figures, selected points or selected text.
     */
    @Override
    public void editCopy() {
    }

    /**
     * Duplicates the selection. Depending on the tool, this could be selected
     * figures, selected points or selected text.
     */
    @Override
    public void editDuplicate() {
    }

    /**
     * Pastes the contents of the clipboard. Depending on the tool, this could
     * be selected figures, selected points or selected text.
     */
    @Override
    public void editPaste() {
    }

    // ---
    // Event handlers
    // ----
    protected void handleMouseMoved(MouseEvent event, DrawingView view) {
    }

    protected void handleMouseDragged(MouseEvent event, DrawingView view) {
    }

    protected void handleMouseExited(MouseEvent event, DrawingView view) {
    }

    protected void handleMouseEntered(MouseEvent event, DrawingView view) {
    }

    protected void handleMouseReleased(MouseEvent event, DrawingView view) {
    }

    protected void handleMousePressed(MouseEvent event, DrawingView view) {
    }

    protected void handleMouseClicked(MouseEvent event, DrawingView view) {
    }

    protected void handleKeyPressed(@Nonnull KeyEvent event, DrawingView view) {
        if (event.getCode()==KeyCode.ESCAPE) {
            fireToolDone();
        }else if (event.getCode()==KeyCode.ENTER) {
            stopEditing();}
    }

    protected void handleKeyReleased(KeyEvent event, DrawingView view) {
    }

    protected void handleKeyTyped(KeyEvent event, DrawingView view) {
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void activate( DrawingEditor editor) {
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void deactivate( DrawingEditor editor) {
    }

    // ---
    // Listeners
    // ---
    @Override
    public void addToolListener(Listener<ToolEvent> listener) {
        toolListeners.add(listener);
    }

    @Override
    public void removeToolListener(Listener<ToolEvent> listener) {
        toolListeners.remove(listener);
    }

    protected void fire(ToolEvent event) {
        for (Listener<ToolEvent> l : toolListeners) {
            l.handle(event);
        }
    }

    protected void handleZoom(ZoomEvent event, DrawingView dv) {
    }
    protected void handleZoomStarted(ZoomEvent event, DrawingView dv) {
    }
    protected void handleZoomFinished(ZoomEvent event, DrawingView dv) {
    }
    protected void fireToolStarted() {
        fire(new ToolEvent(this, ToolEvent.EventType.TOOL_STARTED));
    }

    protected void fireToolDone() {
        fire(new ToolEvent(this, ToolEvent.EventType.TOOL_DONE));
    }

    protected void requestFocus() {
        Platform.runLater(eventPane::requestFocus);
    }
}
