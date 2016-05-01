package main.scala.ProfileUpdater

import scala.xml._
import scala.xml.transform._

class ClearPermissions(transformer : Transformer) extends RewriteRule {
  override def transform(n : Node) = n match {
    case Elem(prefix, "fieldPermissions", _attrs, _scope, children @ _*)
      if (transformer.isGoingToTransform((children \\ "field").text)) =>
        NodeSeq.Empty
    case other => other
  }
}