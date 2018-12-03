/* @(#)XmlBoundingBoxConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.BoundingBox;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.PatternConverter;

/**
 * Converts a {@code javafx.geometry.BoundingBox} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlBoundingBoxConverter implements Converter<BoundingBox> {

    private final PatternConverter formatter = new PatternConverter("{0,number} +{1,number} +{2,number} +{3,number}", new XmlConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, @Nonnull BoundingBox value) throws IOException {
        formatter.toStr(out, idFactory, value.getMinX(), value.getMinY(), value.getWidth(), value.getHeight());
    }

    @Nonnull
    @Override
    public BoundingBox fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new BoundingBox((double) v[0], (double) v[1], (double) v[2], (double) v[3]);
    }

    @Nonnull
    @Override
    public BoundingBox getDefaultValue() {
        return new BoundingBox(0, 0, 1, 1);
    }
}
