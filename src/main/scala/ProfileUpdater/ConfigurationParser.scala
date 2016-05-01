package main.scala.ProfileUpdater

case class Config(srcDir : String = "./src", objectNames : Seq[String] = Seq(), profileName : String = "", mode : String = "copy")

object ConfigurationParser {
	def parser = new scopt.OptionParser[Config]("sfdc-profile-updater") {
		head("SFDC Profile Updater", "0.1")
		opt[String]('s', "srcDir") required() valueName("<dir>") action { (x, c) => c.copy(srcDir = x) } text("srcDir is a required property - directory name containing package.xml file")
		opt[Seq[String]]('o', "objectNames") required() valueName("<strings>") action { (x, c) => c.copy(objectNames = x) } text("objectNames is a required property - API object names")
		opt[String]('p', "profileName") required() valueName("<string>") action { (x, c) => c.copy(profileName = x) } text("profileName is a required property - API profile name")
		cmd("copy") action { (_, c) => c.copy(mode = "copy") } text("copy command") 
		cmd("setAllReadable") action { (_, c) => c.copy(mode = "setAllReadable") } text("setAllReadable sets all fields as readable (where appropriate)")
		cmd("setAllWriteable") action { (_, c) => c.copy(mode = "setAllWriteable") } text("setAllReadable sets all fields as writeable (where appropriate)")
		cmd("setAllReadWrite") action { (_, c) => c.copy(mode = "setAllReadWrite") } text("setAllReadWrite sets all fields as readable and writeable (where appropriate)")
		cmd("clearEntries") action { (_, c) => c.copy(mode = "clearEntries") } text("clearEntries delete entries for all fields for given objects")
	}

	def parse(args : Array[String]) = {
		parser.parse(args, Config())
	}
}