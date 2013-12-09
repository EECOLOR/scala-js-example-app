package example

import scala.scalajs.js
import org.scalajs.dom._
import js.Dynamic.{ global => g }

object ScalaJSExample {
  def main(): Unit = {
    val paragraph = document.createElement("p")

    paragraph.innerHTML = "<strong>It workss!</strong>"
    console.log("soep")
    paragraph.addEventListener("onclick", { e: Event => console.log(e.target) })
    document.getElementById("playground").appendChild(paragraph)
  }
}
