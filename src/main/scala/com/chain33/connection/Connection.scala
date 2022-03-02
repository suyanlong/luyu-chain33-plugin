package com.chain33.connection

import link.luyu.protocol.common.STATUS
import link.luyu.protocol.link
import cn.chain33.javasdk.client._
import com.chain33.constant.Constant._
import com.chain33.util._
import com.chain33.contract.ContractCall
import com.fasterxml.jackson.databind.ObjectMapper

import java.nio.charset.StandardCharsets

class Connection(val url: String) extends link.Connection {
  private val client = new RpcClient(url)
  client.setUrl(url)

  override def start(): Unit = {}

  override def stop(): Unit = {}

  // chain33 rpc api call
  override def asyncSend(
      path: String,
      `type`: Int,
      data: Array[Byte],
      callback: link.Connection.Callback
  ): Unit = {
    `type` match {
      case Type.SEND_TRANSACTION =>
        // TODO
        val tx = client.submitTransaction(new String(data))
        call(tx, callback, () => tx.getBytes)

      case Type.CALL_TRANSACTION =>
        val contractCall = Connection.OBJECT_MAPPER.readValue(data, classOf[ContractCall])
        // TODO
        val jsonResult = client.callEVMAbi(contractCall.sender, contractCall.data)
        call(jsonResult, callback, () => jsonResult.toString.getBytes)

      case Type.GET_TRANSACTION_RECEIPT =>
        val receipt = client.queryTransaction(data.toString) // TODO
        call(receipt, callback, () => Utils.toByteArray(receipt))
      case Type.GET_ABI =>
        val abi = client.queryEVMABIInfo(path, "storage")
        call(abi, callback, () => abi.toString.getBytes(StandardCharsets.UTF_8))

      case Type.GET_BLOCK_NUMBER =>
        val header = client.getLastHeader
        call(header, callback, () => Utils.longToBytes(header.getHeight))

      case Type.GET_BLOCK_BY_HASH =>
        val blockHash = data.toString
        val appBlock  = client.getBlockByHashes(Array { blockHash }, true)
        call(appBlock, callback, () => Utils.toByteArray(new InternalBlock(appBlock.get(0))))

      case Type.GET_BLOCK_BY_NUMBER =>
        val blockNumber = Utils.bytesToLong(data)
        val blk         = client.getBlocks(blockNumber, blockNumber, true)
        call(blk, callback, () => Utils.toByteArray(new InternalBlock(blk.get(0).getBlock)))

      case _ => callback.onResponse(Result.ERROR, "Unrecognized type of " + `type`, null)

    }
  }

  private def call(obj: Object, callback: link.Connection.Callback, data: () => Array[Byte]): Unit = {
    if (obj == null) {
      callback.onResponse(Result.ERROR, "Error", null)
    } else {
      callback.onResponse(STATUS.OK, "Success", data())
    }
  }

  override def subscribe(
      `type`: Int,
      data: Array[Byte],
      callback: link.Connection.Callback
  ): Unit = {
    `type` match {
      case Event.EVENT_NEW_BLOCK =>

      case Event.EVENT_RESOURCES_CHANGED =>

      case _ =>
    }
  }
}

object Connection {
  private val OBJECT_MAPPER = new ObjectMapper
}
