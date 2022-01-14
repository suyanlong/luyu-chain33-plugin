package com.chain33.driver

import com.fasterxml.jackson.databind.ObjectMapper
import link.luyu.protocol.common.STATUS
import link.luyu.protocol.link.{Driver => BaseDriver, _}
import link.luyu.protocol.network.{Account, CallRequest, Events, Resource, Transaction}

import java.util
import com.chain33.util._
import com.chain33.constant.Constant._

case class Driver(val connection: Connection) extends BaseDriver {
  override def start(): Unit = {}

  override def stop(): Unit = {}

  override def getType: String = "chain33"

  override def getSignatureType: String = "SM2_WITH_SM3"

  override def listResources(callback: BaseDriver.ResourcesCallback): Unit = {
    // TODO: mock
    val resources: util.Collection[Resource] = new util.HashSet[Resource]
    val resource: Resource                   = new Resource
    resource.setPath("payment.chain33.0x98ab1ed8d9b9d928b5ebab99a4132363d6880b12")
    resource.setType(getType)
    resource.setMethods(Array.empty)
    resources.add(resource)
    callback.onResponse(0, "Success", resources.toArray(new Array[Resource](resources.size)))
  }

  override def registerEvents(events: Events): Unit = {}

  override def call(
      account: Account,
      request: CallRequest,
      callback: BaseDriver.CallResponseCallback
  ): Unit = ???

  override def getBlockByHash(blockHash: String, callback: BaseDriver.BlockCallback): Unit = {
    connection.asyncSend(
      blockHash,
      Type.GET_BLOCK_BY_HASH,
      null,
      (errorCode: Int, message: String, responseData: Array[Byte]) => {
        if (errorCode != STATUS.OK) callback.onResponse(errorCode, message, null)
        else {
          val block = Utils.toObject(responseData).asInstanceOf[InternalBlock]
          callback.onResponse(STATUS.OK, "Success", block.toBlock)
        }
      }
    )
  }

  override def getBlockNumber: Long = ???

  override def getBlockByNumber(blockNumber: Long, callback: BaseDriver.BlockCallback): Unit = ???

  override def getTransactionReceipt(txHash: String, callback: BaseDriver.ReceiptCallback): Unit =
    ???

  override def sendTransaction(
      account: Account,
      request: Transaction,
      callback: BaseDriver.ReceiptCallback
  ): Unit = ???
}

object Driver {
  private val objectMapper = new ObjectMapper
}
