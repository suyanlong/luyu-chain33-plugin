package util

import com.chain33.util.ChainUtils
import org.scalatest.{FlatSpec, Matchers}

import scala.language.postfixOps

class ChainUtilsSpec extends FlatSpec with Matchers {
  "ChainUtils" should "all test" in{
    ChainUtils.getVersion shouldEqual 2
    ChainUtils.getChainId.longValue() shouldEqual 1
    ChainUtils.getNonce nonEmpty
  }
}
