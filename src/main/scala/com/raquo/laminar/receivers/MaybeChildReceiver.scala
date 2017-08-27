package com.raquo.laminar.receivers

import com.raquo.dombuilder.generic.modifiers.Modifier
import com.raquo.laminar.nodes.{ReactiveChildNode, ReactiveComment, ReactiveElement}
import com.raquo.xstream.XStream
import org.scalajs.dom

class MaybeChildReceiver($maybeNode: XStream[MaybeChildReceiver.MaybeChildNode]) extends Modifier[ReactiveElement[dom.Element]] {

  // @TODO[Elegance] Unify this logic with ChildReceiver? Or not...

  override def applyTo(parentNode: ReactiveElement[dom.Element]): Unit = {
    val sentinelNode = new ReactiveComment("")
    var childNode: ReactiveChildNode[dom.Node] = sentinelNode

    // @TODO[Performance] In case of memory stream we're doing append(comment)+replace(node), but we could do just one append(node)
    parentNode.appendChild(childNode)
    parentNode.subscribe($maybeNode, onNext)

    @inline def onNext(maybeNewChildNode: MaybeChildReceiver.MaybeChildNode): Unit = {
      val newChildNode = maybeNewChildNode.getOrElse(sentinelNode)
      parentNode.replaceChild(childNode, newChildNode)
      childNode = newChildNode
    }
  }
}

object MaybeChildReceiver {

  type MaybeChildNode = Option[ReactiveChildNode[dom.Node]]

  def <--($maybeNode: XStream[MaybeChildReceiver.MaybeChildNode]): MaybeChildReceiver = {
    new MaybeChildReceiver($maybeNode)
  }
}