/* @(#)CssBooleanConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.io.IdFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a {@code Boolean} into the CSS String representation.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssBooleanConverter extends AbstractCssConverter<Boolean> {

    private static final long serialVersionUID = 1L;

    private final String trueString = "true";
    private final String falseString = "false";

    public CssBooleanConverter(boolean nullable) {
        super(nullable);
    }

    @Nonnull
    @Override
    public Boolean parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        tt.requireNextToken(CssTokenType.TT_IDENT,"⟨Boolean⟩ identifier expected.");
        String s = tt.currentString();
        if (s==null)s=CssTokenType.IDENT_NONE;
        switch (s) {
            case trueString:
                return Boolean.TRUE;
            case falseString:
                return Boolean.FALSE;
            default:
                throw new ParseException("⟨Boolean⟩ "+trueString+" or "+falseString+" expected.",tt.getStartPosition());
        }
    }

    @Override
    public <TT extends Boolean> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
            out.accept(new CssToken(CssTokenType.TT_IDENT,((Boolean)value)?trueString:falseString));
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Boolean⟩: true｜false";
    }

}
