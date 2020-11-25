package io
import java.io.{BufferedReader, FileReader, Reader}

import org.apache.commons.csv.{CSVFormat, CSVRecord}

import collection.JavaConverters._

case class CSVOptions(columnNames: Seq[String])

trait Closer {
  def close(): Unit
}

class CloserRegistry {
  def attach(c: Closer): Unit = {}

  def closeAll(): Unit = {}
}

object Closer {
  def withCloser[T](f: CloserRegistry => T): T = {
    val reg = new CloserRegistry()
    try {
      f(reg)
    } finally {
      reg.closeAll()
    }
  }
}

class ReaderCloser(reader: Reader) extends Closer {
  var closed = false

  override def close(): Unit = {
    if (!closed) {
      reader.close()
      closed = true
    }
  }
}

object IOSource {
  type CSVFileOutput = (Iterator[CSVRecord], Closer)

  def fromCSVFile(fileName: String, options: CSVOptions): CSVFileOutput = {
    val in = new BufferedReader(new FileReader(fileName))
    val closer = new ReaderCloser(in)
    val parser = CSVFormat.DEFAULT
      .withHeader(options.columnNames: _*)
      .withSkipHeaderRecord()
      .parse(in)
      .getRecords
    val iter = parser.iterator().asScala
    (iter, closer)
  }
}
