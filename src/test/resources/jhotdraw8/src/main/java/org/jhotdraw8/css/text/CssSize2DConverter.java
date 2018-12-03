/* @(#)CssPoint2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.PatternConverter;

/**
 * Converts a {@code CssSize2D} into a {@code String} and vice versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSize2DConverter implements Converter<CssSize2D> {

    private final PatternConverter formatter = new PatternConverter("{0,size} +{1,size}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, @Nonnull CssSize2D value) throws IOException {
        formatter.toStr(out, idFactory, value.getX(), value.getY());
    }

    @Nonnull
    @Override
    public CssSize2D fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new CssSize2D((CssDimension) v[0], (CssDimension) v[1]);
    }

    @Nullable
    @Override
    public CssSize2D getDefaultValue() {
        return new CssSize2D(new CssDimension(0, null), new CssDimension(0, null));
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Size2D⟩: ⟨x⟩ ⟨y⟩";
    }

}
