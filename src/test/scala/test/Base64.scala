package test

import scala.collection.mutable
import scala.scalajs.js.TypeError

object Base642 {

  val charToIntMap = 
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
      .map(_.toString)
      .zipWithIndex
      .toMap

  def base64(aChar: String) =
    charToIntMap.get(aChar)
      .getOrElse(throw new Error("Not a valid base 64 digit: " + aChar))
/*
  // A single base 64 digit can contain 6 bits of data. For the base 64 variable
  // length quantities we use in the source map spec, the first bit is the sign,
  // the next four bits are the actual value, and the 6th bit is the
  // continuation bit. The continuation bit tells us whether there are more
  // digits in this value following this digit.
  //
  //   Continuation
  //   |    Sign
  //   |    |
  //   V    V
  //   101011

  val VLQ_BASE_SHIFT = 5

  // binary: 100000
  val VLQ_BASE = 1 << VLQ_BASE_SHIFT

  // binary: 011111
  val VLQ_BASE_MASK = VLQ_BASE - 1

  // binary: 100000
  val VLQ_CONTINUATION_BIT = VLQ_BASE

  def fromVLQSigned(aValue: Int) = {
    var isNegative = (aValue & 1) == 1
    var shifted = aValue >> 1
    if (isNegative) -shifted else shifted
  }

  def decode(aStr: String) = {
    var i = 0
    val strLen = aStr.length
    var result = 0
    var shift = 0
    var continuation = false
    var digit = 0

    do {
      if (i >= strLen) {
        throw new Error("Expected more digits in base 64 VLQ value.");
      }
      digit = base64(aStr(i).toString)
      i += 1
      continuation = (digit & VLQ_CONTINUATION_BIT) != 0
      digit &= VLQ_BASE_MASK
      result += (digit << shift)
      shift += VLQ_BASE_SHIFT
    } while (continuation)

    (fromVLQSigned(result), aStr.substring(i))
  }
  * 
  */
}