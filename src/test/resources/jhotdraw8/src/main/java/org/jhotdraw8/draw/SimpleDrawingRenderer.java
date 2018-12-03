/* @(#)SimpleDrawingRenderer.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.figure.Drawing;
import java.util.Collection;
import org.jhotdraw8.draw.figure.Figure;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.Node;
import org.jhotdraw8.beans.SimplePropertyBean;
import org.jhotdraw8.collection.Key;

/**
 * SimpleDrawingRenderer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawingRenderer extends SimplePropertyBean implements RenderContext {

    // ---
    // Field declarations
    // ---
    protected final HashMap<Figure, Node> figureToNodeMap = new HashMap<>();

    // ---
    // Behavior
    // ---
    @Override
    public Node getNode(@Nonnull Figure f) {
        Node n = figureToNodeMap.get(f);
        if (n == null) {
            n = f.createNode(this);
            figureToNodeMap.put(f, n);
        }
        return n;
    }

    /**
     * Renders the provided figure into a JavaFX Node.
     *
     * @param figure The figure
     * @return the rendered node
     */
    public Node render(@Nonnull Figure figure) {
        figureToNodeMap.clear();
        renderRecursive(figure);
        return getNode(figure);
    }

    /**
     * Recursive part of the render method.
     *
     * @param figure The figure
     */
    private void renderRecursive(Figure figure) {
        figure.updateNode(this, getNode(figure));
        for (Figure child : figure.getChildren()) {
            renderRecursive(child);
        }
    }

    public static Node toNode(@Nonnull Drawing external, @Nonnull Collection<Figure> selection, @Nullable Map<Key<?>, Object> renderingHints) {
        SimpleDrawingRenderer r = new SimpleDrawingRenderer();
        if (renderingHints != null) {
            r.getProperties().putAll(renderingHints);
        }
        LinkedList<Node> nodes = new LinkedList<>();
        for (Figure f : external.preorderIterable()) {
            if (selection.contains(f)) {
                nodes.add(r.render(f));
            }
        }
        Node drawingNode;
        if (nodes.size() == 1) {
            drawingNode = nodes.getFirst();
        } else {
            drawingNode = new Group(nodes);
        }
        return drawingNode;
    }

}
