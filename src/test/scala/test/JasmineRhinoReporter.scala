package test

import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.Dynamic.global
import org.scalajs.dom._
import scala.scalajs.js.Date
import scala.scalajs.js.Undefined
import java.util.NoSuchElementException

/**
 * Console-oriented reporter for Rhino. Colorizes output based on the value of
 * the TERM env variable.
 *
 * Usage:
 *
 * jasmine.getEnv().addReporter(new jasmine.RhinoReporter());
 * jasmine.getEnv().execute();
 */
object JasmineRhinoReporter {

  if (!global.jasmine) {
    throw new Exception("jasmine library does not exist in global namespace!");
  }

  var started: js.Number = _
  var failCount: js.Number = 0
  var executeCount: js.Number = 0
  var passCount: js.Number = 0
  var suite: JasmineSuite = _

  def reportRunnerStarting(runner: JasmineRunner) = {
    console.log("Report Runner Starting...")
    started = now()
  }

  def failure = withColor(Color.RED, "x")
  def success = withColor(Color.GREEN, "+")

  def reportSpecResults(spec: JasmineSpec) = {

    val results = spec.results()
    val description = spec.description

    if (results.passed)
      print(s"   $success $description")
    else {
      error(s"  $failure $description")

      results.getItems.foreach(displayResult)
    }
  }

  def readSourceMapFor(fileName: String): SourceMap = {
    val json =
      global.eval(s"java.util.Scanner( new java.io.File('$fileName.map') ).useDelimiter('$$').next()").toString
    global.eval(s"(function() { return $json; })()").asInstanceOf[SourceMap]
  }

  trait SourceMap extends js.Object {
    val mappings: js.String = ???
  }

  implicit class SourceMapWrapper(s: SourceMap) {
    val mappings: String = s.mappings
    val lines = mappings.split(";").map(Line)

    case class Line(line: String) {
      val entries = line.split(",")

      lazy val firstColumn = {
        val firstLetterOfLine = entries.head.head.toString
        Base642.base64(firstLetterOfLine) >> 1
      }
    }
  }

  def displayResult(result: JasmineResultItem) = {
    if (result.`type` == "log") {
      print(s"    ${result.toString}");
    } else if (result.`type` == "expect") {
      if (!result.passed()) {
        try {
          val message = result.message

          val Pattern = """^(.+?) in ([^ ]+\.js) \(line (\d+)\)$""".r

          message match {
            case Pattern(originalMessage, fileName, lineNumber) =>

              val map = readSourceMapFor(fileName)

              val line = map.lines(lineNumber.toInt - 1)

              val smc = Dynamic.newInstance(global.SourceMapConsumer)(map)
              val x = js.Dictionary(
                "line" -> lineNumber.toInt,
                "column" -> line.firstColumn)

              val origPosition: js.Dictionary = smc.originalPositionFor(x)

              val newLineNumber = origPosition("line")
              val newSource = origPosition("source")
              val newMessage = s"$originalMessage \n    in $newSource (line $newLineNumber)"

              error(s"    $newMessage")
            case message =>
              error(s"    $message")
          }

        } catch {
          case t =>
            console.log(t.toString)
        }

        if (!result.trace.stack.isInstanceOf[Undefined]) {
          //error(result.trace.stack.asInstanceOf[String])
        }
      }
    }
  }

  def reportSpecStarting = { spec: JasmineSpec =>
    if (suite != spec.suite) {
      suite = spec.suite
      suite(suite.description);
    }
  }

  def reportSuiteResults(suite: JasmineSuite) = {
    var results = suite.results();
    if (results.passedCount != results.totalCount)
      info(s"${results.failedCount} of ${results.totalCount} failed");
    passCount += results.passedCount
    failCount += results.failedCount
    executeCount += results.totalCount
    print("");
  }

  def reportRunnerResults(runner: JasmineRunner) = {
    val executionTime = (now() - started) / 1000
    val msg = s"$executeCount spec(s), $failCount failure(s) in $executionTime"

    if (failCount > 0) {
      error("Failed: " + msg)
      throw new Error("soep")
    }
    else print(msg)
  }

  def log(str: js.String) = {
    print(str);
  }

  def now() = Date.now

  def suite(str: String) =
    print(str)

  def passed(str: String) =
    print(withColor(Color.GREEN, str))

  def failed(str: String) =
    error(withColor(Color.RED, str))

  def info(str: String) =
    print(withColor(Color.BLUE, str))

  def print(msg: String) =
    console.log(msg)

  def error(msg: String) =
    console.error(msg)

  def withColor(color: String, message: String) =
    color + message + Color.RESET

  def Color = getColors

  val ColorTerminals: Map[String, Colors] = Map("xterm" -> XtermColors)

  def getColors = {
    //concat with string, it's an object from the Rhino engine
    val term = "" + global.java.lang.System.getenv("TERM")
    ColorTerminals.get(term).getOrElse(NoColors)
  }

  def displayMethod(method: String) = console.log(">>> " + global.eval(s"ScalaJS.modules.test_JasmineRhinoReporter().$method"))
}

trait Colors {
  val RESET: String = ""
  val GREEN: String = ""
  val RED: String = ""
  val BLUE: String = ""
}

object XtermColors extends Colors {
  override val RESET = "\033[m"
  override val GREEN = "\033[32m"
  override val RED = "\033[31m"
  override val BLUE = "\033[34m"
}

object NoColors extends Colors