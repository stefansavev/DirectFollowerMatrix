package mining.stages

import mining.Utils

trait OutputFormattingArgs{
  def apply(s: Seq[((String, String), Int)]): String
}

object CSVFormattingArgs extends OutputFormattingArgs{
  def apply(s: Seq[((String, String), Int)]): String = {
    Utils.formatFinalResults(s)
  }
}

object GraphVizFormattingArgs extends OutputFormattingArgs{
  def apply(s: Seq[((String, String), Int)]): String = {
    Utils.formatToDot(s)
  }
}

object FormattingFactory {
  type Formatter = Seq[((String, String), Int)] => String
  def fromArgs(args: OutputFormattingArgs): Formatter = {
    args.apply
  }
}
