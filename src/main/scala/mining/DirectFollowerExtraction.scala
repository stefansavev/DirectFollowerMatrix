package mining

import io.{Closer, IOKeyedCounter}
import mining.stages.{CSVFactory, CSVFileArgs, CSVFormattingArgs, CSVToRecordConverter, EventPartitioner}
import mining.stages.{FormattingFactory, NoTraceFilter, OutputFormattingArgs, SlidingEvents, SlidingEventsArgs}
import mining.stages.{SlidingWindowCounter, TraceFilterArgs, TraceFilterFactory}
import models.Trace

class CommandLineArgs(
                       val fileArgs: CSVFileArgs,
                       val filterArgs: TraceFilterArgs,
                       val slidingEventArgs: SlidingEventsArgs,
                       val formatArgs: OutputFormattingArgs)

object CommandLineArgs {
  def fromArgs(args: Array[String]): CommandLineArgs = {
    new CommandLineArgs(CSVFileArgs(args(1)), NoTraceFilter, SlidingEventsArgs(true),
      CSVFormattingArgs)
  }
}

/**
 * Extracts the direct follower matrix from a log.
 * Parses each line of the log file as an `Event` and builds the `Trace`s from the events.
 * Then counts all direct follower relations and prints them to the command line.
 */
object DirectFollowerExtraction extends App {

  def run(parsedArgs: CommandLineArgs): String = {
    Closer.withCloser{ cleanup =>
      // Create parameterized objects
      val csvReader = CSVFactory.fromArgs(parsedArgs.fileArgs)
      val eventsFilter = TraceFilterFactory.fromArgs(parsedArgs.filterArgs)
      val slidingProcessor = SlidingEvents.fromArgs(parsedArgs.slidingEventArgs)
      val formatter = FormattingFactory.fromArgs(parsedArgs.formatArgs)

      // Get an iterator and a closer. Closer if for cleaning up
      val (csvRecordsIter, closer) = csvReader()
      cleanup.add(closer) // this is GoLang style

      // build events out of CSV Records
      val eventsIter = csvRecordsIter.map(CSVToRecordConverter.csvRecordToEvent)

      // partition events into Traces. Potentially can work in external memory
      val partitionedEvents = EventPartitioner.partition(eventsIter)

      // filter traces if necessary
      val filteredEvents: Iterator[Trace] = eventsFilter(partitionedEvents)

      // perform counting
      val counts = SlidingWindowCounter.count(slidingProcessor, filteredEvents)

      // prepare output
      formatter(counts)
    }
  }
  val result = run(CommandLineArgs.fromArgs(args))
  // scalastyle:off
  println(result)
  // scalastyle:on
}
