package kz.greetgo.sandbox.db.register_impl.migration.error;

import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class CommonErrorFileWriter implements ErrorFile {
  private long lineNo = 0;
  private PrintWriter printWriter;

  public CommonErrorFileWriter(File outputErrorFile) throws IOException {
    printWriter = new PrintWriter(new FileWriterWithEncoding(outputErrorFile, "UTF-8"));
  }

  @Override
  public void appendErrorLine(String line) {
    lineNo++;
    printWriter.write(String.valueOf(lineNo) + ". " + line + "\n");
  }
}
