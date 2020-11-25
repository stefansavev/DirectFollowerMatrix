package mining.stages

import io.{CSVOptions, Closer, IOSource}
import org.apache.commons.csv.CSVRecord

case class CSVFileArgs(fileName: String, columnNames: Seq[String])

object InputFactory {
  type InputGenerator = () => (Iterator[CSVRecord], Closer)
  def fromArgs(args: CSVFileArgs): InputGenerator = {
    (
        () =>
          IOSource.fromCSVFile(args.fileName, CSVOptions(args.columnNames))
    )
  }
}
