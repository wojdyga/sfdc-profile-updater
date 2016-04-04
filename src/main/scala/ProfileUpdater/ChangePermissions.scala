package main.scala.ProfileUpdater

import scala.io.Source
import scala.xml._
import scala.xml.transform._

object XMLBuilder {
	def fieldPermissionXML(editable : String, fullName : String, readable : String) = {
	    <fieldPermissions>
	        <editable>{editable}</editable>
	        <field>{fullName}</field>
	        <readable>{readable}</readable>
	    </fieldPermissions>
	}

	def fieldPermissionXMLForChange(change : FieldPermissionChange) = {
		val setWrite = change.setWrite.getOrElse("false")
		val setRead = change.setRead.getOrElse("false")
		val fullName = change.objectName + "." + change.fieldName

		fieldPermissionXML(setWrite, fullName, setRead)
	}
}

class Transformer(var changes : Seq[FieldPermissionChange]) {
	def extractChangesUpTo(fieldNameFromFile : Option[String]) = {
		val (toCreate, other) = changes.partition(c => {
			def objectName = c.objectName
			def fieldName = c.fieldName
			def fullName = s"$objectName.$fieldName"

			fieldNameFromFile match {
				case Some(name) => fullName > name
				case None => true
			}
		})

		val result = toCreate.map(c => XMLBuilder.fieldPermissionXMLForChange(c))
		changes = other

		result
	}

	def applyChangeIfNeeded(n : Node, fieldName : String, children : NodeSeq) = {
		val objectName = changes.head.objectName
		val fieldName = changes.head.fieldName
		val fullName = s"$objectName.$fieldName"

		if (fieldName.equals(fullName)) {
			val change = changes.head
			val setWrite = change.setWrite.getOrElse((children \\ "editable").text)
			val setRead = change.setRead.getOrElse((children \\ "readable").text)

			val result = XMLBuilder.fieldPermissionXML(setWrite, fullName, setRead)
			changes = changes.tail 

			result
		} else if (fieldName > fullName) {
			extractChangesUpTo(Some(fieldName))
		} else {
			n
		}
	}
}

class ChangePermissions(changes : Seq[FieldPermissionChange]) extends RewriteRule {
	val transformer = new Transformer(changes)

	override def transform(n : Node) = n match {
		case Elem(_prefix, "classAccesses", _attrs, _scope, _) => {
			n
		}
		case Elem(_prefix, "custom", _attrs, _scope, _) => {
			n
		}
		case Elem(_prefix, "fieldPermissions", _attrs, _scope, children @ _*) => {
			transformer.applyChangeIfNeeded(n, (children \\ "field").text, children)
		}
		case Elem(_prefix, "loginIpRanges", _attrs, _scope, _) => {
			transformer.extractChangesUpTo(None)
		}
		case Elem(_prefix, "objectPermissions", _attrs, _scope, _) => {
			transformer.extractChangesUpTo(None)
		}
		case Elem(_prefix, "pageAccesses", _attrs, _scope, _) => {
			transformer.extractChangesUpTo(None)
		}
		case Elem(_prefix, "tabVisibilities", _attrs, _scope, _) => {
			transformer.extractChangesUpTo(None)
		}
		case Elem(_prefix, "userLicense", _attrs, _scope, _) => {
			transformer.extractChangesUpTo(None)
		}
		case Elem(_prefix, "userPermissions", _attrs, _scope, _) => {
			transformer.extractChangesUpTo(None)
		}
		case other => other
	}
}