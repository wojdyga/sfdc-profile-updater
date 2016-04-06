package main.scala.ProfileUpdater

import scala.io.Source
import scala.xml._
import scala.xml.transform._

class ChangePermissions(transformer : Transformer) extends RewriteRule {
	override def transform(n : Node) = n match {
		case Elem(prefix, "fieldPermissions", _attrs, _scope, children @ _*) => {
			transformer.getTransformedNode(n, (children \\ "field").text, children)
		}
		case other => other
	}
}