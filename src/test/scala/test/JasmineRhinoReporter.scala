package test

import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.Dynamic.global
import scala.scalajs.js.JavaScriptException

import org.scalajs.dom.console

import test.jasmine.ExpectationResult
import test.jasmine.Result
import test.jasmine.Spec
import test.jasmine.Suite
import test.sourceMaps.SourceMap
import test.sourceMaps.SourceMaps

/**
 * Console-oriented reporter for Rhino. Colorizes output based on the value of
 * the TERM env variable.
 */
case object JasmineTestFailed extends RuntimeException

object JasmineRhinoReporter {

  def withSourceMapsAndJasmine(code: (SourceMaps, js.Dynamic) => Unit): Unit =
    global.require(js.Array("source-map-consumer", "jasmine"), code)

  def runTests(tests: js.Function0[Unit]) =
    withSourceMapsAndJasmine { (sourceMaps, jasmine) =>

      val reporter = new test.JasmineRhinoReporter(sourceMaps)

      try {
        tests()

        val jasmineEnv = jasmine.getEnv()
        jasmineEnv.addReporter(reporter.asInstanceOf[js.Any])
        jasmineEnv.updateInterval = 0
        jasmineEnv.execute()
      } catch {
        case JavaScriptException(exception) =>
          reporter.error("Problem executing code in tests: " + exception)
          reporter.displayStackTrace(exception.asInstanceOf[js.Dynamic].stack)
          throw JasmineTestFailed
      }
    }
}

class JasmineRhinoReporter(sourceMaps: SourceMaps) {

  private var started: js.Number = _
  private var failCount: js.Number = 0
  private var executeCount: js.Number = 0
  private var passCount: js.Number = 0
  private var currentSuite: Suite = _

  def reportRunnerStarting() = {
    print("Report Runner Starting...")
    print("")
    started = now()
  }

  def reportSpecStarting(spec: Spec) = {
    if (currentSuite != spec.suite) {
      currentSuite = spec.suite
      print(currentSuite.description);
    }
  }

  def reportSpecResults(spec: Spec) = {

    val results = spec.results()
    val description = spec.description

    if (results.passed)
      print(s"   $success $description")
    else {
      error(s"  $failure $description")

      results.getItems foreach displayResult
    }
  }

  def reportSuiteResults(suite: Suite) = {
    var results = suite.results();
    if (results.passedCount != results.totalCount)
      info(s"${results.failedCount} of ${results.totalCount} failed");

    passCount += results.passedCount
    failCount += results.failedCount
    executeCount += results.totalCount
    print("");
  }

  def reportRunnerResults() = {
    val executionTime = (now() - started) / 1000
    val msg = s"$executeCount spec(s), $failCount failure(s) in $executionTime"

    if (failCount > 0) {
      error("Failed: " + msg)
      throw JasmineTestFailed
    } else print(msg)
  }

  private def now() = js.Date.now

  private def print(msg: js.Any) =
    console.log(msg)

  private def info(str: String) =
    print(withColor(Color.BLUE, str))

  private def error(msg: js.Any) =
    console.error(msg)

  private def withColor(color: String, message: String) =
    color + message + Color.RESET

  val ColorTerminals: Map[String, Colors] = Map("xterm" -> XtermColors)

  lazy val Color = {
    //concat with string, it's an object from the Rhino engine
    val term = "" + global.java.lang.System.getenv("TERM")
    ColorTerminals.get(term).getOrElse(NoColors)
  }

  private def sanitizeMessage(message: String) = {

    val FilePattern = """^(.+?) ([^ ]+\.js) \(line (\d+)\).*?$""".r
    val StackTracePattern = """^(.+?) ([^ ]+\.js):(\d+).*?$""".r
    val EvalPattern = """^(.+?) in eval.+\(eval\).+?\(line \d+\).*?$""".r

    message match {
      case StackTracePattern(originalMessage, fileName, lineNumber) =>
        val (newSource, newLineNumber) =
          getSourceInfo(fileName, lineNumber.toInt)
        s"$originalMessage $newSource (line $newLineNumber)"

      case FilePattern(originalMessage, fileName, lineNumber) =>
        val (newSource, newLineNumber) =
          getSourceInfo(fileName, lineNumber.toInt)
        s"$originalMessage $newSource (line $newLineNumber)"

      case EvalPattern(originalMessage) => originalMessage
      case message => message
    }
  }

  private def getSourceInfo(fileName: String, lineNumber: Int) = {

    val sourceMap = SourceMap forFile fileName

    val smc = sourceMaps.createConsumer(sourceMap)
    val line = sourceMap.lines(lineNumber - 1)

    val generatedPosition =
      js.Dictionary(
        "line" -> lineNumber.toInt,
        "column" -> line.firstColumn)

    val originalPosition =
      smc.originalPositionFor(generatedPosition)

    (originalPosition("source"), originalPosition("line"))
  }

  private def failure = withColor(Color.RED, "x")
  private def success = withColor(Color.GREEN, "+")

  private def displayResult(result: Result) =
    result match {
      case r if (r.`type` == "log") =>
        print(s"    ${r.toString}");
      case r if (r.`type` == "expect") =>
        val result = r.asInstanceOf[ExpectationResult]
        if (!result.passed()) {

          val message = sanitizeMessage(result.message)
          error(s"    $message")

          displayStackTrace(result.trace.stack)
        }
    }

  private def displayStackTrace(stack: js.Any) =
    if (stack.isInstanceOf[js.String]) {
      try {

        val stackString = stack.asInstanceOf[String]
        val stackElements = stackString.split("\n")
        val interestingElements =
          stackElements.takeWhile(!_.contains("at eval"))

        interestingElements.foreach { s =>
          error(sanitizeMessage(s))
        }
      } catch {
        case JavaScriptException(exception) =>
          error("Failed to sanitize stack trace")
          error(exception)
          error(exception.asInstanceOf[Dynamic].stack)
      }
    }

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