package test

import scala.scalajs.js
import scala.scalajs.js.Dynamic.global
import scala.scalajs.js.Undefined
import scala.scalajs.js.Any

object Test extends ScalaJSTest {

  //global.____.generate_an_error
  
  describe("Part1") {
    it("should show an exception") {

      global.nonExistent()

      expect(true).toEqual(true)
    }

    it("should succeed") {
      expect(true).toEqual(true)
    }
  }

  describe("Part1") {
    it("should show a failure") {
      expect(true).toEqual(false)
      expect(true).toEqual(true)
    }

    it("should succeed") {
      expect(true).toEqual(true)
    }
  }
}