package test

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

trait JasmineResultItem extends js.Object {

  def `type`: js.String = ???
  def passed(): js.Boolean = ???
  val message: js.String = ???
  val trace: js.Dynamic = ???
  val e: js.Dynamic = ???
}