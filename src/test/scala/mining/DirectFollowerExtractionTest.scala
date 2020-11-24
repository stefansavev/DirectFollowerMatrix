package mining
import mining.stages.{CSVFileArgs, GraphVizFormattingArgs, NoTraceFilter, NoTraceFilter$, OutputFormattingArgs, SlidingEventsArgs}
import org.scalatest.FunSpec
import org.scalatest._
import prop._

object TestUtils {
  def defaultTestArgs(
      fileName: String,
      formattingArgs: OutputFormattingArgs): CommandLineArgs = {
    new CommandLineArgs(
      fileArgs = CSVFileArgs(fileName),
      filterArgs = NoTraceFilter,
      slidingEventArgs = SlidingEventsArgs(useStartEnd = true),
      formatArgs = formattingArgs)
  }

}

class DirectFollowerExtractionTest extends FunSpec {
  it("app test") {
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
    val args = TestUtils.defaultTestArgs(file, GraphVizFormattingArgs)
    val output = DirectFollowerExtraction.run(args)
    assert("\n" + output + "\n" == expectedOutput)
    print(output)
  }

}

class TableTests extends PropSpec with TableDrivenPropertyChecks with Matchers {

  val examples = Table("filename", "6Lines.csv")

  property("each file should work") {
    forAll(examples) { (t: String) =>
      val file = getClass.getResource(s"/${t}").getPath()
      val args = TestUtils.defaultTestArgs(file, GraphVizFormattingArgs)
      val output = DirectFollowerExtraction.run(args)
      println(t)
      println(output)
    }
  }
}
