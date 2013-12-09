package test

import scala.scalajs.js
import scala.scalajs.js.Dynamic.global
import scala.scalajs.js.Undefined
import scala.scalajs.js.Any

object Test extends ScalaJSTest {

  describe("soep") {
    it("blablabla") {

      global.soep()

      //expect(true).toEqual(false)
      expect(true).toEqual(true)
    }

    it("asdasd") {
      expect(true).toEqual(true)
    }
  }

  describe("soep2") {
    it("blablabla2") {
      expect(true).toEqual(false)
      expect(true).toEqual(true)
    }

    it("asdasd2") {
      expect(true).toEqual(true)
    }
  }
}