package mining.stages

import io.{CSVOptions, Closer, IOSource}
import mining.Header
import org.apache.commons.csv.CSVRecord

case class CSVFileArgs(fileName: String)

object CSVFactory {
  type CSVProcessor = () => (Iterator[CSVRecord], Closer)
  def fromArgs(args: CSVFileArgs): CSVProcessor = { (() =>
    IOSource.fromCSVFile(args.fileName, CSVOptions(Header.getNames)))
  }
}
