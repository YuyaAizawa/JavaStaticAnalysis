/* @(#)CssParser.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import org.jhotdraw8.css.ast.AbstractAttributeSelector;
import org.jhotdraw8.css.ast.AdjacentSiblingCombinator;
import org.jhotdraw8.css.ast.AndCombinator;
import org.jhotdraw8.css.ast.AtRule;
import org.jhotdraw8.css.ast.ExistsMatchSelector;
import org.jhotdraw8.css.ast.EqualsMatchSelector;
import org.jhotdraw8.css.ast.ChildCombinator;
import org.jhotdraw8.css.ast.ClassSelector;
import org.jhotdraw8.css.ast.DashMatchSelector;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.DescendantCombinator;
import org.jhotdraw8.css.ast.FunctionPseudoClassSelector;
import org.jhotdraw8.css.ast.GeneralSiblingCombinator;
import org.jhotdraw8.css.ast.IdSelector;
import org.jhotdraw8.css.ast.IncludeMatchSelector;
import org.jhotdraw8.css.ast.PrefixMatchSelector;
import org.jhotdraw8.css.ast.PseudoClassSelector;
import org.jhotdraw8.css.ast.SelectNothingSelector;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Selector;
import org.jhotdraw8.css.ast.SelectorGroup;
import org.jhotdraw8.css.ast.SimplePseudoClassSelector;
import org.jhotdraw8.css.ast.SimpleSelector;
import org.jhotdraw8.css.ast.Stylesheet;
import org.jhotdraw8.css.ast.SubstringMatchSelector;
import org.jhotdraw8.css.ast.SuffixMatchSelector;
import org.jhotdraw8.css.ast.TypeSelector;
import org.jhotdraw8.css.ast.UniversalSelector;

/**
 * The {@code CssParser} processes a stream of characters into a
 * {@code Stylesheet} object.
 * <p>
 * The CSS Syntax Module Level 3 defines a grammar which is equivalent to the
 * following EBNF ISO/IEC 14977 productions:
 * <pre>
 * stylesheet_core = { S | CDO | CDC | qualified_rule | at_rule} ;
 *
 * rule_list    = { S | qualified_rule | at_rule} ;
 *
 * at_rule      = AT_KEYWORD , { component_value } , ( curly_block | ';' ) ;
 *
 * qualified_rule
 *              = { component_value } , curly_block ;
 *
 * declaration_list_core
 *              = { S } , ( [ declaration_core ] , [ ';' , declaration_list_core ]
 *                        | at_rule , declaration_list_core ,
 *                        ) ;
 *
 * declaration_core  = IDENT , { S } ,  ":", { component_value } , [ !important ] ;
 *
 * !important   = '!' , { S } , "important" , { S } ;
 *
 * component_value
 *              = ( preserved_token | curly_block | round_block | square_block
 *                | function_block ) ;
 *
 * curly_block  = '{' , { component_value } , '}' ;
 * round_block  = '(' , { component_value } , ')' ;
 * square_block = '[' , { component_value } , ']' ;
 * function_block
 *              = FUNCTION , { component_value } , ')' ;
 *
 * </pre> This parser parses the following syntax:
 * <pre>
 * stylesheet   = { S | CDO | CDC | qualified_rule | style_rule} ;
 *
 * operator     = ( '/' | ',' ) , { S } ;
 *
 * combinator   = ( '+' | '&gt;' | '~' ) , { S } ;
 *
 * unary_operator
 *              = ( '-' | '+' ) ;
 *
 * property     = IDENT , { S } ;
 *
 * style_rule   = [ selector_group ] , "{" , declaration_list , "}" ;
 *
 * selector_group
 *              = selector , { "," , { S }, selector } ;
 *
 * selector     = simple_selector ,
 *                { ( combinator , selector
 *                  | { S }, [ [ combinator ] , selector ]
 *                  )
 *                } ;
 *
 * simple_selector
 *              = universal_selector | type_selector | id_selector
 *                | class_selector | pseudoclass_selector | attribute_selector ;
 * universal_selector   = '*' ;
 * type_selector        = IDENT ;
 * id_selector          = HASH ;
 * class_selector       = "." , IDENT ;
 * pseudoclass_selector = ":" , IDENT ;
 * attribute_selector   = "[" , IDENT
 *                            , [ ( "=" | "~=" | "|=" ) , ( IDENT | STRING ) ],
 *                        "]" ;
 *
 * declaration_list
 *              = { S } , [ declaration ] , [ ';' , declaration_list ] ;
 *
 * declaration  = IDENT , { S } ,  ":", { preserved_token } ;
 *
 * term         = [ unary_operator] ,
 *                ( NUMBER , { S } | PERCENTAGE , { S }  | LENGTH , { S }
 *                | EMS , { S } | EXS , { S } | ANGLE , { S } | TIME , { S }
 *                | FREQ , { S } | STRING , { S } | IDENT , { S } | URI , { S }
 *                | hexcolor | function
 *                ) ;
 *
 * function     = FUNCTION , { S } , expr , ')' , { S } ;
 * expr         = term , { [ operator ] , term } ;
 *
 * hexcolor     = HASH , { S } ;
 *                (* There is a constraint on the color that it must
 *                   have either 3 or 6 hex-digits (i.e., [0-9a-fA-F])
 *                   after the "#"; e.g., "#000" is OK, but "#abcd" is not. *)
 *
 * </pre>
 * <p>
 * References:
 * <ul>
 * <li><a href="http://www.w3.org/TR/2014/CR-css-syntax-3/#parsing">
 * CSS Syntax Module Level 3, Chapter 5. Parsing</a></li>
 * <li><a href="https://www.w3.org/TR/CSS2/grammar.html#q25.0">
 * W3C CSS2, Appendix G.1 Grammar of CSS 2.1</a></li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssParser {

    private List<ParseException> exceptions;

    @Nonnull
    public Stylesheet parseStylesheet(@Nonnull URL css) throws IOException {
        try (Reader in = new BufferedReader(new InputStreamReader(css.openConnection().getInputStream()))) {
            return parseStylesheet(in);
        }
    }

    @Nonnull
    public Stylesheet parseStylesheet(@Nonnull URI css) throws IOException {
        return parseStylesheet(css.toURL());
    }

    @Nonnull
    public Stylesheet parseStylesheet(@Nonnull String css) throws IOException {
        return parseStylesheet(new StringReader(css));
    }

    @Nonnull
    public Stylesheet parseStylesheet(Reader css) throws IOException {
        exceptions = new ArrayList<>();
        CssTokenizer tt = new CssStreamTokenizer(css);
        return parseStylesheet(tt);
    }

    /**
     * Parses a declaration list.
     *
     * @param css A stylesheet
     * @return the declaration list
     * @throws IOException if parsing fails
     */
    @Nonnull
    public List<Declaration> parseDeclarationList(@Nonnull String css) throws IOException {
        return CssParser.this.parseDeclarationList(new StringReader(css));
    }

    /**
     * Parses a declaration list.
     *
     * @param css A stylesheet
     * @return the declaration list
     * @throws IOException if parsing fails
     */
    @Nonnull
    public List<Declaration> parseDeclarationList(Reader css) throws IOException {
        exceptions = new ArrayList<>();
        CssTokenizer tt = new CssStreamTokenizer(css);
        try {
            return parseDeclarationList(tt);
        } catch (ParseException ex) {
            exceptions.add(ex);
        }
        return new ArrayList<>();
    }

    private Stylesheet parseStylesheet(CssTokenizer tt) throws IOException {
        List<StyleRule> styleRules = new ArrayList<>();
        while (tt.nextNoSkip() != CssTokenType.TT_EOF) {
            try {
                switch (tt.current()) {
                    case CssTokenType.TT_S:
                    case CssTokenType.TT_CDC:
                    case CssTokenType.TT_CDO:
                        break;
                    case CssTokenType.TT_AT_KEYWORD: {
                        tt.pushBack();
                        AtRule r = parseAtRule(tt);
                        if (r != null) {
                            // FIXME don't throw at-rules away!
                            //   rulesets.add(r);
                        }
                        break;
                    }
                    default: {
                        tt.pushBack();
                        // FIXME parse qualified rules instead of style rule
                        StyleRule r = parseStyleRule(tt);
                        if (r != null) {
                            styleRules.add(r);
                        }
                        break;
                    }
                }
            } catch (ParseException e) {
                exceptions.add(e);
            }
        }
        return new Stylesheet(styleRules);
    }

    public List<ParseException> getParseExceptions() {
        return exceptions;
    }

    private void skipWhitespace(CssTokenizer tt) throws IOException, ParseException {
        while (tt.current() == CssTokenType.TT_S//
                || tt.current() == CssTokenType.TT_CDC//
                || tt.current() == CssTokenType.TT_CDO) {
            tt.nextNoSkip();
        }
    }

    private AtRule parseAtRule(CssTokenizer tt) throws IOException, ParseException {
        // FIXME implement this properly
        if (tt.nextNoSkip() != CssTokenType.TT_AT_KEYWORD) {
            throw new ParseException("AtRule: At-Keyword expected.", tt.getLineNumber());
        }
        String atKeyword = tt.currentString();
        tt.nextNoSkip();
        while (tt.current() != CssTokenType.TT_EOF
                && tt.current() != '{'//
                && tt.current() != ';') {
            tt.pushBack();
            parseComponentValue(tt);
            tt.nextNoSkip();
        }
        if (tt.current() == ';') {
            return new AtRule(atKeyword, null, null);
        } else {
            tt.pushBack();
            parseCurlyBlock(tt);
            return new AtRule(atKeyword, null, null);
        }
    }

    private Object parseComponentValue(CssTokenizer tt) throws IOException, ParseException {
        switch (tt.nextNoSkip()) {
            case '{':
                tt.pushBack();
                parseCurlyBlock(tt);
                break;
            case '(':
                tt.pushBack();
                parseRoundBlock(tt);
                break;
            case '[':
                tt.pushBack();
                parseSquareBlock(tt);
                break;
            case CssTokenType.TT_FUNCTION:
                tt.pushBack();
                parseFunctionBlock(tt);
                break;
            default:
                tt.pushBack();
                parsePreservedToken(tt);
                break;
        }
        return null;
    }

    private Object parseCurlyBlock(CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() != '{') {
            throw new ParseException("CurlyBlock: '{' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != '}') {
            tt.pushBack();
            // FIXME do something with component value
            parseComponentValue(tt);
        }
        if (tt.current() != '}') {
            throw new ParseException("CurlyBlock: '}' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        return null;
    }

    private Object parseRoundBlock(CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() != '(') {
            throw new ParseException("RoundBlock: '(' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != ')') {
            tt.pushBack();
            // FIXME do something with component value
            parseComponentValue(tt);
        }
        if (tt.nextNoSkip() != ')') {
            throw new ParseException("RoundBlock: ')' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        return null;
    }

    private Object parseSquareBlock(CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() != '[') {
            throw new ParseException("SquareBlock: '[' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != ']') {
            tt.pushBack();
            // FIXME do something with component value
            parseComponentValue(tt);
        }
        if (tt.nextNoSkip() != ']') {
            throw new ParseException("SquareBlock: ']' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        return null;
    }

    private Object parseFunctionBlock(CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() != CssTokenType.TT_FUNCTION) {
            throw new ParseException("FunctionBlock: function expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        if (tt.nextNoSkip() != ')') {
            throw new ParseException("FunctionBlock: ')' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        return null;
    }

    private Object parsePreservedToken(CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() == CssTokenType.TT_EOF) {
            throw new ParseException("CssToken: token expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        return null;
    }

    private Object parseQualifiedRule(CssTokenizer tt) throws IOException, ParseException {
        // Fixme don't throw away a qualified rule
        tt.nextNoSkip();
        skipWhitespace(tt);
        while (tt.current() != CssTokenType.TT_EOF
                && tt.current() != '{'//
                && tt.current() != ';') {
            tt.pushBack();
            parseComponentValue(tt);
            tt.nextNoSkip();
        }
        tt.pushBack();
        parseCurlyBlock(tt);
        return null;
    }

    private StyleRule parseStyleRule(CssTokenizer tt) throws IOException, ParseException {
        SelectorGroup selectorGroup;
        tt.nextNoSkip();
        skipWhitespace(tt);
        if (tt.current() == '{') {
            tt.pushBack();
            selectorGroup = new SelectorGroup(new UniversalSelector());
        } else {
            tt.pushBack();
            selectorGroup = parseSelectorGroup(tt);
        }
        skipWhitespace(tt);
        if (tt.nextNoSkip() != '{') {
            throw new ParseException("StyleRule: '{' expected.", tt.getLineNumber());
        }
        List<Declaration> declarations = parseDeclarationList(tt);
        tt.nextNoSkip();
        skipWhitespace(tt);
        if (tt.current() != '}') {
            throw new ParseException("StyleRule: '}' expected.", tt.getLineNumber());
        }
        return new StyleRule(selectorGroup, declarations);
    }

    private SelectorGroup parseSelectorGroup(@Nonnull CssTokenizer tt) throws IOException, ParseException {
        List<Selector> selectors = new ArrayList<>();
        selectors.add(parseSelector(tt));
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != '{') {
            skipWhitespace(tt);
            if (tt.current() != ',') {
                throw new ParseException("SelectorGroup: ',' expected.", tt.getLineNumber());
            }
            tt.nextNoSkip();
            skipWhitespace(tt);
            tt.pushBack();
            selectors.add(parseSelector(tt));
        }
        tt.pushBack();
        return new SelectorGroup(selectors);
    }

    private Selector parseSelector(@Nonnull CssTokenizer tt) throws IOException, ParseException {
        SimpleSelector simpleSelector = parseSimpleSelector(tt);
        Selector selector = simpleSelector;
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != '{' && tt.current() != ',') {

            boolean potentialDescendantCombinator = false;
            if (tt.current() == CssTokenType.TT_S) {
                potentialDescendantCombinator = true;
                skipWhitespace(tt);
            }
            if (tt.current() == CssTokenType.TT_EOF
                    || tt.current() == '{' || tt.current() == ',') {
                break;
            }
            switch (tt.current()) {
                case '>':
                    selector = new ChildCombinator(simpleSelector, parseSelector(tt));
                    break;
                case '+':
                    selector = new AdjacentSiblingCombinator(simpleSelector, parseSelector(tt));
                    break;
                case '~':
                    selector = new GeneralSiblingCombinator(simpleSelector, parseSelector(tt));
                    break;
                default:
                    tt.pushBack();
                    if (potentialDescendantCombinator) {
                        selector = new DescendantCombinator(simpleSelector, parseSelector(tt));
                    } else {
                        selector = new AndCombinator(simpleSelector, parseSelector(tt));
                    }
                    break;
            }
        }
        tt.pushBack();
        return selector;
    }

    private SimpleSelector parseSimpleSelector(CssTokenizer tt) throws IOException, ParseException {
        tt.nextNoSkip();
        skipWhitespace(tt);
        try {
            switch (tt.current()) {
                case '*':
                    return new UniversalSelector();
                case CssTokenType.TT_IDENT:
                    return new TypeSelector(tt.currentString());
                case CssTokenType.TT_HASH:
                    return new IdSelector(tt.currentString());
                case '.':
                    if (tt.nextNoSkip() != CssTokenType.TT_IDENT) {
                        throw new ParseException("SimpleSelector: identifier expected.", tt.getLineNumber());
                    }
                    return new ClassSelector(tt.currentString());
                case ':':
                    tt.pushBack();
                    return parsePseudoClassSelector(tt);
                case '[':
                    tt.pushBack();
                    return parseAttributeSelector(tt);
                default:
                    throw new ParseException("SimpleSelector: SimpleSelector expected instead of \"" + tt.currentString() + "\". Line " + tt.getLineNumber() + ".", tt.getStartPosition());
            }
        } catch (ParseException e) {
            exceptions.add(e);
            return new SelectNothingSelector();
        }
    }

    private PseudoClassSelector parsePseudoClassSelector(CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() != ':') {
            throw new ParseException("Pseudo Class Selector: ':' expected of \"" + tt.currentString() + "\". Line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                && tt.current() != CssTokenType.TT_FUNCTION) {
            throw new ParseException("Pseudo Class Selector: identifier or function expected instead of \"" + tt.currentString() + "\". Line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }

        if (tt.current() == CssTokenType.TT_FUNCTION) {
            String ident = tt.currentString();
            List<CssToken> terms = new ArrayList<>();
            while (tt.nextNoSkip() != CssTokenType.TT_EOF
                    && tt.current() != ')') {
                terms.add(new CssToken(tt.current(), tt.currentString(), tt.currentNumber(),
                        tt.getLineNumber(), tt.getStartPosition(), tt.getEndPosition()));
            }
            return new FunctionPseudoClassSelector(ident, terms);
        } else {

            return new SimplePseudoClassSelector(tt.currentString());
        }
    }

    private AbstractAttributeSelector parseAttributeSelector(CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() != '[') {
            throw new ParseException("AttributeSelector: '[' expected.", tt.getLineNumber());
        }
        if (tt.nextNoSkip() != CssTokenType.TT_IDENT) {
            throw new ParseException("AttributeSelector: Identifier expected. Line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        String attributeName = tt.currentString();
        AbstractAttributeSelector selector;
        switch (tt.nextNoSkip()) {
            case '=':
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw new ParseException("AttributeSelector: identifier, string or number expected. Line:"+ tt.getLineNumber()+".", tt.getStartPosition());
                }
                selector = new EqualsMatchSelector(attributeName, tt.currentString());
                break;
            case CssTokenType.TT_INCLUDE_MATCH:
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw new ParseException("AttributeSelector: identifier, string or number expected. Line:"+ tt.getLineNumber()+".", tt.getStartPosition());
                }
                selector = new IncludeMatchSelector(attributeName, tt.currentString());
                break;
            case CssTokenType.TT_DASH_MATCH:
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw new ParseException("AttributeSelector: identifier, string or number expected. Line:"+ tt.getLineNumber()+".", tt.getStartPosition());
                }
                selector = new DashMatchSelector(attributeName, tt.currentString());
                break;
            case CssTokenType.TT_PREFIX_MATCH:
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw new ParseException("AttributeSelector: identifier, string or number expected. Line:"+ tt.getLineNumber()+".", tt.getStartPosition());
                }
                selector = new PrefixMatchSelector(attributeName, tt.currentString());
                break;
            case CssTokenType.TT_SUFFIX_MATCH:
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw new ParseException("AttributeSelector: identifier, string or number expected. Line:"+ tt.getLineNumber()+".", tt.getStartPosition());
                }
                selector = new SuffixMatchSelector(attributeName, tt.currentString());
                break;
            case CssTokenType.TT_SUBSTRING_MATCH:
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw new ParseException("AttributeSelector: identifier, string or number expected. Line:"+ tt.getLineNumber()+".", tt.getStartPosition());
                }
                selector = new SubstringMatchSelector(attributeName, tt.currentString());
                break;
            case ']':
                selector = new ExistsMatchSelector(attributeName);
                tt.pushBack();
                break;
            default:
                throw new ParseException("AttributeSelector: operator expected. Line " + tt.getLineNumber() + ".", tt.getStartPosition());

        }
        if (tt.nextNoSkip() != ']') {
            throw new ParseException("AttributeSelector: ']' expected.", tt.getLineNumber());
        }
        return selector;
    }

    @Nonnull
    private List<Declaration> parseDeclarationList(CssTokenizer tt) throws IOException, ParseException {
        List<Declaration> declarations = new ArrayList<>();

        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != '}') {
            switch (tt.current()) {
                case CssTokenType.TT_IDENT:
                    tt.pushBack();
                    try {
                        declarations.add(parseDeclaration(tt));
                    } catch (ParseException e) {
                        // We could not parse the current declaration.
                        // However we will try to parse the next declarations.
                        exceptions.add(e);
                    }
                    break;
                case ';':
                case CssTokenType.TT_S:
                    break;
                default:
                    throw new ParseException(//
                            "Declaration List: declaration or at-rule expected. Line"//
                            + tt.getLineNumber() + ".", //
                            tt.getStartPosition());

            }
        }

        tt.pushBack();
        return declarations;

    }

    private Declaration parseDeclaration(CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() != CssTokenType.TT_IDENT) {
            throw new ParseException(//
                    "Declaration: property name expected. Line "//
                    + tt.getLineNumber() + ".",//
                    tt.getStartPosition());
        }
        String property = tt.currentString();
        int startPos = tt.getStartPosition();
        tt.nextNoSkip();
        skipWhitespace(tt);
        if (tt.current() != ':') {
            throw new ParseException("Declaration: ':' expected instead of \"" + tt.currentString() + "\". Line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        List<CssToken> terms = parseTerms(tt);
        int endPos = terms.isEmpty() ? tt.getStartPosition() : terms.get(terms.size() - 1).getEndPos();

        return new Declaration(property, terms, startPos, endPos);

    }

    @Nonnull
    private List<CssToken> parseTerms(CssTokenizer tt) throws IOException, ParseException {
        List<CssToken> terms = new ArrayList<>();
        tt.nextNoSkip();
        skipWhitespace(tt);
        tt.pushBack();
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && //
                tt.current() != '}' && tt.current() != ';') {
            switch (tt.current()) {
                case CssTokenType.TT_CDC:
                case CssTokenType.TT_CDO:
                    break;
                case CssTokenType.TT_BAD_URI:
                    throw new ParseException("Terms: Bad URI in line " + tt.getLineNumber() + ".", tt.getStartPosition());
                case CssTokenType.TT_BAD_STRING:
                    throw new ParseException("Terms: Bad String in line " + tt.getLineNumber() + ".", tt.getStartPosition());
                default:
                    terms.add(new CssToken(tt.current(), tt.currentString(), tt.currentNumber(),
                            tt.getLineNumber(),tt.getStartPosition(), tt.getEndPosition()));
                    break;
            }
        }
        tt.pushBack();

        /*if (terms.isEmpty()) {
            throw new ParseException("Terms: Terms expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }*/
        return terms;
    }

    private CssToken parseTerm(CssTokenizer tt) throws IOException, ParseException {
        switch (tt.nextNoSkip()) {
            case CssTokenType.TT_EOF:
                throw new ParseException("Term: Term expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
            case CssTokenType.TT_BAD_URI:
                throw new ParseException("Term: Bad URI in line " + tt.getLineNumber() + ".", tt.getStartPosition());
            case CssTokenType.TT_BAD_STRING:
                throw new ParseException("Term: Bad String in line " + tt.getLineNumber() + ".", tt.getStartPosition());
            default:
                return new CssToken(tt.current(), tt.currentString(), tt.currentNumber(),
                        tt.getLineNumber(),tt.getStartPosition(), tt.getEndPosition());
        }
    }
}
