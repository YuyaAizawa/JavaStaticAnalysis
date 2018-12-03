/* @(#)AbstractFocusOwnerAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ViewController;

/**
 * AbstractFocusOwnerAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFocusOwnerAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    @Nullable
    private Node target = null;

    @Nullable
    private final ChangeListener<ViewController> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        if (newValue == null || newValue.getNode() == null) {
            disabled.set(true);
        } else {
            Scene s = newValue.getNode().getScene();
            BooleanBinding binding = Bindings.isNotEmpty(disablers).or(app.disabledProperty());
            if (target != null) {
                binding = binding.or(s.focusOwnerProperty().isNotEqualTo(target));
            } else {
                binding = binding.or(s.focusOwnerProperty().isNull());
            }
            disabled.bind(binding);
        }
    };

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public AbstractFocusOwnerAction(@Nonnull Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param target the target node
     */
    public AbstractFocusOwnerAction(@Nonnull Application app, Node target) {
        super(app);
        this.target = target;

        app.activeViewProperty().addListener(activeViewListener);
        activeViewListener.changed(null, null, app == null ? null : app.getActiveView());

    }
}
