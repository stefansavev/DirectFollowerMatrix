package mining.stages

import mining.stages.OutputUtils.getUniqueIds

import scala.collection.mutable

object OutputUtils {
  def getUniqueIds(s: Seq[((String, String), Int)]): Seq[String] = {
    s.flatMap { case ((k1, k2), _) => Seq(k1, k2) }.distinct.sorted
  }
}

trait OutputFormattingArgs {
  def apply(s: Seq[((String, String), Int)]): String
}

object CSVFormattingArgs extends OutputFormattingArgs {

  def apply(s: Seq[((String, String), Int)]): String = {
    val uniqueIds = getUniqueIds(s)
    val sep = ","
    val newLine = "\n"
    val lookup = s.toMap.withDefaultValue(0)

    val results = uniqueIds
      .map(k1 => {
        val data = uniqueIds.map(k2 => s"${lookup(k1, k2)}").mkString(sep)
        s"${k1}${sep}${data}"
      })
    val header = uniqueIds.mkString(sep)
    header + newLine + results.mkString(newLine)
  }
}

object GraphVizFormattingArgs extends OutputFormattingArgs {

  def apply(s: Seq[((String, String), Int)]): String = {
    val sb = new mutable.StringBuilder()
    val uniqueIdsWithIdx = OutputUtils.getUniqueIds(s).zipWithIndex
    val id2Index = uniqueIdsWithIdx.toMap
    val lookup = s.toMap.withDefaultValue(0)
    def quote(x: String): String = {
      val q = "\""
      q + x.replace(q, "\\").replace("\\", "\\\\") +
        q
    }
    def nodeName(i: Int): String = {
      "node" + i.toString
    }

    sb.append(
      "/* Visualize at: https://dreampuf.github.io/GraphvizOnline/ */\n"
    )
    sb.append("digraph {\n")
    uniqueIdsWithIdx.foreach {
      case (name, idx) => {
        sb.append(s" ${quote(nodeName(idx))} [label=${quote(name)}]\n")
      }
    }
    s.foreach {
      case ((k1, k2), _) => {
        sb.append(
          s"  ${nodeName(id2Index(k1))} -> ${nodeName(id2Index(k2))} [label=${quote(lookup(k1, k2).toString)}]\n"
        )
      }
    }
    sb.append("}")
    sb.toString()
  }
}

object OutputFactory {
  type Formatter = Seq[((String, String), Int)] => String
  def fromArgs(args: OutputFormattingArgs): Formatter = {
    args.apply
  }
}
