package test.sourceMaps

import scala.scalajs.js

trait SourceMaps extends js.Object {
  val SourceMapConsumer: js.Dynamic = ???
}

object SourceMaps {

  implicit class SourceMapsOps(sourceMaps: SourceMaps) {

    def createConsumer(map: SourceMap): SourceMapConsumer =
      js.Dynamic.newInstance(sourceMaps.SourceMapConsumer)(map)
        .asInstanceOf[SourceMapConsumer]

  }
}