/* @(#)EffectStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.effect.Effect;
import javax.annotation.Nonnull;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssEffectConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * EffectStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EffectStyleableFigureKey extends AbstractStyleableFigureKey<Effect> implements WriteableStyleableMapAccessor<Effect> {

    final static long serialVersionUID = 1L;
    private final CssEffectConverter converter = new CssEffectConverter();
    private final CssMetaData<? extends Styleable, Effect> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public EffectStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public EffectStyleableFigureKey(String name, Effect defaultValue) {
        super(name, Effect.class, DirtyMask.of(DirtyBits.NODE), defaultValue);

        StyleablePropertyFactory<? extends Styleable> factory = new StyleablePropertyFactory<>(null);
        cssMetaData = factory.createEffectCssMetaData(
                Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public CssMetaData<? extends Styleable, Effect> getCssMetaData() {
        return cssMetaData;

    }

    @Nonnull
    @Override
    public Converter<Effect> getConverter() {
        return converter;
    }
}
