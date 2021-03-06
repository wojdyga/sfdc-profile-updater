package main.scala.ProfileUpdater

import scala.io.Source
import scala.xml._

case class FieldPermissionChange(objectName : String, fieldName : String, setRead : Option[String], setWrite : Option[String])

class ObjectImporter(srcDirPath : String, objectName : String) {
	private def getAllAvailableFields = {
		(XML.loadString(Source.fromFile(srcDirPath + "/objects/" + objectName + ".object").mkString) \\ "fields").toList
	}

	private def nonRequiredFields(n : Node) : Boolean = {
		val typeMD = (n \\ "type").headOption.getOrElse(<type></type>).text.equals("MasterDetail")
		val required = (n \\ "required").text.equals("true")

		! required && ! typeMD
	}

	def getAllReadableChanges : List[FieldPermissionChange] = {
		getAllAvailableFields.filter(nonRequiredFields).map(n => nodeToReadable(n))
	}

	private def nodeToReadable(n : Node) : FieldPermissionChange = {
		FieldPermissionChange(objectName, (n \ "fullName").text, Some("true"), None)
	}

	def nonFormulaField(n : Node) : Boolean = {
		(n \\ "formula").isEmpty
	}

	def nonSummaryField(n: Node): Boolean = {
		!(n \\ "type").headOption.getOrElse(<type></type>).text.equals("Summary")
	}

	def isFieldWriteable(n : Node) : Boolean = {
		nonFormulaField(n) && nonSummaryField(n)
	}
	def nodeToWriteable(n : Node) : FieldPermissionChange = {
		FieldPermissionChange(objectName, (n \ "fullName").text, Some("true"), Some("true"))
	}

	def getAllWriteableChanges : List[FieldPermissionChange] = {
		getAllAvailableFields.filter(nonRequiredFields).filter(isFieldWriteable).map(nodeToWriteable)
	}

	def getAllReadWriteChanges : List[FieldPermissionChange] = {
		getAllAvailableFields.filter(nonRequiredFields).map { n =>
			if (isFieldWriteable(n)) {
				nodeToWriteable(n)
			} else {
				nodeToReadable(n)
			}
		}
	}

	def getAllNoAccessChanges : List[FieldPermissionChange] = {
		getAllAvailableFields.filter(nonRequiredFields).map(nodeToNoAccess)
	}

	private def nodeToNoAccess(n : Node) = {
		FieldPermissionChange(objectName, (n \ "fullName").text, Some("false"), Some("false"))
	}

	private def nodeToField(n : Node) : FieldPermissionChange =
		FieldPermissionChange(objectName, (n \ "fullName").text, None, None)

	def getClearEntries : List[FieldPermissionChange] = {
		getAllAvailableFields map nodeToField
	}
}
