/* @(#)RedoAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import javafx.event.ActionEvent;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractViewControllerAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.ViewController;

/**
 * Redoes the last user action on the active view.
 * <p>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RedoAction extends AbstractViewControllerAction<ViewController> {

    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.redo";
    private Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");

    /*
    private PropertyChangeListener redoActionPropertyListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == AbstractAction.NAME) {
                putValue(AbstractAction.NAME, evt.getNewValue());
            } else if (name == "enabled") {
                updateEnabledState();
            }
        }
    };*/

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public RedoAction(Application app, ViewController view) {
        super(app, view,null);
        labels.configureAction(this, ID);
    }

    /*
    protected void updateEnabledState() {
        boolean isEnabled = false;
        Action realRedoAction = getRealRedoAction();
        if (realRedoAction != null && realRedoAction!=this) {
            isEnabled = realRedoAction.isEnabled();
        }
        setEnabled(isEnabled);
    }

    @Override
    protected void updateView(ViewController oldValue, ViewController newValue) {
        super.updateView(oldValue, newValue);
        if (newValue != null && //
                newValue.getActionMap().get(ID) != null && //
                newValue.getActionMap().get(ID) != this) {
            putValue(AbstractAction.NAME, newValue.getActionMap().get(ID).
                    getValue(AbstractAction.NAME));
            updateEnabledState();
        }
    }

    /**
     * Installs listeners on the view object.
     * /
    @Override
    protected void installViewListeners(ViewController p) {
        super.installViewListeners(p);
        Action redoActionInView = p.getActionMap().get(ID);
        if (redoActionInView != null && redoActionInView != this) {
            redoActionInView.addPropertyChangeListener(redoActionPropertyListener);
        }
    }

    /**
     * Installs listeners on the view object.
     * /
    @Override
    protected void uninstallViewListeners(ViewController p) {
        super.uninstallViewListeners(p);
        Action redoActionInView = p.getActionMap().get(ID);
        if (redoActionInView != null && redoActionInView != this) {
            redoActionInView.removePropertyChangeListener(redoActionPropertyListener);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Action realAction = getRealRedoAction();
        if (realAction != null && realAction!=this) {
            realAction.actionPerformed(e);
        }
    }

    @Nullable
    private Action getRealRedoAction() {
        return (getActiveView() == null) ? null : getActiveView().getActionMap().get(ID);
    }

    @Override
    public void handle(javafx.event.ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
     */

    @Override
    protected void handleActionPerformed(ActionEvent event, ViewController view) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
