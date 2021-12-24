package com.chain33.util

import java.math.BigInteger
import java.util.Random

object ChainUtils {
  private var version       = 2
  private var chainId       = BigInteger.valueOf(1L)
  private var currentHeight = new BigInteger("0")
  val ABI_ADDRESS           = "ffffffffffffffffffffffffffffffffff010001"
  private val RANDOM        = new Random

  def initChainDatas(chain_version: Int, chain_id: BigInteger): Unit = {
    version = chain_version
    chainId = chain_id
  }

  def getVersion: Int = version

  def getChainId: BigInteger = chainId

  def getNonce: String = System.nanoTime + String.valueOf(RANDOM.nextInt(100000) + 900000)

  def getCurrentHeight(service: Chain33): BigInteger = getCurrentHeight(service, 3)

  def setCurrentHeight(height: BigInteger): Unit = {
    currentHeight = height
  }

  def getCurrentHeight: BigInteger = currentHeight

  private def getCurrentHeight(service: Chain33, retry: Int) = {
    var count  = 0
    var height = 0L
    height = -1L
    while (count < retry) {
//      try height = service.appBlockNumber.send.getBlockNumber.longValue//TODO
      try height = 0 // TODO
      catch {
        case var8: Exception =>
          height = -1L
          println("getBlockNumber failed retry ..")
          try Thread.sleep(2000L)
          catch {
            case var7: Exception =>
              println("failed to get block number, Exception: $var7")
          }
      }
      count += 1
    }
    if (height == -1L) {
      println(s"Failed to get block number after $count  times")
    }
    BigInteger.valueOf(height)
  }

  def getValidUtilBlock(validUntilBlock: Int): BigInteger =
    getCurrentHeight.add(BigInteger.valueOf(validUntilBlock))

  def getValidUtilBlock(service: Chain33, validUntilBlock: Int): BigInteger =
    getCurrentHeight(service).add(BigInteger.valueOf(validUntilBlock))
}
