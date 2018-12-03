/* @(#)PrefsURIListKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.util.prefs;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * PrefsURIListKey. The words are separated by tab character.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PrefsURIListKey {
    private final String key;
    @Nonnull
    private final List<String> defaultValue;


    public PrefsURIListKey(String key, @Nonnull List<String>  defaultValue) {
        this.key = key;
        this.defaultValue = Collections.unmodifiableList(new ArrayList<>(defaultValue));

    }

    @Nonnull
    public List<String> get(Preferences prefs) {
        return defaultValue;
    }

    public void put(Preferences prefs, int newValue) {
    }
}
