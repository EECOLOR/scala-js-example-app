package test.sourceMaps

import scala.scalajs.js
import scala.scalajs.js.Dynamic.global

trait SourceMap extends js.Object {
  val mappings: js.String = ???
}

object SourceMap {

  //TODO can we use the one from the source map library?
  val charToIntMap =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
      .map(_.toString)
      .zipWithIndex
      .toMap

  def base64(aChar: String) =
    charToIntMap.get(aChar)
      .getOrElse(throw new Error("Not a valid base 64 digit: " + aChar))

  var cachedSourceMaps = Map.empty[String, SourceMap]

  def forFile(fileName: String): SourceMap =
    cachedSourceMaps.getOrElse(fileName, {
      val json = global.eval(s"java.util.Scanner( new java.io.File('$fileName.map') ).useDelimiter('$$').next()").toString
      val sourceMap = global.eval(s"(function() { return $json; })()").asInstanceOf[SourceMap]

      cachedSourceMaps += fileName -> sourceMap
      sourceMap
    })

  implicit class SourceMapWrapper(s: SourceMap) {
    val mappings: String = s.mappings
    val lines = mappings.split(";").map(Line)

    case class Line(line: String) {
      val entries = line.split(",")

      lazy val firstColumn = {
        val firstLetterOfLine = entries.head.head.toString
        base64(firstLetterOfLine) >> 1
      }
    }
  }
}