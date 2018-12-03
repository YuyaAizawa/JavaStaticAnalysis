/* @(#)WordListConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.collection.SetWrapper;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.PatternConverter;

/**
 * WordSetConverter converts an ImmutableObservableSet of Strings into a
 * String.
 * <p>
 The word list is actually a "set of space separated tokens", as specified in
 HTML 5 and in XML Schema Part 2.
 <p>
 * The word list converter coalesces duplicate entries if they have the same
 * Unicode NFD form. The tokens are sorted using their Unicode NFD form.
 * <p>
 * References:
 * <ul>
 * <li><a href="https://dev.w3.org/html5/spec-preview/common-microsyntaxes.html#set-of-space-separated-tokens">
 * HTML 5, Common Microsyntaxes, Space-separated tokens
 * </a></li>
 * <li><a href="https://www.w3.org/TR/xmlschema-2/#token">
 * XML Schema Part 2, Built-in datatypes, Derived datatypes, CssToken
 * </a></li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssWordSetConverter implements Converter<ImmutableSet<String>> {

    private final PatternConverter formatter = new PatternConverter("{0,list,{1,word}|[ \n\r\t]+}", new CssConverterFactory());

    public final static Comparator<String> NFD_COMPARATOR
            = (o1, o2) -> Normalizer.normalize(o1, Normalizer.Form.NFD).compareTo(
                    Normalizer.normalize(o2, Normalizer.Form.NFD));

    @Override
    public <TT extends ImmutableSet<String>> void toString(Appendable out, IdFactory idFactory, @Nonnull TT value) throws IOException {
        Set<String> tokens = new TreeSet<>(NFD_COMPARATOR);
        tokens.addAll(new SetWrapper<>(value));
        Object[] v = new Object[tokens.size() + 1];
        v[0] = value.size();
        value.copyInto(v, 1);
        formatter.toString(out, v);
    }

    @Override
    public ImmutableSet<String> fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        ImmutableSet<String> l =  ImmutableSet.ofArray(v,1,(int)v[0]);
        return l;
    }

    @Override
    public ImmutableSet<String> getDefaultValue() {
        return ImmutableSet.emptySet();
    }

}
