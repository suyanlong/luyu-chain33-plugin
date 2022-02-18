package com.chain33.util

import java.math.BigInteger
import java.util.Random

object ChainUtils {
  private val version        = 2
  private val chainId        = BigInteger.valueOf(1L)
  private val RANDOM         = new Random
  def getVersion: Int        = version
  def getChainId: BigInteger = chainId
  def getNonce: String       = System.nanoTime + String.valueOf(RANDOM.nextInt(100000) + 900000)
}
