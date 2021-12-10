package com.chain33.connection

import link.luyu.protocol.link
import cn.chain33.javasdk.client._

import com.chain33.constant.Constant._

class Connection(val url: String) extends link.Connection {
  private var client = new RpcClient(url)
  client.setUrl(url)

  override def start(): Unit = {}

  override def stop(): Unit = {}

  override def asyncSend(
      path: String,
      `type`: Int,
      data: Array[Byte],
      callback: link.Connection.Callback
  ): Unit = ???

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
