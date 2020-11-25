package mining

import io.Closer
import mining.stages.{
  CSVFileArgs,
  CSVFormattingArgs,
  EventPartitioner,
  InputFactory,
  NoTraceFilter,
  OutputFactory,
  OutputFormattingArgs,
  RecordConverterArgs,
  RecordConverterFactory,
  SlidingEventsArgs,
  SlidingWindowCounter,
  TraceFilterArgs,
  TraceFilterFactory,
  WindowedEventsFactory
}
import models.Trace

class CommandLineArgs(
    val fileArgs: CSVFileArgs,
    val recordConverterArgs: RecordConverterArgs,
    val filterArgs: TraceFilterArgs,
    val slidingEventArgs: SlidingEventsArgs,
    val formatArgs: OutputFormattingArgs
)

object CommandLineArgs {
  def fromArgs(args: Array[String]): CommandLineArgs = {
    new CommandLineArgs(
      CSVFileArgs(args(1), Header.getNames),
      RecordConverterArgs(
        Header.CaseID,
        Header.Activity,
        Header.Start,
        Header.defaultStartDateFormat
      ),
      NoTraceFilter,
      SlidingEventsArgs(true),
      CSVFormattingArgs
    )
  }
}

/**
  * Extracts the direct follower matrix from a log.
  * Parses each line of the log file as an `Event` and builds the `Trace`s from the events.
  * Then counts all direct follower relations and prints them to the command line.
  */
object DirectFollowerExtraction extends App {

  def run(parsedArgs: CommandLineArgs): String = {
    Closer.withCloser { cleanup =>
      // Create parameterized objects
      val csvReader = InputFactory.fromArgs(parsedArgs.fileArgs)
      val recordConverter =
        RecordConverterFactory.fromArgs(parsedArgs.recordConverterArgs)
      val traceFilter = TraceFilterFactory.fromArgs(parsedArgs.filterArgs)
      val slidingProcessor =
        WindowedEventsFactory.fromArgs(parsedArgs.slidingEventArgs)
      val formatter = OutputFactory.fromArgs(parsedArgs.formatArgs)

      // Get an iterator and a closer. Closer is for cleaning up
      val (csvRecordsIter, closer) = csvReader()
      cleanup.attach(closer) // I prefer as much linear control flow as possible

      // Build events out of CSV Records
      val eventsIter = csvRecordsIter.map(recordConverter)

      // Partition events into Traces. Potentially can work in external memory
      val traces = EventPartitioner.partition(eventsIter)

      // Filter traces if necessary
      val filteredTraces = traceFilter(traces)

      // Perform counting
      val counts = SlidingWindowCounter.count(slidingProcessor, filteredTraces)

      // Prepare output
      formatter(counts)
    }
  }
  val result = run(CommandLineArgs.fromArgs(args))
  // scalastyle:off
  println(result)
  // scalastyle:on
}
