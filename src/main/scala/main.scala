/*
 * Tool to update, set and clear salesforce.com profiles and permission sets
 * https://github.com/wojdyga/sfdc-profile-updater
 */
package main.scala.ProfileUpdater

import scala.io.Source
import scala.xml._
import scala.xml.transform._

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
					} else if (config.mode.equals("clearEntries")) {
						oi => oi.getClearEntries
					} else if (config.mode.equals("setAllNoAccess")) {
						oi => oi.getAllNoAccessChanges
					} else {
						oi => List()
					}
				}

				val oldXML = loadProfile(config)

				val fieldChanges = config.objectNames.sorted
					.map(on => new ObjectImporter(config.srcDir, on))
					.flatMap(oi => modeOperator(oi))

				val transformer = new Transformer(fieldChanges)

				val rewriteRule : RewriteRule = {
					if (config.mode.equals("clearEntries")) {
						new ClearPermissions(transformer)
					} else {
						new ChangePermissions(transformer)
					}
				}

				val newXMLProfile = new RuleTransformer(rewriteRule).transform(oldXML).head

				val allNodes = {
					if (config.mode.equals("clearEntries")) {
						newXMLProfile
					} else {
						ProfileXMLMerger.merge(newXMLProfile, transformer.getNodesForAllNotTransformedFields)
					}
				}

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
