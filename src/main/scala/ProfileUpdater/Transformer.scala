package main.scala.ProfileUpdater

import scala.xml._
import scala.xml.transform._

class Transformer(changes : Seq[FieldPermissionChange]) {
	val changeForField = scala.collection.mutable.Map[String, FieldPermissionChange]()
	changes.map(c => changeForField(c.objectName+"."+c.fieldName) = c)

	val fieldsTransformed = scala.collection.mutable.Set[String]()

	def getTransformedNode(n : Node, inputFieldName : String, children : NodeSeq) = {
		if (! changeForField.contains(inputFieldName)) {
			n
		} else {
			val change = changeForField(inputFieldName)
			val setWrite = change.setWrite.getOrElse((children \\ "editable").text)
			val setRead = change.setRead.getOrElse((children \\ "readable").text)
			val fullName = change.objectName + "." + change.fieldName
			fieldsTransformed += fullName
			val result = XMLBuilder.fieldPermissionXML(setWrite, fullName, setRead)

			result
		}
	}

	def getAllNotTransformedFields = changeForField.keysIterator.filter {
		k => ! fieldsTransformed.contains(k)
	}

	def getNodesForAllNotTransformedFields = {
		getAllNotTransformedFields.map { f =>			
			XMLBuilder.fieldPermissionXMLForChange(changeForField(f))
		}.toList
	}
}

object ProfileXMLMerger {
	def merge(profile : Node, fieldPermissions : NodeSeq) = {
		profile match {
			case Elem(prefix, label, attribs, scope, child @ _*) =>
				Elem(prefix, label, attribs, scope, mergeNodeSeqs(child, fieldPermissions) : _*)
		}
	}

	private def mergeNodeSeqs(ns1 : NodeSeq, ns2 : NodeSeq) : NodeSeq = {
		if (ns1.isEmpty) {
			ns2
		} else if (ns2.isEmpty) {
			ns1
		} else {
			if (nodeBefore(ns1.head, ns2.head)) {
				ns1.head ++ mergeNodeSeqs(ns1.tail, ns2)
			} else {
				ns2.head ++ mergeNodeSeqs(ns1, ns2.tail)
			}
		}
	}

	private def nodeBefore(n1 : Node, n2 : Node) = {
		n1 match {
			case Elem(prefix1, "fieldPermissions", attribs1, scope1, child1 @ _*) => {
				n2 match {
					case Elem(prefix2, "fieldPermissions", attribs2, scope2, child2 @ _*) => {
						(child1 \\ "field").text < (child2 \\ "field").text
					}
					case Elem(prefix2, label2, attribs2, scope2, child2 @ _*) => {
						"fieldPermissions" < label2
					}
					case other => true
				}
			}
			case Elem(prefix1, label1, attribs1, scope1, child1 @ _*) => {
				n2 match {
					case Elem(prefix2, label2, attribs2, scope2, child2 @ _*) => {
						label1 < label2
					}
					case other => true
				}
			}
			case other => true
		}
	}
}

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
