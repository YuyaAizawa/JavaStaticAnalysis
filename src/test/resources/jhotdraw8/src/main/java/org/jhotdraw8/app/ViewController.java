/* @(#)ViewController.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import org.jhotdraw8.collection.HierarchicalMap;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javax.annotation.Nullable;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.beans.PropertyBean;

/**
 * A {@code ViewController} represents an activity that the user
 * is going to perform with help of the computer.
 * <p>
 * The life-cycle of a view controller object is managed by an application. See the
 * class comment of {@link Application} on how to launch an application.
 * <p>
 * The lifecycle of an activity consists of the following steps:
 * <ol>
 * <li><b>Creation</b><br>
 * The application model instantiates a new activity.
 * </li>
 * <li><b>Initialisation</b><br>
 * The application calls the following methods:
 * {@code getActionMap().setParent();setApplication(); init()}. Then it either
 * calls {@code clear()} or {@code read()}.
 * </li>
 * <li><b>Start</b><br>
 * The application adds the component of the activity to a container (for example
 * a Stage) and then calls {@code start()}.
 * </li>
 * <li><b>Activation</b><br>
 * When an activity becomes the active view of the application, application
 * calls {@code activate()}.
 * </li>
 * <li><b>Deactivation</b><br>
 * When an activity is not anymore the active view of the application,
 * application calls {@code deactivate()}. At a later time, the activity may
 * become activated again.
 * </li>
 * <li><b>Stop</b><br>
 * The application calls {@code stop()} on the activity and then removes its
 * view from the screen. At a later time, the activity may be started
 * again.
 * </li>
 * <li><b>Dispose</b><br>
 * When the view is no longer needed, application calls {@code dispose()} on
 * the view, followed by
 * {@code setApplication(null);}, {@code getActionMap().setParent(null)} and
 * then removes all references to it, so that it can be garbage collected.
 * </li>
 * </ol>
 *
 * @design.pattern Application Framework, KeyAbstraction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ViewController extends Disableable, PropertyBean {

    /**
     * Activates the view.
     */
    public void activate();

    /**
     * The application property is maintained by the application.
     *
     * @return the property
     */
    public ObjectProperty<Application> applicationProperty();

    /**
     * Deactivates the view.
     */
    public void deactivate();

    public IntegerProperty disambiguationProperty();

    /**
     * Disposes of the view.
     */
    public void dispose();

    /**
     * The action map of the view.
     *
     * @return the action map
     */
    public HierarchicalMap<String, Action> getActionMap();

    @Nullable
    default public Application getApplication() {
        return applicationProperty().get();
    }

    default public void setApplication(@Nullable Application newValue) {
        applicationProperty().set(newValue);
    }

    default public int getDisambiguation() {
        return disambiguationProperty().get();
    }

    default public void setDisambiguation(int newValue) {
        disambiguationProperty().set(newValue);
    }

    /**
     * Returns the scene node which renders the view for this controller.
     *
     * @return The node.
     */
    public Node getNode();

    // convenience method
    @Nullable
    default public String getTitle() {
        return titleProperty().get();
    }

    default public void setTitle(@Nullable String newValue) {
        titleProperty().set(newValue);
    }

    /**
     * Initializes the view. This method must be called before the view can be
     * used.
     */
    public void init();

    /**
     * Starts the view.
     */
    public void start();

    /**
     * Stops the view.
     */
    public void stop();

    /**
     * Provides a title for the view
     *
     * @return The title property.
     */
    public StringProperty titleProperty();

}
