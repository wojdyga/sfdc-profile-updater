package main.scala.ProfileUpdater

import scala.xml._
import scala.io.Source

case class FieldPermissionChange(objectName : String, fieldName : String, setRead : Option[String], setWrite : Option[String])

class ObjectImporter(srcDirPath : String, objectName : String) {
	private def getAllAvailableFields = {
		(XML.loadString(Source.fromFile(srcDirPath + "/objects/" + objectName + ".object").mkString) \\ "fields").filter(nonRequiredFields).toList
	}

	private def nonRequiredFields(n : Node) : Boolean = {
		val typeMD = (n \\ "type").headOption.getOrElse(<type></type>).text.equals("MasterDetail")
		val required = (n \\ "required").text.equals("true")

		! required && ! typeMD
	}

	def getAllReadableChanges : List[FieldPermissionChange] = {
		getAllAvailableFields.map(n => nodeToReadable(n))
	}

	private def nodeToReadable(n : Node) : FieldPermissionChange = {
		FieldPermissionChange(objectName, (n \\ "fullName").text, Some("true"), None)
	}

	def getAllWriteableChanges : List[FieldPermissionChange] = {
		List()
	}

	def getAllReadWrite : List[FieldPermissionChange] = {
		List()
	}
}
