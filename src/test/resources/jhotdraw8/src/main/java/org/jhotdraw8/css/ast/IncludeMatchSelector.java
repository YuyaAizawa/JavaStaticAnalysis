/* @(#)IncludeMatchSelector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.css.SelectorModel;

/**
 * An "include match selector" {@code ~=} matches an element if the element has
 * an attribute with the specified name and the attribute value contains a word
 * list with the specified word.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IncludeMatchSelector extends AbstractAttributeSelector {

    private final String attributeName;
    private final String word;

    public IncludeMatchSelector(String attributeName, String word) {
        this.attributeName = attributeName;
        this.word = word;
    }

    @Nullable
    @Override
    protected <T> T match(@Nonnull SelectorModel<T> model, T element) {
        return model.attributeValueContainsWord(element, attributeName, word) ? element : null;
    }

    @Nonnull
    @Override
    public String toString() {
        return "[" + attributeName + "~=" + word + ']';
    }
}
