/* @(#)CssStringConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssStreamTokenizer;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.CharBufferReader;
import org.jhotdraw8.text.Converter;

/**
 * Converts an SVG path to a CSS String.
 * <p>
 * The null value will be converted to the CSS identifier "none".
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSvgPathConverter implements Converter<String> {

    private final boolean nullable;

    public CssSvgPathConverter(boolean nullable) {
        this.nullable = nullable;
    }

    @Nullable
    @Override
    public String fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssStreamTokenizer tt = new CssStreamTokenizer(new CharBufferReader(buf));

        String p = null;
        if (tt.next() == CssTokenType.TT_IDENT) {
            if (!nullable) {
                throw new ParseException("String expected. " + tt.current(), buf.position());
            }
            if (!"none".equals(tt.currentString())) {
                throw new ParseException("none or String expected. " + tt.current()+" "+tt.currentString(), buf.position());
            }
            p = null;
        } else {
            if (tt.current() != CssTokenType.TT_STRING) {
                throw new ParseException("Css String expected. " + tt.current(), buf.position());
            }
            p = (tt.currentString());
        }
        buf.position(buf.limit());

        return p;
    }

  @Nonnull
  @Override
    public String getHelpText() {
        StringBuilder buf = new StringBuilder("Format of ⟨SvgPath⟩: \" ⟨moveTo ⟩｛ moveTo｜⟨lineTo⟩｜⟨quadTo⟩｜⟨cubicTo⟩｜⟨arcTo⟩｜⟨closePath⟩ ｝ \"");
        buf.append("\nFormat of ⟨moveTo ⟩: M ⟨x⟩ ⟨y⟩ ｜m ⟨dx⟩ ⟨dy⟩ ");
        buf.append("\nFormat of ⟨lineTo ⟩: L ⟨x⟩ ⟨y⟩ ｜l ⟨dx⟩ ⟨dy⟩ | H ⟨x⟩ | h ⟨dx⟩ | V ⟨y⟩ | v ⟨dy⟩");
        buf.append("\nFormat of ⟨quadTo ⟩: Q ⟨x⟩ ⟨y⟩  ⟨x1⟩ ⟨y1⟩ ｜q ⟨dx⟩ ⟨dy⟩  ⟨x1⟩ ⟨y1⟩ ｜T ⟨x⟩ ⟨y⟩ ｜t ⟨dx⟩ ⟨dy⟩");
        buf.append("\nFormat of ⟨cubicTo ⟩: C ⟨x⟩ ⟨y⟩  ⟨x1⟩ ⟨y1⟩  ⟨x2⟩ ⟨y2⟩ ｜c ⟨dx⟩ ⟨dy⟩  ⟨dx1⟩ ⟨dy1⟩  ⟨dx2⟩ ⟨dy2⟩｜ S ⟨x⟩ ⟨y⟩  ⟨x1⟩ ⟨y1⟩ ｜s ⟨dx⟩ ⟨dy⟩  ⟨dx1⟩ ⟨dy1⟩");
        buf.append("\nFormat of ⟨arcTo ⟩: A ⟨x⟩ ⟨y⟩ ⟨r1⟩ ⟨r2⟩ ⟨angle⟩ ⟨larrgeArcFlag⟩ ⟨sweepFlag⟩ ｜a ⟨dx⟩ ⟨dy⟩ ⟨r1⟩ ⟨r2⟩ ⟨angle⟩ ⟨larrgeArcFlag⟩ ⟨sweepFlag⟩ ");
        buf.append("\nFormat of ⟨closePath ⟩: Z ｜z ");
        return buf.toString();
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable String value) throws IOException {
        if (value == null) {
            if (!nullable) {
                throw new IllegalArgumentException("value is null");
            }
            out.append("none");
            return;
        }
        out.append('"');
        for (char ch : value.toCharArray()) {
            switch (ch) {
                case '"':
                    out.append('\\');
                    out.append('"');
                    break;
                case ' ':
                    out.append(ch);
                    break;
                case '\n':
                    out.append('\\');
                    out.append('\n');
                    break;
                default:
                    if (Character.isISOControl(ch) || Character.isWhitespace(ch)) {
                        out.append('\\');
                        String hex = Integer.toHexString(ch);
                        for (int i = 0, n = 6 - hex.length(); i < n; i++) {
                            out.append('0');
                        }
                        out.append(hex);
                    } else {
                        out.append(ch);
                    }
                    break;
            }
        }
        out.append('"');
    }

    @Nullable
    @Override
    public String getDefaultValue() {
        
        return null;
    }
    
    
}
