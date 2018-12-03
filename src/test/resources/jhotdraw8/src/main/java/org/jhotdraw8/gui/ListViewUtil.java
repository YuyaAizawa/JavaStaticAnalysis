/* @(#)ListViewUtil.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

/**
 * ListViewUtil.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ListViewUtil {

    private static class DnDSupport<T> {

        private final ListView<T> listView;
        private int draggedCellIndex = -1;
        private final ClipboardIO<T> io;
        private boolean reorderingOnly;

        public DnDSupport(ListView<T> listView, ClipboardIO<T> io, boolean reorderingOnly) {
            this.listView = listView;
            this.io = io;
            this.reorderingOnly = reorderingOnly;
        }

        @Nonnull
        private EventHandler<? super DragEvent> cellDragHandler = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.isConsumed()) {
                    return;
                }
                EventType<DragEvent> t = event.getEventType();
                if (t == DragEvent.DRAG_DONE) {
                    onDragDone(event);
                }
            }

            private void onDragDone(@Nonnull DragEvent event) {
                if (reorderingOnly) {
                    // XXX assumes that the ListView autodetects reordering!
                    draggedCellIndex = -1;
                    event.consume();
                    return;
                }

                ListCell<?> cell = (ListCell<?>) event.getSource();
                if (event.getAcceptedTransferMode() == TransferMode.MOVE) {
                    listView.getItems().remove(draggedCellIndex);
                }
                event.consume();
            }

        };

        @Nonnull
        private EventHandler<? super MouseEvent> cellMouseHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.isConsumed()) {
                    return;
                }
                if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    @SuppressWarnings("unchecked")
                    ListCell<T> draggedCell = (ListCell<T>) event.getSource();
                    draggedCellIndex = draggedCell.getIndex();
                    // XXX we currently only support single selection!!
                    if (!listView.getSelectionModel().isSelected(draggedCell.getIndex())) {
                        return;
                    }

                    Dragboard dragboard = draggedCell.startDragAndDrop(reorderingOnly ? new TransferMode[]{TransferMode.MOVE} : TransferMode.COPY_OR_MOVE);
                    ArrayList<T> items = new ArrayList<>();
                    items.add(draggedCell.getItem());
                    io.write(dragboard, items);
                    dragboard.setDragView(draggedCell.snapshot(new SnapshotParameters(), null));
                    event.consume();
                }
            }

        };

        @javax.annotation.Nullable
        EventHandler<? super DragEvent> listDragHandler = new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.isConsumed()) {
                    return;
                }
                EventType<DragEvent> t = event.getEventType();
                if (t == DragEvent.DRAG_DROPPED) {
                    onDragDropped(event);
                } else if (t == DragEvent.DRAG_OVER) {
                    onDragOver(event);
                }
            }

            private TransferMode[] acceptModes(DragEvent event) {
                ListView<?> gestureTargetListView = null;
                if (event.getGestureSource() instanceof ListCell) {
                    ListCell<?> gestureTargetCell = (ListCell<?>) event.getGestureSource();
                    gestureTargetListView = gestureTargetCell.getListView();
                }
                TransferMode[] mode;
                if (reorderingOnly) {
                    mode = (listView == gestureTargetListView) ? new TransferMode[]{TransferMode.MOVE} : TransferMode.NONE;
                } else {
                    mode = (listView == gestureTargetListView) ? new TransferMode[]{TransferMode.MOVE} : new TransferMode[]{TransferMode.COPY};
                }

                return mode;
            }

            private void onDragDropped(DragEvent event) {
                boolean isAcceptable = io.canRead(event.getDragboard());
                if (isAcceptable) {
                    boolean success = false;
                    TransferMode[] mode = acceptModes(event);
                    if (mode.length == 0) {
                        return;
                    }
                    event.acceptTransferModes(mode);

                    // XXX foolishly assumes fixed cell height
                    double cellHeight = listView.getFixedCellSize();
                    int index = Math.max(0, Math.min((int) (event.getY() / cellHeight), listView.getItems().size()));

                    if (reorderingOnly) {
                        // FIXME only supports single item drag
                        T item = listView.getItems().get(draggedCellIndex);
                        listView.getItems().add(index, item);
                        success = true;
                    } else {

                        List<T> items = io.read(event.getDragboard());
                        success = items != null;
                        if (success) {
                            for (T item : items) {
                                listView.getItems().add(index, item);
                                if (index <= draggedCellIndex) {
                                    draggedCellIndex++;
                                }
                                index++;
                            }
                        }
                    }
                    event.setDropCompleted(success);
                    event.consume();
                }
            }

            private void onDragOver(DragEvent event) {
                boolean isAcceptable = io.canRead(event.getDragboard());
                if (isAcceptable && (!reorderingOnly || draggedCellIndex != -1)) {
                    event.acceptTransferModes(acceptModes(event));
                    event.consume();
                }
            }
        };
    }

    /**
     * Adds drag and drop support to the list view
     *
     * @param <T> the data type of the list view
     * @param listView the list view
     * @param clipboardIO a reader/writer for the clipboard.
     */
    public static <T> void addDragAndDropSupport(@Nonnull ListView<T> listView, ClipboardIO<T> clipboardIO) {
        addDragAndDropSupport(listView, listView.getCellFactory(), clipboardIO);
    }

    /**
     * Adds drag and drop support to the list view
     *
     * FIXME should also add support for cut, copy and paste keys
     *
     * @param <T> the data type of the list view
     * @param listView the list view
     * @param cellFactory the cell factory of the list view
     * @param clipboardIO a reader/writer for the clipboard.
     */
    public static <T> void addDragAndDropSupport(@Nonnull ListView<T> listView, @Nonnull Callback<ListView<T>, ListCell<T>> cellFactory, ClipboardIO<T> clipboardIO) {
        addDragAndDropSupport(listView, cellFactory, clipboardIO, false);
    }

    private static <T> void addDragAndDropSupport(@Nonnull ListView<T> listView, @Nonnull Callback<ListView<T>, ListCell<T>> cellFactory, ClipboardIO<T> clipboardIO,
                                                  boolean reorderingOnly) {
        DnDSupport<T> dndSupport = new DnDSupport<>(listView, clipboardIO, reorderingOnly);
        Callback<ListView<T>, ListCell<T>> dndCellFactory = lv -> {
            ListCell<T> cell = cellFactory.call(lv);
            cell.addEventHandler(DragEvent.DRAG_DONE, dndSupport.cellDragHandler);
            cell.addEventHandler(MouseEvent.DRAG_DETECTED, dndSupport.cellMouseHandler);
            return cell;
        };
        listView.setCellFactory(dndCellFactory);
        listView.addEventHandler(DragEvent.ANY, dndSupport.listDragHandler);
    }

    /**
     * Adds reordering support to the list view.
     *
     * @param <T> the data type of the list view
     * @param listView the list view
     */
    public static <T> void addReorderingSupport(@Nonnull ListView<T> listView) {
        addReorderingSupport(listView, listView.getCellFactory(), null);
    }
    /**
     * Adds reordering support to the list view.
     *
     * @param <T> the data type of the list view
     * @param listView the list view
     * @param clipboardIO the clipboard i/o
     */
    public static <T> void addReorderingSupport(@Nonnull ListView<T> listView, ClipboardIO<T> clipboardIO) {
        addReorderingSupport(listView, listView.getCellFactory(), clipboardIO);
    }

    /**
     * Adds reordering support to the list view.
     * <p>
     * FIXME should also add support for cut, copy and paste keys.
     * <p>
     * FIXME only supports lists with single item selection (no multiple item selection yet!).
     *
     * @param <T> the data type of the list view
     * @param listView the list view
     * @param cellFactory the cell factory of the list view
     * @param clipboardIO a reader/writer for the clipboard. You can provide null if you don't want cut/copy/paste functionality.
     */
    public static <T> void addReorderingSupport(@Nonnull ListView<T> listView, @Nonnull Callback<ListView<T>, ListCell<T>> cellFactory, @Nullable ClipboardIO<T> clipboardIO) {
        if (clipboardIO==null) {
                clipboardIO = new ClipboardIO<T>() {
                @Override
                public void write(@Nonnull Clipboard clipboard, @Nonnull List<T> items) {
        // We just write the index of the selected item in the clipboard.
                    if (items.size() != 1) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                   ClipboardContent content = new ClipboardContent();
                     content.putString(""+listView.getSelectionModel().getSelectedIndex());
                    clipboard.setContent(content);
                }

                @Nonnull
                @Override
                public List<T> read(Clipboard clipboard) {
                    // We are not actually interested in the clipboard content.
                    return Collections.emptyList();
                }

                @Override
                public boolean canRead(@Nonnull Clipboard clipboard) {
                    return clipboard.hasString();
                }
            };
        }
        addDragAndDropSupport(listView, cellFactory, clipboardIO, true);
    }
    

}
