/* @(#)StringStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javax.annotation.Nonnull;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.css.text.CssStringOrIdentConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * This key has a string value which can be given as a CSS ident-token or
 * as a CSS string-token.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StringOrIdentStyleableFigureKey extends AbstractStyleableFigureKey<String> implements WriteableStyleableMapAccessor<String> {

    final static long serialVersionUID = 1L;
    @Nonnull
    private final CssMetaData<? extends Styleable, String> cssMetaData;

    /**
     * Creates a new instance with the specified name and with an empty String
     * as the default value.
     *
     * @param name The name of the key.
     */
    public StringOrIdentStyleableFigureKey(String name) {
        this(name, "");
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public StringOrIdentStyleableFigureKey(String name, String defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public StringOrIdentStyleableFigureKey(String name, DirtyMask mask, String defaultValue) {
        super(name, String.class, false, mask, defaultValue);

        Function<Styleable, StyleableProperty<String>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, String> converter
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, String> md
                = new SimpleCssMetaData<>(property, function,
                converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<? extends Styleable, String> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<String> converter;

    @Override
    public Converter<String> getConverter() {
        if (converter == null) {
            converter = new CssStringOrIdentConverter();
        }
        return converter;
    }
}
