package com.chain33.driver

import com.fasterxml.jackson.databind.ObjectMapper
import link.luyu.protocol.link.{Driver => BaseDriver, _}
import link.luyu.protocol.network.{Account, CallRequest, Events, Transaction}

case class Driver(val connection: Connection) extends BaseDriver {
  override def start(): Unit = {}

  override def stop(): Unit = {}

  override def getType: String = "chain33"

  override def listResources(callback: BaseDriver.ResourcesCallback): Unit = ???

  override def registerEvents(events: Events): Unit = {}

  override def call(
      account: Account,
      request: CallRequest,
      callback: BaseDriver.CallResponseCallback
  ): Unit = ???

  override def getSignatureType: String = "SM2_WITH_SM3"

  override def getBlockByHash(blockHash: String, callback: BaseDriver.BlockCallback): Unit = ???

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
