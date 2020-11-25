package mining

object Header {
  val CaseID = "Case ID"
  val Activity = "Activity"
  val Start = "Start"
  val Complete = "Complete"
  val Classification = "Classification"

  val defaultStartDateFormat = "yyyy/MM/dd HH:mm:ss.SSS"

  def getNames: Seq[String] = {
    Seq(CaseID, Activity, Start, Complete, Classification)
  }
}
