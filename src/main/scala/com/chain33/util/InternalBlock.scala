package com.chain33.util

import java.io.Serializable
import link.luyu.protocol.network.Block
import cn.chain33.javasdk.model.rpcresult.BlockResult

class InternalBlock(val appBlock: BlockResult) extends Serializable {
  var blockNumber: Long = appBlock.getHeight
  var hash: String      = appBlock.getHash
  var timeStamp: Long   = appBlock.getBlockTime.getTime

  def toBlock: Block = {
    val block = new Block
    block.setNumber(blockNumber)
    block.setHash(hash)
    block.setTimestamp(timeStamp)
    block.setBytes _ // mock
    block
  }
}
