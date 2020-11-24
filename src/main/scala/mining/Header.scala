package mining

object Header {
  val CaseID = "Case ID"
  val Activity = "Activity"
  val Start = "Start"
  val Complete = "Complete"
  val Classification = "Classification"

  def getNames: Seq[String] = {
    Seq(CaseID, Activity, Start, Complete, Classification)
  }
}
