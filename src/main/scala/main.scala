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
			case Some(config) => {
				val modeOperator : ObjectImporter => List[FieldPermissionChange] = {
					if (config.mode.equals("setAllReadable")) {
						oi => oi.getAllReadableChanges
					} else if (config.mode.equals("setAllWriteable")) {
						oi => oi.getAllWriteableChanges
					} else if (config.mode.equals("setAllReadWrite")) {
						oi => oi.getAllReadWriteChanges
					} else {
						oi => List()
					}
				}

				val oldXML = loadProfile(config)

				val fieldChanges = config.objectNames.sorted
					.map(on => new ObjectImporter(config.srcDir, on))
					.flatMap(oi => modeOperator(oi))

				val transformer = new Transformer(fieldChanges)

				val newXMLProfile = new RuleTransformer(new ChangePermissions(transformer)).transform(oldXML).head

				val newNodes = transformer.getNodesForAllNotTransformedFields

				val allNodes = ProfileXMLMerger.merge(newXMLProfile, newNodes)

				printProfile(allNodes)
			}
			case None => {}
		}

		def loadProfile(c : Config) = {
			val profileFile = c.srcDir + "/profiles/" + c.profileName + ".profile"

			val xmlContent = Source.fromFile(profileFile).mkString match {
				case s if s.startsWith(xmlHeader) => s.stripPrefix(xmlHeader)
				case other => other
			}
			XML.loadString(xmlContent)
		}

		def printProfile(profileXML : Node) = {
			println(xmlHeader)
			val printer = new scala.xml.PrettyPrinter(240, 4)
			println(printer.format(profileXML))
		}

		def copy(profileFile : String) {
			val newString = Source.fromFile(profileFile).mkString
			val xml = XML.loadString(newString)
			val output = xml map copyToOutput
			println(output)
		}

		def copyToOutput(n : Node) = { n match {
			case other @ _  => other
		}}
	}

	private def xmlHeader = """<?xml version="1.0" encoding="UTF-8"?>"""
}
