package mining

import java.time.ZonedDateTime

import scala.collection.mutable
import scala.math.Ordering

object ZoneDateTimeOrdering extends Ordering[ZonedDateTime] {
  def compare(a: ZonedDateTime, b: ZonedDateTime): Int = a.compareTo(b)
}

object Utils {

  def getUniqueIds(s: Seq[((String, String), Int)]): Seq[String] = {
    s.flatMap { case ((k1, k2), _) => Seq(k1, k2) }.distinct.sorted
  }

  def formatToDot(s: Seq[((String, String), Int)]): String = {
    val sb = new mutable.StringBuilder()
    val uniqueIdsWithIdx = getUniqueIds(s).zipWithIndex
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
      "/* Visualize at: https://dreampuf.github.io/GraphvizOnline/ */\n")
    sb.append("digraph {\n")
    uniqueIdsWithIdx.foreach {
      case (name, idx) => {
        sb.append(s" ${quote(nodeName(idx))} [label=${quote(name)}]\n")
      }
    }
    s.foreach {
      case ((k1, k2), _) => {
        sb.append(
          s"  ${nodeName(id2Index(k1))} -> ${nodeName(id2Index(k2))} [label=${quote(
            lookup(k1, k2).toString)}]\n")
      }
    }
    sb.append("}")
    sb.toString()
  }
  def formatFinalResults(s: Seq[((String, String), Int)]): String = {
    val uniqueIds = getUniqueIds(s)
    val lookup = s.toMap.withDefaultValue(0)

    uniqueIds
      .map(k1 => {
        uniqueIds.map(k2 => s"${lookup(k1, k2)}").mkString(", ")
      })
      .mkString("\n")
  }
}
