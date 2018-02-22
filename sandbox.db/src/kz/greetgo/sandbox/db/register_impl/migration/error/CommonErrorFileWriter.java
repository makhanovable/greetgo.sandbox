package kz.greetgo.sandbox.db.register_impl.migration.error;

import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class CommonErrorFileWriter implements ErrorFile {
  private long lineNo = 0;
  private PrintWriter printWriter;

  private static final long FILE_FLUSH_TIME = 1000;

  public CommonErrorFileWriter(File outputErrorFile) throws IOException {
    printWriter = new PrintWriter(new FileWriterWithEncoding(outputErrorFile, "UTF-8"));
  }

  @Override
  public void appendErrorLine(String line) {
    lineNo++;
    printWriter.write(String.valueOf(lineNo) + ". " + line + "\n");

    this.flush();
  }

  @Override
  public void finish() {
    this.flush();

    printWriter.close();
  }

  private void flush() {
    if (lineNo > FILE_FLUSH_TIME) {
      printWriter.flush();
      lineNo = 0;
    }
  }
}
