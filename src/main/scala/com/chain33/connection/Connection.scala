package com.chain33.connection

import link.luyu.protocol.common.STATUS
import link.luyu.protocol.link
import cn.chain33.javasdk.client._
import com.chain33.constant.Constant._
import com.chain33.util._
import com.chain33.contract.ContractCall
import com.citahub.cita.protocol.core.DefaultBlockParameterName
import com.citahub.cita.protocol.core.methods.request.Call
import com.fasterxml.jackson.databind.ObjectMapper

import java.nio.charset.StandardCharsets

class Connection(val url: String) extends link.Connection {
  private val client = new RpcClient(url)
  client.setUrl(url)

  override def start(): Unit = {}

  override def stop(): Unit = {}

  override def asyncSend(
      path: String,
      `type`: Int,
      data: Array[Byte],
      callback: link.Connection.Callback
  ): Unit = {
    `type` match {
      case Type.SEND_TRANSACTION =>
        // TODO
        val appSendTransaction = citAj.appSendRawTransaction(new String(data)).send
        if (appSendTransaction.getError != null) {
          val message = appSendTransaction.getError.getMessage
          callback.onResponse(TransactionConstant.Result.ERROR, message, null)
        } else callback.onResponse(Result.SUCCESS, "Success", appSendTransaction.getSendTransactionResult.getHash.getBytes)

      case Type.CALL_TRANSACTION =>
        val contractCall = Connection.OBJECT_MAPPER.readValue(data, classOf[ContractCall])
        val call         = new Call(contractCall.sender, contractCall.contract, contractCall.data)
        // TODO
        val result = citAj.appCall(call, DefaultBlockParameterName.PENDING).send.getValue
        callback.onResponse(Result.SUCCESS, "Success", result.getBytes)

      case Type.GET_TRANSACTION_RECEIPT =>
        val txHash  = data.toString
        val receipt = client.queryTransaction(txHash) // TODO
        callback.onResponse(Result.SUCCESS, "Success", Utils.toByteArray(receipt))

      case Type.GET_ABI =>
        val abi = client.queryEVMABIInfo(path, "storage").toString
        callback.onResponse(Result.SUCCESS, "Success", abi.getBytes(StandardCharsets.UTF_8))
      case Type.GET_BLOCK_NUMBER =>
        callback.onResponse(Result.SUCCESS, "Success", Utils.longToBytes(client.getLastHeader.getHeight))
      case Type.GET_BLOCK_BY_HASH =>
        val blockHash = path // TODO
        val appBlock  = client.getBlockByHashes(Array { blockHash }, true).get(0)
        val blk       = new InternalBlock(appBlock)
        callback.onResponse(STATUS.OK, "Success", Utils.toByteArray(blk))
      case Type.GET_BLOCK_BY_NUMBER =>
        val blockNumber = Utils.bytesToLong(data)
        val blk         = client.getBlocks(blockNumber, blockNumber, true).get(0)
        callback.onResponse(STATUS.OK, "Success", Utils.toByteArray(new InternalBlock(blk.getBlock)))
      case _ => callback.onResponse(Result.ERROR, "Unrecognized type of " + `type`, null)

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
  private var OBJECT_MAPPER = new ObjectMapper
}
