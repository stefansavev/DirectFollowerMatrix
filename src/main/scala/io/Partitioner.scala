package io

import scala.math.Ordering

trait PartitionProps[INPUT, PART_KEY, SORT_KEY, VALUE] {
  def getPartitionKey(inp: INPUT): PART_KEY
  def getSortKey(inp: INPUT): SORT_KEY
  def getValue(inp: INPUT): VALUE
}

case class Partition[PART_KEY, SORT_KEY, VALUE](
    key: PART_KEY,
    values: Seq[(SORT_KEY, VALUE)]
)

case class PartitionOptions(tmpDir: Option[String])

class Partitioner[INPUT, PART_KEY, SORT_KEY, VALUE](opts: PartitionOptions)(
    implicit ord: Ordering[SORT_KEY]
) {
  def partition(
      iter: Iterator[INPUT],
      props: PartitionProps[INPUT, PART_KEY, SORT_KEY, VALUE]
  ): Iterator[Partition[PART_KEY, SORT_KEY, VALUE]] = {

    val grouped = iter.toSeq.groupBy(key => props.getPartitionKey(key))
    def prepareValues(values: Seq[INPUT]): Seq[(SORT_KEY, VALUE)] = {
      values
        .map(input => (props.getSortKey(input), props.getValue(input)))
        .sortBy(_._1)(ord)
    }

    grouped
      .map(group => Partition(group._1, prepareValues(group._2)))
      .toIterator
  }
}
