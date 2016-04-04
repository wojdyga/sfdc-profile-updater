/*
 * Tool to update, set and clear salesforce.com profiles and permission sets
 * https://github.com/wojdyga/sfdc-profile-updater
 */
package main.scala.ProfileUpdater

import scala.xml._
import scala.xml.transform._
import scala.io.Source

object ProfileUpdater {
	def main(args : Array[String]) = {
		ConfigurationParser.parse(args) match {
			case Some(config) =>
				if (config.mode.equals("copy")) {
					copy(config.srcDir + "/profiles/" + config.profileName + ".profile")
				} else if (config.mode.equals("setAllReadable")) {
					val profileFile = config.srcDir + "/profiles/" + config.profileName + ".profile"
					val oldXML = XML.loadString(Source.fromFile(profileFile).mkString)

					val fieldChanges = config.objectNames.sorted
						.map(on => new ObjectImporter(config.srcDir, on))
						.flatMap(oi => oi.getAllReadableChanges)

					println(new RuleTransformer(new ChangePermissions(fieldChanges)).transform(oldXML))
				} else if (config.mode.equals("setAllWriteable")) {

				}
			case None => {}
		}

		def copy(profileFile : String) {
			val newString = Source.fromFile(profileFile).mkString
			val xml = XML.loadString(newString)
			val output = xml map copyToOutput
			println(xml \\ "fieldPermissions")
		}

		def copyToOutput(n : Node) = { n match {
			case other @ _  => other
		}}
	}
}


