/* @(#)URIExtensionFilter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import java.util.List;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import javax.annotation.Nonnull;

/**
 * URIExtensionFilter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class URIExtensionFilter {

    private final DataFormat format;

    private final FileChooser.ExtensionFilter extensionFilter;

    public URIExtensionFilter(@Nonnull String description, DataFormat format, String... extensions) {
        extensionFilter = new FileChooser.ExtensionFilter(description, extensions);
        this.format = format;
    }

    public URIExtensionFilter(@Nonnull final String description, DataFormat format,
                              final List<String> extensions) {
        extensionFilter = new FileChooser.ExtensionFilter(description, extensions);
        this.format = format;
    }

    public FileChooser.ExtensionFilter getFileChooserExtensionFilter() {
        return extensionFilter;
    }

    public String getDescription() {
        return extensionFilter.getDescription();
    }

    public List<String> getExtensions() {
        return extensionFilter.getExtensions();
    }

    public DataFormat getDataFormat() {
        return format;
    }
}
