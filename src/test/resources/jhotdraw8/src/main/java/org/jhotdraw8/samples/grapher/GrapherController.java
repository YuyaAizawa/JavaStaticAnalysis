/* @(#)GrapherController.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.grapher;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import java.util.prefs.Preferences;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javax.annotation.Nonnull;
import org.jhotdraw8.app.AbstractDocumentOrientedViewController;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.app.action.view.ToggleBooleanAction;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.FXWorker;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.EditorView;
import org.jhotdraw8.draw.SimpleDrawingEditor;
import org.jhotdraw8.draw.SimpleDrawingView;
import org.jhotdraw8.draw.action.AddToGroupAction;
import org.jhotdraw8.draw.action.BringToFrontAction;
import org.jhotdraw8.draw.action.GroupAction;
import org.jhotdraw8.draw.action.RemoveFromGroupAction;
import org.jhotdraw8.draw.action.RemoveTransformationsAction;
import org.jhotdraw8.draw.action.SelectChildrenAction;
import org.jhotdraw8.draw.action.SelectSameAction;
import org.jhotdraw8.draw.action.SendToBackAction;
import org.jhotdraw8.draw.action.UngroupAction;
import org.jhotdraw8.draw.constrain.GridConstrainer;
import org.jhotdraw8.draw.figure.SimpleBezierFigure;
import org.jhotdraw8.draw.figure.SimpleCombinedPathFigure;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.SimpleEllipseFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.figure.SimpleGroupFigure;
import org.jhotdraw8.draw.figure.SimpleImageFigure;
import org.jhotdraw8.draw.figure.SimpleLabelFigure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleLineConnectionWithMarkersFigure;
import org.jhotdraw8.draw.figure.SimpleLineFigure;
import org.jhotdraw8.draw.figure.SimplePageFigure;
import org.jhotdraw8.draw.figure.SimplePageLabelFigure;
import org.jhotdraw8.draw.figure.SimplePolygonFigure;
import org.jhotdraw8.draw.figure.SimplePolylineFigure;
import org.jhotdraw8.draw.figure.SimpleRectangleFigure;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.figure.SimpleSliceFigure;
import org.jhotdraw8.draw.figure.StrokeableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.input.MultiClipboardInputFormat;
import org.jhotdraw8.draw.input.MultiClipboardOutputFormat;
import org.jhotdraw8.draw.inspector.DrawingInspector;
import org.jhotdraw8.draw.inspector.GridInspector;
import org.jhotdraw8.draw.inspector.HelpTextInspector;
import org.jhotdraw8.draw.inspector.HierarchyInspector;
import org.jhotdraw8.draw.inspector.Inspector;
import org.jhotdraw8.draw.inspector.Labels;
import org.jhotdraw8.draw.inspector.LayersInspector;
import org.jhotdraw8.draw.inspector.StyleAttributesInspector;
import org.jhotdraw8.draw.inspector.StyleClassesInspector;
import org.jhotdraw8.draw.inspector.StylesheetsInspector;
import org.jhotdraw8.draw.inspector.ToolsToolbar;
import org.jhotdraw8.draw.inspector.ZoomToolbar;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.io.DefaultFigureFactory;
import org.jhotdraw8.draw.io.FigureFactory;
import org.jhotdraw8.draw.io.PrinterExportFormat;
import org.jhotdraw8.draw.io.SimpleFigureIdFactory;
import org.jhotdraw8.draw.io.SimpleXmlIO;
import org.jhotdraw8.draw.io.SvgExportOutputFormat;
import org.jhotdraw8.draw.io.XMLEncoderOutputFormat;
import org.jhotdraw8.draw.tool.BezierCreationTool;
import org.jhotdraw8.draw.tool.ConnectionTool;
import org.jhotdraw8.draw.tool.CreationTool;
import org.jhotdraw8.draw.tool.ImageCreationTool;
import org.jhotdraw8.draw.tool.PolyCreationTool;
import org.jhotdraw8.draw.tool.SelectionTool;
import org.jhotdraw8.draw.tool.Tool;
import org.jhotdraw8.gui.dock.Dock;
import org.jhotdraw8.gui.dock.DockItem;
import org.jhotdraw8.gui.dock.DockRoot;
import org.jhotdraw8.gui.dock.ScrollableVBoxTrack;
import org.jhotdraw8.gui.dock.SingleItemDock;
import org.jhotdraw8.gui.dock.SplitPaneTrack;
import org.jhotdraw8.gui.dock.TabbedAccordionDock;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.svg.SvgExporter;
import org.jhotdraw8.css.text.CssSize2D;
import org.jhotdraw8.css.text.CssSizeInsets;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.util.prefs.PreferencesUtil;
import org.jhotdraw8.app.DocumentOrientedViewModel;

/**
 * GrapherController.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GrapherController extends AbstractDocumentOrientedViewController implements DocumentOrientedViewModel, EditorView {

    private final static String GRAPHER_NAMESPACE_URI = "http://jhotdraw.org/samples/grapher";
    private static final String VIEWTOGGLE_PROPERTIES = "view.toggleProperties";
    /**
     * Counter for incrementing layer names.
     */
    @Nonnull
    private Map<String, Integer> counters = new HashMap<>();
    @FXML
    private ScrollPane detailsScrollPane;
    @FXML
    private VBox detailsVBox;
    private final BooleanProperty detailsVisible = new SimpleBooleanProperty(this, "detailsVisible", true);

    private DrawingView drawingView;

    private DrawingEditor editor;
    @FXML
    private BorderPane contentPane;
    private Node node;
    @FXML
    private ToolBar toolsToolBar;
    private DockRoot dockRoot;

    @Nonnull
    private DockItem addInspector(Inspector inspector, String id, Priority grow) {
        Resources r = Labels.getResources();
        DockItem dockItem = new DockItem();
        dockItem.setText(r.getString(id + ".toolbar"));
        dockItem.setContent(inspector.getNode());
        dockItem.getProperties().put("inspector", inspector);
        return dockItem;
    }

    private void addInspectorOLD(Inspector inspector, String id, Priority grow) {
        Resources r = Labels.getResources();

        Accordion a = new Accordion();
        a.getStyleClass().setAll("inspector", "flush");
        Pane n = (Pane) inspector.getNode();
        TitledPane t = new TitledPane(r.getString(id + ".toolbar"), n);
        a.getPanes().add(t);
        a.getProperties().put("inspector", inspector);

        // Make sure that an expanded accordion has the specified grow priority.
        // But when it is collapsed it should have none.
        t.expandedProperty().addListener((o, oldValue, newValue) -> {
            VBox.setVgrow(a, newValue ? grow : Priority.NEVER);
        });

        PreferencesUtil.installBooleanPropertyHandler(Preferences.userNodeForPackage(GrapherController.class), id + ".expanded", t.expandedProperty());
        if (t.isExpanded()) {
            a.setExpandedPane(t);
            VBox.setVgrow(a, grow);
        }
    }

    @Nonnull
    @Override
    public CompletionStage<Void> clear() {
        Drawing d = new SimpleDrawing();
        d.set(StyleableFigure.ID, "drawing1");
        drawingView.setDrawing(d);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Creates a figure with a unique id.
     *
     * @param <T> the figure type
     * @param supplier the supplier
     * @return the created figure
     */
    public <T extends Figure> T createFigure(@Nonnull Supplier<T> supplier) {
        T created = supplier.get();
        String prefix = created.getTypeSelector().toLowerCase();
        Integer counter = counters.get(prefix);
        Set<String> ids = new HashSet<>();
        counter = counter == null ? 1 : counter + 1;
        // XXX O(n) !!!
        for (Figure f : drawingView.getDrawing().preorderIterable()) {
            ids.add(f.getId());
        }
        String id = prefix + counter;
        while (ids.contains(id)) {
            counter++;
            id = prefix + counter;
        }
        counters.put(created.getTypeSelector(), counter);
        created.set(StyleableFigure.ID, id);
        return created;
    }

    @Override
    public DrawingEditor getEditor() {
        return editor;
    }

    @Override
    public Node getNode() {
        return node;
    }

    public Node getPropertiesPane() {
        return detailsScrollPane;
    }

    @Override
    protected void initActionMap(@Nonnull HierarchicalMap<String, Action> map) {
        map.put(RemoveTransformationsAction.ID, new RemoveTransformationsAction(getApplication(), editor));
        map.put(SelectSameAction.ID, new SelectSameAction(getApplication(), editor));
        map.put(SelectChildrenAction.ID, new SelectChildrenAction(getApplication(), editor));
        map.put(SendToBackAction.ID, new SendToBackAction(getApplication(), editor));
        map.put(BringToFrontAction.ID, new BringToFrontAction(getApplication(), editor));
        map.put(VIEWTOGGLE_PROPERTIES, new ToggleBooleanAction(
                getApplication(), this,
                VIEWTOGGLE_PROPERTIES,
                Resources.getResources("org.jhotdraw8.samples.grapher.Labels"), detailsVisible));
        map.put(GroupAction.ID, new GroupAction(getApplication(), editor, () -> createFigure(SimpleGroupFigure::new)));
        map.put(GroupAction.COMBINE_PATHS_ID, new GroupAction(GroupAction.COMBINE_PATHS_ID, getApplication(), editor, () -> createFigure(SimpleCombinedPathFigure::new)));
        map.put(UngroupAction.ID, new UngroupAction(getApplication(), editor));
        map.put(AddToGroupAction.ID, new AddToGroupAction(getApplication(), editor));
        map.put(RemoveFromGroupAction.ID, new RemoveFromGroupAction(getApplication(), editor));

    }

    @Nonnull
    private Supplier<Layer> initToolBar() throws MissingResourceException {
        //drawingView.setConstrainer(new GridConstrainer(0,0,10,10,45));
        ToolsToolbar ttbar = new ToolsToolbar(editor);
        Resources labels = Resources.getResources("org.jhotdraw8.samples.grapher.Labels");
        Supplier<Layer> layerFactory = () -> createFigure(SimpleLayer::new);
        Tool defaultTool;
        ttbar.addTool(defaultTool = new SelectionTool("tool.resizeFigure", HandleType.RESIZE, null, HandleType.LEAD, labels), 0, 0);
        ttbar.addTool(new SelectionTool("tool.moveFigure", HandleType.MOVE, null, HandleType.LEAD, labels), 1, 0);
        ttbar.addTool(new SelectionTool("tool.selectPoint", HandleType.POINT, labels), 0, 1);
        ttbar.addTool(new SelectionTool("tool.transform", HandleType.TRANSFORM, labels), 1, 1);
        ttbar.addTool(new CreationTool("edit.createRectangle", labels, () -> createFigure(SimpleRectangleFigure::new), layerFactory), 2, 0, 16);
        ttbar.addTool(new CreationTool("edit.createEllipse", labels, () -> createFigure(SimpleEllipseFigure::new), layerFactory), 3, 0);
        ttbar.addTool(new ConnectionTool("edit.createLineConnection", labels, () -> createFigure(SimpleLineConnectionWithMarkersFigure::new), layerFactory), 3, 1);
        ttbar.addTool(new CreationTool("edit.createLine", labels, () -> createFigure(SimpleLineFigure::new), layerFactory), 2, 1, 16);
        ttbar.addTool(new PolyCreationTool("edit.createPolyline", labels, SimplePolylineFigure.POINTS, () -> createFigure(SimplePolylineFigure::new), layerFactory), 4, 1);
        ttbar.addTool(new PolyCreationTool("edit.createPolygon", labels, SimplePolygonFigure.POINTS, () -> createFigure(SimplePolygonFigure::new), layerFactory), 5, 1);
        ttbar.addTool(new BezierCreationTool("edit.createBezier", labels, SimpleBezierFigure.PATH, () -> createFigure(SimpleBezierFigure::new), layerFactory), 6, 1);
        ttbar.addTool(new CreationTool("edit.createText", labels,//
                () -> createFigure(() -> new SimpleLabelFigure(0, 0, "Hello", FillableFigure.FILL, null, StrokeableFigure.STROKE, null)), //
                layerFactory), 6, 0);
        ttbar.addTool(new CreationTool("edit.createPageLabel", labels,//
                () -> createFigure(() -> new SimplePageLabelFigure(0, 0,
                labels.getFormatted("pageLabel.text", SimplePageLabelFigure.PAGE_PLACEHOLDER, SimplePageLabelFigure.NUM_PAGES_PLACEHOLDER),
                FillableFigure.FILL, null, StrokeableFigure.STROKE, null)), //
                layerFactory), 9, 1);
        ttbar.addTool(new ImageCreationTool("edit.createImage", labels, () -> createFigure(SimpleImageFigure::new), layerFactory), 4, 0);
        ttbar.addTool(new CreationTool("edit.createSlice", labels, () -> createFigure(SimpleSliceFigure::new), layerFactory), 8, 0, 16);
        ttbar.addTool(new CreationTool("edit.createPage", labels, () -> createFigure(() -> {
            SimplePageFigure pf = new SimplePageFigure();
            pf.set(SimplePageFigure.PAPER_SIZE, new CssSize2D(297, 210, "mm"));
            pf.set(SimplePageFigure.PAGE_INSETS, new CssSizeInsets(2, 1, 2, 1, "cm"));
            SimplePageLabelFigure pl = new SimplePageLabelFigure(940, 700, labels.getFormatted("pageLabel.text",
                    SimplePageLabelFigure.PAGE_PLACEHOLDER, SimplePageLabelFigure.NUM_PAGES_PLACEHOLDER),
                    FillableFigure.FILL, null, StrokeableFigure.STROKE, null);
            pf.addChild(pl);
            return pf;
        }), layerFactory), 8, 1, 16);
        ttbar.setDrawingEditor(editor);
        editor.setDefaultTool(defaultTool);
        toolsToolBar.getItems().add(ttbar);
        return layerFactory;
    }

    @Override
    public void initView() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            node = loader.load(getClass().getResourceAsStream("Grapher.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        drawingView = new SimpleDrawingView();
        // FIXME should use preferences!
        drawingView.setConstrainer(new GridConstrainer(0, 0, 10, 10, 11.25, 5, 5));
        //drawingView.setHandleType(HandleType.TRANSFORM);
        //
        drawingView.getModel().addListener((InvalidationListener) drawingModel -> {
            modified.set(true);
        });

        FigureFactory factory = new DefaultFigureFactory();
        IdFactory idFactory = new SimpleFigureIdFactory();
        SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
        drawingView.setClipboardOutputFormat(new MultiClipboardOutputFormat(
                io, new SvgExportOutputFormat(), new BitmapExportOutputFormat()));
        drawingView.setClipboardInputFormat(new MultiClipboardInputFormat(io));

        editor = new SimpleDrawingEditor();
        editor.addDrawingView(drawingView);

        ScrollPane viewScrollPane = new ScrollPane();
        viewScrollPane.setFitToHeight(true);
        viewScrollPane.setFitToWidth(true);
        viewScrollPane.getStyleClass().addAll("view", "flush");
        viewScrollPane.setContent(drawingView.getNode());

        Supplier<Layer> layerFactory = initToolBar();

        ZoomToolbar ztbar = new ZoomToolbar();
        ztbar.zoomFactorProperty().bindBidirectional(drawingView.zoomFactorProperty());
        toolsToolBar.getItems().add(ztbar);

        // set up the docking framework
        dockRoot = new DockRoot();
        dockRoot.setDockFactory(TabbedAccordionDock::new);
        dockRoot.setVerticalInnerTrackFactory(ScrollableVBoxTrack::new);
        dockRoot.setHorizontalTrackFactory(SplitPaneTrack::createHorizontalTrack);
        dockRoot.getVerticalTrackFactoryMap().put(SingleItemDock.class, SplitPaneTrack::createVerticalTrack);
        dockRoot.setVerticalRootTrackFactory(SplitPaneTrack::createVerticalTrack);
        DockItem dockItem = new DockItem(null, viewScrollPane);
        SingleItemDock singleItemDock = new SingleItemDock(dockItem);
        dockRoot.addDock(singleItemDock);

        contentPane.setCenter(dockRoot);

        FXWorker.supply(() -> {
            Set<Dock> d = new LinkedHashSet<>();
            Dock dock = new TabbedAccordionDock();
            dock.getItems().add(addInspector(new StyleAttributesInspector(), "styleAttributes", Priority.ALWAYS));
            dock.getItems().add(addInspector(new StyleClassesInspector(), "styleClasses", Priority.NEVER));
            dock.getItems().add(addInspector(new StylesheetsInspector(), "styleSheets", Priority.ALWAYS));
            d.add(dock);
            dock = new TabbedAccordionDock();
            dock.getItems().add(addInspector(new LayersInspector(layerFactory), "layers", Priority.ALWAYS));
            dock.getItems().add(addInspector(new HierarchyInspector(), "figureHierarchy", Priority.ALWAYS));
            d.add(dock);
            dock = new TabbedAccordionDock();
            dock.getItems().add(addInspector(new DrawingInspector(), "drawing", Priority.NEVER));
            dock.getItems().add(addInspector(new GridInspector(), "grid", Priority.NEVER));
            dock.getItems().add(addInspector(new HelpTextInspector(), "helpText", Priority.NEVER));
            d.add(dock);
            return d;
        }).thenAccept(list -> {
            ScrollableVBoxTrack vtrack = new ScrollableVBoxTrack();
            Set<DockItem> items = new LinkedHashSet<>();
            for (Dock dock : list) {
                for (DockItem n : dock.getItems()) {
                    items.add(n);
                    Inspector i = (Inspector) n.getProperties().get("inspector");
                    i.setDrawingView(drawingView);
                }
                vtrack.getItems().add(dock.getNode());
            }
            SplitPaneTrack htrack = SplitPaneTrack.createHorizontalTrack();
            htrack.getItems().add(vtrack.getNode());
            dockRoot.addTrack(htrack);
            dockRoot.setDockableItems(FXCollections.observableSet(items));
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

    }

    @Override
    public CompletionStage<Void> print(@Nonnull PrinterJob job) {
        Drawing drawing = drawingView.getDrawing();
        return FXWorker.run(() -> {
            try {
                PrinterExportFormat pof = new PrinterExportFormat();
                pof.print(job, drawing);
            } finally {
                job.endJob();
            }
        });

    }

    @Override
    public CompletionStage<DataFormat> read(@Nonnull URI uri, DataFormat format, Map<? super Key<?>, Object> options, boolean append) {
        return FXWorker.supply(() -> {
            FigureFactory factory = new DefaultFigureFactory();
            IdFactory idFactory = new SimpleFigureIdFactory();
            SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
            SimpleDrawing drawing = (SimpleDrawing) io.read(uri, null);
            System.out.println("READING..." + uri);
            return drawing;
        }).thenApply(drawing->{drawingView.setDrawing(drawing);return format;});
    }

    @Override
    public void start() {
        getNode().getScene().getStylesheets().addAll(//
                GrapherApplication.class.getResource("/org/jhotdraw8/draw/inspector/inspector.css").toString(),//
                GrapherApplication.class.getResource("/org/jhotdraw8/samples/grapher/grapher.css").toString()//
        );

        Preferences prefs = Preferences.userNodeForPackage(GrapherController.class);
//        PreferencesUtil.installVisibilityPrefsHandlers(prefs, detailsScrollPane, detailsVisible, mainSplitPane, Side.RIGHT);
    }

    @Override
    public CompletionStage<Void> write(@Nonnull URI uri, DataFormat format, Map<? super Key<?>, Object> options) {
        Drawing drawing = drawingView.getDrawing();
        return FXWorker.run(() -> {
            if (SvgExporter.SVG_FORMAT.equals(format) || uri.getPath().endsWith(".svg")) {
                SvgExportOutputFormat io = new SvgExportOutputFormat();
                io.setOptions(options);
                io.write(uri, drawing);
            } else if (BitmapExportOutputFormat.PNG_FORMAT.equals(format) || uri.getPath().endsWith(".png")) {
                BitmapExportOutputFormat io = new BitmapExportOutputFormat();
                io.setOptions(options);
                io.write(uri, drawing);
            } else if (XMLEncoderOutputFormat.XML_SERIALIZER_FORMAT.equals(format) || uri.getPath().endsWith(".ser.xml")) {
                XMLEncoderOutputFormat io = new XMLEncoderOutputFormat();
                io.write(uri, drawing);
            } else {
                FigureFactory factory = new DefaultFigureFactory();
                IdFactory idFactory = new SimpleFigureIdFactory();
                SimpleXmlIO io = new SimpleXmlIO(factory, idFactory, GRAPHER_NAMESPACE_URI, null);
                io.write(uri, drawing);
            }
        }).handle((voidvalue, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
            return null;
        });
    }

}
