package com.chain33.connection

import link.luyu.protocol.link
import cn.chain33.javasdk.client._
import com.chain33.constant.Constant._
import com.chain33.util._
import com.chain33.contract.ContractCall
import com.fasterxml.jackson.databind.ObjectMapper

import java.nio.charset.StandardCharsets

sealed case class Connection(properties: Map[String, AnyRef]) extends link.Connection {
  private val url        = properties.get("chainUrl").toString
  private val encryption = properties.get("encryption").toString
  private val client     = new RpcClient(url)
  client.setUrl(url)

  val matchMap = Map {
    Type.SEND_TRANSACTION -> { (data: Array[Byte], callback: link.Connection.Callback) =>
      {
        // TODO
        val tx = client.submitTransaction(new String(data))
        call(tx, callback, () => tx.getBytes)
      }
    }
    Type.CALL_TRANSACTION -> { (data: Array[Byte], callback: link.Connection.Callback) =>
      {
        val contractCall = Connection.OBJECT_MAPPER.readValue(data, classOf[ContractCall])
        // TODO
        val jsonResult = client.callEVMAbi(contractCall.sender, contractCall.data)
        call(jsonResult, callback, () => jsonResult.toString.getBytes)
      }
    }

    Type.GET_TRANSACTION_RECEIPT -> { (data: Array[Byte], callback: link.Connection.Callback) =>
      {
        val receipt = client.queryTransaction(data.toString) // TODO
        call(receipt, callback, () => Utils.toByteArray(receipt))
      }
    }
    Type.GET_ABI -> { (data: Array[Byte], callback: link.Connection.Callback) =>
      {
        val abi = client.queryEVMABIInfo(data.toString, "storage") // TODO
        call(abi, callback, () => abi.toString.getBytes(StandardCharsets.UTF_8))
      }
    }

    Type.GET_BLOCK_NUMBER -> { (data: Array[Byte], callback: link.Connection.Callback) =>
      {
        val header = client.getLastHeader
        call(header, callback, () => Utils.longToBytes(header.getHeight))
      }
    }

    Type.GET_BLOCK_BY_HASH -> { (data: Array[Byte], callback: link.Connection.Callback) =>
      {
        val appBlock = client.getBlockByHashes(Array { data.toString }, true)
        call(appBlock, callback, () => Utils.toByteArray(new InternalBlock(appBlock.get(0))))
      }
    }

    Type.GET_BLOCK_BY_NUMBER -> { (data: Array[Byte], callback: link.Connection.Callback) =>
      {
        val blockNumber = Utils.bytesToLong(data)
        val blk         = client.getBlocks(blockNumber, blockNumber, true)
        call(blk, callback, () => Utils.toByteArray(new InternalBlock(blk.get(0).getBlock)))
      }
    }

  }

  override def start(): Unit = {}

  override def stop(): Unit = {}

  // chain33 rpc api call
  override def asyncSend(
      path: String,
      `type`: Int,
      data: Array[Byte],
      callback: link.Connection.Callback
  ): Unit = matchMap.getOrElse(
    `type`,
    (_: Array[Byte], callback: link.Connection.Callback) => callback.onResponse(Result.ERROR, "Unrecognized type of " + `type`, null)
  )(data, callback)

  private def call(obj: Object, callback: link.Connection.Callback, data: () => Array[Byte]): Unit = {
    if (obj == null) {
      callback.onResponse(Result.ERROR, Msg.ERROR, null)
    } else {
      callback.onResponse(Result.SUCCESS, Msg.SUCCESS, data())
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
