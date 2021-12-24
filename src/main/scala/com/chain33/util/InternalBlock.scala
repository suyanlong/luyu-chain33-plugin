package com.chain33.util

import cn.chain33.javasdk.model.rpcresult.BlockResult
import link.luyu.protocol.network.Block
import java.io.Serializable

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
