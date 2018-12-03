/* @(#)TeddyController.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.teddy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.DataFormat;
import javax.annotation.Nonnull;
import org.jhotdraw8.app.AbstractDocumentOrientedViewController;
import org.jhotdraw8.app.action.Action;
import org.jhotdraw8.collection.HierarchicalMap;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.FXWorker;
import org.jhotdraw8.app.DocumentOrientedViewModel;

/**
 * TeddyController.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TeddyController extends AbstractDocumentOrientedViewController implements DocumentOrientedViewModel, Initializable {

  @FXML
  private URL location;

  private Node node;
  @FXML
  private ResourceBundle resources;
  @FXML
  private TextArea textArea;

  @Nonnull
  @Override
  public CompletionStage<Void> clear() {
    textArea.setText(null);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void clearModified() {
    modified.set(false);
  }

  @Override
  public Node getNode() {
    return node;
  }

  @Override
  protected void initActionMap(HierarchicalMap<String, Action> map) {
    // empty
  }

  @Override
  public void initView() {
    FXMLLoader loader = new FXMLLoader();
    loader.setController(this);

    try {
      node = loader.load(getClass().getResourceAsStream("TeddyProject.fxml"));
    } catch (IOException ex) {
      throw new InternalError(ex);
    }
  }

  /**
   * Initializes the controller class.
   */
  @FXML
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    textArea.textProperty().addListener((observable -> modified.set(true)));
  }

  @Nonnull
  @Override
  public CompletionStage<Void> print(PrinterJob job) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public CompletionStage<DataFormat> read(@Nonnull URI uri, DataFormat format, Map<? super Key<?>, Object> options, boolean append) {
    return FXWorker.supply(() -> {
      StringBuilder builder = new StringBuilder();
      char[] cbuf = new char[8192];
      try (Reader in = new InputStreamReader(new FileInputStream(new File(uri)), StandardCharsets.UTF_8)) {
        for (int count = in.read(cbuf, 0, cbuf.length); count != -1; count = in.read(cbuf, 0, cbuf.length)) {
          builder.append(cbuf, 0, count);
        }
      }
      return builder.toString();
    }).thenApply(value -> {
      if (append) {
        textArea.appendText(value);
      } else {
        textArea.setText(value);
      }
      return format;
    });
  }

  @Override
  public CompletionStage<Void> write(@Nonnull URI uri, DataFormat format, Map<? super Key<?>, Object> options) {
    final String text = textArea.getText();
    return FXWorker.run(() -> {
      try (Writer out = new OutputStreamWriter(new FileOutputStream(new File(uri)), StandardCharsets.UTF_8)) {
        out.write(text);
      }
    });
  }

}
