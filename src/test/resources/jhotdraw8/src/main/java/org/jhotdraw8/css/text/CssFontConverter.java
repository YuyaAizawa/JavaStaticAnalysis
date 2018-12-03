/* @(#)CssFontConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.io.IdFactory;

/**
 * CssFontConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * CssFont := [FontStyle] [FontWeight] FontSize FontFamily ;
 * FontStyle := normal|italic|oblique;
 * FontWeight := normal|bold|bolder|lighter|100|200|300|400|500|600|700|800|900;
 * FontSize := Size;
 * FontFamily := Word|Quoted;
 * </pre>
 * <p>
 * FIXME currently only parses the Color production
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssFontConverter extends AbstractCssConverter<CssFont> {


    public CssFontConverter(boolean nullable) {
        super(nullable);
    }

    @Override
    public <TT extends CssFont> void produceTokensNonnull(@Nonnull TT font, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        double fontSize = font.getSize();
        String fontFamily = font.getFamily();
        final FontPosture posture = font.getPosture();

        boolean needsSpace = false;

        if (posture != null) {
            switch (font.getPosture()) {
                case ITALIC:
                    out.accept(new CssToken(CssTokenType.TT_IDENT, "italic"));
                    needsSpace = true;
                    break;
                case REGULAR:
                    break;
                default:
                    throw new RuntimeException("Unknown fontPosture:" + font.getPosture());
            }
        }
        final FontWeight weight = font.getWeight();
        if (weight != null) {
            switch (weight) {
                case NORMAL:
                    break;
                case BOLD:
                    if (needsSpace) {
                        out.accept(new CssToken(CssTokenType.TT_S, " "));
                    }
                    out.accept(new CssToken(CssTokenType.TT_IDENT, "bold"));
                    break;
                default:
                    if (needsSpace) {
                        out.accept(new CssToken(CssTokenType.TT_S, " "));
                    }
                    out.accept(new CssToken(CssTokenType.TT_NUMBER, weight.getWeight()));
                    break;
            }
        }
        out.accept(new CssToken(CssTokenType.TT_S, " "));
        out.accept(new CssToken(CssTokenType.TT_NUMBER, fontSize));
        out.accept(new CssToken(CssTokenType.TT_S, " "));
        if (fontFamily.contains(" ") || fontFamily.contains("\'") || fontFamily.contains("\"")) {
            out.accept(new CssToken(CssTokenType.TT_STRING, fontFamily));
        } else {
            out.accept(new CssToken(CssTokenType.TT_IDENT, fontFamily));
        }
    }

    @NotNull
    @Override
    public CssFont parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        FontPosture fontPosture = FontPosture.REGULAR;
        FontWeight fontWeight = FontWeight.NORMAL;
        double fontSize = 12.0;
        String fontFamily = "System";

        // parse FontStyle
        if (tt.next() == CssTokenType.TT_IDENT) {
            switch (tt.currentStringNonnull().toLowerCase()) {
                case "normal":
                    fontPosture = FontPosture.REGULAR;
                    break;
                case "italic":
                case "oblique":
                    fontPosture = FontPosture.ITALIC;
                    break;
                default:
                    tt.pushBack();
                    break;
            }
        } else {
            tt.pushBack();
        }

        // parse FontWeight
        boolean fontWeightConsumed = false;
        if (tt.next() == CssTokenType.TT_IDENT) {
            switch (tt.currentStringNonnull().toLowerCase()) {
                case "normal":
                    fontWeight = FontWeight.NORMAL;
                    fontWeightConsumed = true;
                    break;
                case "bold":
                    fontWeight = FontWeight.BOLD;
                    fontWeightConsumed = true;
                    break;
                case "bolder":
                    // FIXME weight should be relative to parent font
                    fontWeight = FontWeight.BOLD;
                    fontWeightConsumed = true;
                    break;
                case "lighter":
                    // FIXME weight should be relative to parent font
                    fontWeight = FontWeight.LIGHT;
                    fontWeightConsumed = true;
                    break;
                default:
                    tt.pushBack();
                    break;
            }
        } else {
            tt.pushBack();
        }

        double fontWeightOrFontSize = 0.0;
        boolean fontWeightOrFontSizeConsumed = false;
        if (!fontWeightConsumed) {
            if (tt.next() == CssTokenType.TT_NUMBER) {
                fontWeightOrFontSize = tt.currentNumberNonnull().doubleValue();
                fontWeightOrFontSizeConsumed = true;
            } else {
                tt.pushBack();
            }
        }

        // parse FontSize
        if (tt.next() == CssTokenType.TT_NUMBER) {
            fontSize = tt.currentNumberNonnull().doubleValue();

            if (fontWeightOrFontSizeConsumed) {
                switch ((int) fontWeightOrFontSize) {
                    case 100:
                        fontWeight = FontWeight.THIN;
                        break;
                    case 200:
                        fontWeight = FontWeight.EXTRA_LIGHT;
                        break;
                    case 300:
                        fontWeight = FontWeight.LIGHT;
                        break;
                    case 400:
                        fontWeight = FontWeight.NORMAL;
                        break;
                    case 500:
                        fontWeight = FontWeight.MEDIUM;
                        break;
                    case 600:
                        fontWeight = FontWeight.SEMI_BOLD;
                        break;
                    case 700:
                        fontWeight = FontWeight.BOLD;
                        break;
                    case 800:
                        fontWeight = FontWeight.EXTRA_BOLD;
                        break;
                    case 900:
                        fontWeight = FontWeight.BLACK;
                        break;
                    default:
                        throw new ParseException("⟨Font⟩: illegal font weight " + fontWeightOrFontSize, tt.getStartPosition());
                }
            }

        } else if (fontWeightOrFontSizeConsumed) {
            tt.pushBack();
            fontSize = fontWeightOrFontSize;
        } else {
            tt.pushBack();
        }

        if (tt.next() == CssTokenType.TT_IDENT || tt.current() == CssTokenType.TT_STRING) {
            fontFamily = tt.currentString();
            while (tt.next() == CssTokenType.TT_IDENT) {
                fontFamily += " " + tt.currentString();
            }
        } else if (tt.current() == CssTokenType.TT_STRING) {
            fontFamily = tt.currentString();
        } else {
            throw new ParseException("⟨Font⟩: ⟨FontFamily⟩ expected", tt.getStartPosition());
        }
        CssFont font = CssFont.font(fontFamily, fontWeight, fontPosture, fontSize);
        if (font == null) {
            font = CssFont.font(null, fontWeight, fontPosture, fontSize);
        }
        return font;
    }


    @Override
    public String getHelpText() {
        return "Format of ⟨Font⟩: ［⟨FontStyle⟩］［⟨FontWeight⟩］ ⟨FontSize⟩ ⟨FontFamily⟩";
    }
}
