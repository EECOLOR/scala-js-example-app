package test

import scala.scalajs.js

trait JasmineSpecResults extends js.Object {

  def passed(): js.Boolean = ???

  def getItems():js.Array[JasmineResultItem] = ???
}