/* @(#)XmlOutputFormatMixin.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javax.annotation.Nonnull;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.xml.XmlUtil;
import org.w3c.dom.Document;

/**
 * XmlOutputFormatMixin.
 * <p>
 * FIXME delete me
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface XmlOutputFormatMixin extends OutputFormat {

   
 
    default Document toDocument(@Nonnull Drawing drawing) throws IOException {
        return toDocument(drawing, drawing.getChildren());
    }
 
    Document toDocument( Drawing drawing,  Collection<Figure> selection) throws IOException;

    @Override
    default void write(@Nonnull File file, @Nonnull Drawing drawing) throws IOException {
        Document doc = toDocument(drawing);
        XmlUtil.write(file, doc);
    }

    @Override
    default void write(OutputStream out, @Nonnull Drawing drawing) throws IOException {
        write(out, drawing, drawing.getChildren());
    }

    default void write( OutputStream out,  Drawing drawing,  Collection<Figure> selection) throws IOException {
        Document doc = toDocument(drawing, selection);
        XmlUtil.write(out, doc);
    }


    default void write( Writer out,  Drawing drawing,  Collection<Figure> selection) throws IOException {
        Document doc = toDocument(drawing, selection);
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            t.transform(source, result);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }

}
