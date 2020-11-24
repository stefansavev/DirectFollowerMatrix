package mining.stages

import java.time.ZonedDateTime

import io.{Partition, PartitionOptions, PartitionProps, Partitioner}
import mining.ZoneDateTimeOrdering
import models.{Event, Trace}

object EventPartitioner {
  object EventsPartitionProps
      extends PartitionProps[Event, String, ZonedDateTime, String] {
    override def getPartitionKey(event: Event): String = event.traceId

    override def getSortKey(event: Event): ZonedDateTime = event.start

    override def getValue(event: Event): String = event.activity
  }

  def partition(events: Iterator[Event]): Iterator[Trace] = {
    val opts = PartitionOptions(None)
    val partitioner = new Partitioner[Event, String, ZonedDateTime, String](
      opts
    )(ZoneDateTimeOrdering)
    val iter = partitioner.partition(events, EventsPartitionProps)
    def partToTrace(part: Partition[String, ZonedDateTime, String]): Trace = {
      val partEvents =
        part.values.map(values => Event(part.key, values._2, values._1))
      Trace(part.key, partEvents)
    }
    iter.map(partToTrace)
  }
}
