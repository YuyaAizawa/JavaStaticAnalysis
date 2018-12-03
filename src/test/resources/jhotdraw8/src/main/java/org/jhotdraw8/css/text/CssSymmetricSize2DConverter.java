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
 * Converts a {@code javafx.geometry.Point2D} into a {@code String} and vice
 * versa. If the X and the Y-value are identical, then only one value is output.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSymmetricSize2DConverter implements Converter<CssSize2D> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,size}|[ ]+}", new CssConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, @Nonnull CssSize2D value) throws IOException {
        CssDimension x = value.getX();
        CssDimension y = value.getY();
        if (x == y) {
            formatter.toStr(out, idFactory, 1, value.getX());
        } else {
            formatter.toStr(out, idFactory, 2, value.getX(), value.getY());
        }
    }

    @Nonnull
    @Override
    public CssSize2D fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        int count = (Integer) v[0];
        switch (count) {
            case 1:
                return new CssSize2D(((CssDimension) v[1]), ((CssDimension) v[1]));
            case 2:
                return new CssSize2D(((CssDimension) v[1]), ((CssDimension) v[2]));
            default:
                throw new ParseException("one or two numbers expected, found " + count + " numbers", 0);
        }
    }

    @Nullable
    @Override
    public CssSize2D getDefaultValue() {
        return new CssSize2D(CssDimension.ZERO, CssDimension.ZERO);
    }
    
    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨SymmetricSize2D⟩: ⟨xy⟩ ｜ ⟨x⟩ ⟨y⟩";
    }
    
}
