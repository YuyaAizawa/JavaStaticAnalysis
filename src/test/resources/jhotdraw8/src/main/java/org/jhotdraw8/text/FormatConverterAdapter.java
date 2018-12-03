/* @(#)FormatConverterAdapter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Allows to use a {@code Converter} with the {@code java.text.Format} API.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FormatConverterAdapter extends Format {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final Converter<Object> converter;

    public FormatConverterAdapter(Converter<?> converter) {
        @SuppressWarnings("unchecked")
        Converter<Object> temp = (Converter<Object>) converter;
        this.converter = temp;
    }

    @Nonnull
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        toAppendTo.append(converter.toString(obj));
        return toAppendTo;
    }

    @Nullable
    @Override
    public Object parseObject(@Nonnull String source, ParsePosition pos) {
        try {
            CharBuffer buf = CharBuffer.wrap(source);
            Object value = converter.fromString(buf, null);
            pos.setIndex(buf.position());
            return value;
        } catch (ParseException ex) {
            pos.setErrorIndex(ex.getErrorOffset());
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
}
