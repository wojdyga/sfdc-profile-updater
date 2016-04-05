package main.scala.ProfileUpdater

import scala.io.Source
import scala.xml._
import scala.xml.transform._

class ChangePermissions(transformer : Transformer) extends RewriteRule {
	override def transform(n : Node) = n match {
		/*case Elem(_prefix, "classAccesses", _attrs, _scope, _) => {
			n
		}
		case Elem(_prefix, "custom", _attrs, _scope, _) => {
			n
		}*/
		case Elem(prefix, "fieldPermissions", _attrs, _scope, children @ _*) => {
			transformer.getTransformedNode(n, (children \\ "field").text, children)
		}
		/*case Elem(_prefix, "loginIpRanges", _attrs, _scope, _) => {
			val l = transformer.extractAllNotTransformedFields(None)
			println("transform loginIpRanges", l)
			n :: l
		}
		case Elem(_prefix, "objectPermissions", _attrs, _scope, _) => {
			val l = transformer.extractAllNotTransformedFields(None)
			println("transform objectPermissions", l)
			n :: l
		}
		case Elem(_prefix, "pageAccesses", _attrs, _scope, _) => {
			val l = transformer.extractAllNotTransformedFields(None)
			println("transform pageAccesses", l)
			n :: l
		}
		case Elem(_prefix, "tabVisibilities", _attrs, _scope, _) => {
			val l = transformer.extractAllNotTransformedFields(None)
			println("transform tabVisibilities", l)
			n :: l		
		}
		case Elem(_prefix, "userLicense", _attrs, _scope, _) => {
			val l = transformer.extractAllNotTransformedFields(None)
			println("transform userLicense", l)
			n :: l
		}
		case Elem(_prefix, "userPermissions", _attrs, _scope, _) => {
			val l = transformer.extractAllNotTransformedFields(None)
			println("transform userPermissions", l)
			n :: l
		}*/
		case other => other
	}
}