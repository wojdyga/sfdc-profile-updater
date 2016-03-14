import scala.xml._
import scala.io.Source

object ProfileUpdater {
	def main(args : Array[String]) = {
		val newString = Source.fromFile(args(0)).mkString
		val xml = XML.loadString(newString)
		val output = xml map copyToOutput
		println(output)
	}

	def copyToOutput(n : Node) = { n match {
		case other @ _  => other
	}}
}