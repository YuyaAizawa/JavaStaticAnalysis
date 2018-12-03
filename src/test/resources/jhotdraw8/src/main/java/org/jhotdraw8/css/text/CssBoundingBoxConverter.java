/* @(#)CssBoundingBoxConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import javafx.geometry.BoundingBox;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.BoundingBox} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssBoundingBoxConverter extends AbstractCssConverter<BoundingBox> {
    private final boolean withSpace;

    public CssBoundingBoxConverter(boolean nullable) {
        this(nullable, true);
    }

    public CssBoundingBoxConverter(boolean nullable, boolean withSpace) {
        super(nullable);
        this.withSpace = withSpace;
    }

    @Nonnull
    @Override
    public BoundingBox parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        final double x, y, width, height;
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨min-x⟩ expected.");
        x = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨min-y⟩ expected.");
        y = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨width⟩ expected.");
        width = tt.currentNumberNonnull().doubleValue();
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        tt.requireNextToken(CssTokenType.TT_NUMBER, " ⟨BoundingBox⟩: ⟨height⟩ expected.");
        height = tt.currentNumberNonnull().doubleValue();

        return new BoundingBox(x, y, width, height);
    }

    @Override
    protected <TT extends BoundingBox> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getMinX()));
        out.accept(new CssToken(CssTokenType.TT_COMMA));
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getMinY()));
        out.accept(new CssToken(CssTokenType.TT_COMMA));
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getWidth()));
        out.accept(new CssToken(CssTokenType.TT_COMMA));
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getHeight()));
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨BoundingBox⟩: ⟨min-x⟩ ⟨min-y⟩ ⟨width⟩ ⟨height⟩";
    }
}
