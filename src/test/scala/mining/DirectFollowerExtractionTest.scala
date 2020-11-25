package mining
import mining.stages.{
  CSVFileArgs,
  CSVFormattingArgs,
  GraphVizFormattingArgs,
  InBetweenDatesTraceFilter,
  NoTraceFilter,
  OutputFormattingArgs,
  RecordConverterArgs,
  SlidingEventsArgs,
  TraceFilterArgs
}
import org.scalatest.FunSpec
import org.scalatest._
import prop._

object TestUtils {
  def getTestArgs(
      fileName: String,
      traceFilter: TraceFilterArgs,
      formattingArgs: OutputFormattingArgs
  ): CommandLineArgs = {
    new CommandLineArgs(
      CSVFileArgs(fileName, Header.getNames),
      RecordConverterArgs(
        Header.CaseID,
        Header.Activity,
        Header.Start,
        Header.defaultStartDateFormat
      ),
      traceFilter,
      SlidingEventsArgs(true),
      formattingArgs
    )
  }

  def stripBeginAndEndNewLine(s: String): String = {
    val newLine = "\n"
    s.stripPrefix(newLine).stripSuffix(newLine)
  }
}

class DirectFollowerExtractionTest extends FunSpec {
  it("test with customer file") {
    val expectedOutput =
      """
        |/* Visualize at: https://dreampuf.github.io/GraphvizOnline/ */
        |digraph {
        | "node0" [label="Functional escalation"]
        | "node1" [label="Incident classification"]
        | "node2" [label="Incident closure"]
        | "node3" [label="Incident logging"]
        | "node4" [label="Initial diagnosis"]
        | "node5" [label="Investigation and diagnosis"]
        | "node6" [label="Resolution and recovery"]
        | "node7" [label="[End]"]
        | "node8" [label="[Start]"]
        |  node1 -> node4 [label="2000"]
        |  node5 -> node2 [label="97"]
        |  node4 -> node2 [label="268"]
        |  node5 -> node6 [label="417"]
        |  node3 -> node1 [label="2000"]
        |  node8 -> node3 [label="2000"]
        |  node2 -> node7 [label="2000"]
        |  node4 -> node6 [label="1218"]
        |  node4 -> node0 [label="851"]
        |  node5 -> node4 [label="337"]
        |  node0 -> node5 [label="851"]
        |  node6 -> node2 [label="1635"]
        |}
        |""".stripMargin
    val file = getClass.getResource("/IncidentExample.csv").getPath()
    val args =
      TestUtils.getTestArgs(file, NoTraceFilter, GraphVizFormattingArgs)
    val output = DirectFollowerExtraction.run(args)
    assert(output == TestUtils.stripBeginAndEndNewLine(expectedOutput))
  }

  it("test csv output") {
    val expectedOutput =
      """
        |[End],[Start],step1,step2,step3
        |[End],0,0,0,0,0
        |[Start],0,0,1,0,0
        |step1,0,0,0,1,0
        |step2,0,0,0,0,1
        |step3,1,0,0,0,0
        |""".stripMargin
    val file = getClass.getResource("/3Lines.csv").getPath()
    val args = TestUtils.getTestArgs(file, NoTraceFilter, CSVFormattingArgs)
    val output = DirectFollowerExtraction.run(args)
    assert("\n" + output + "\n" == expectedOutput)
  }

  it("test with date filter") {
    val expectedOutput =
      """
        |/* Visualize at: https://dreampuf.github.io/GraphvizOnline/ */
        |digraph {
        | "node0" [label="[End]"]
        | "node1" [label="[Start]"]
        | "node2" [label="step1"]
        | "node3" [label="step2"]
        | "node4" [label="step3"]
        |  node4 -> node0 [label="1"]
        |  node1 -> node2 [label="1"]
        |  node2 -> node3 [label="1"]
        |  node3 -> node4 [label="1"]
        |}
        |""".stripMargin
    val file = getClass.getResource("/FilterTest.csv").getPath()
    val startDate = "2017/01/04 00:00:00.000"
    val endDate = "2019/01/04 00:00:00.000"
    val traceFilter = InBetweenDatesTraceFilter.fromStrings(
      Header.defaultStartDateFormat,
      startDate,
      endDate
    )
    val args = TestUtils.getTestArgs(file, traceFilter, GraphVizFormattingArgs)
    val output = DirectFollowerExtraction.run(args)
    assert("\n" + output + "\n" == expectedOutput)
  }
}

class TableTests extends PropSpec with TableDrivenPropertyChecks with Matchers {

  val examples =
    Table("filename", "0Lines", "1Line", "3Lines", "3LinesNotOrdered", "6Lines")

  property("each file should work") {
    forAll(examples) { (t: String) =>
      val file = getClass.getResource(s"/${t}.csv").getPath()
      val args =
        TestUtils.getTestArgs(file, NoTraceFilter, GraphVizFormattingArgs)
      val output = DirectFollowerExtraction.run(args)
      val outputFile = getClass.getResource(s"/${t}Output.csv").getPath()
      val expected = scala.io.Source.fromFile(outputFile).mkString
      assert(output == expected)
    }
  }
}
