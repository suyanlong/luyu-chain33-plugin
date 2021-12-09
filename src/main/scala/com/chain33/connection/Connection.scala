package com.chain33.connection

import link.luyu.protocol.link

class Connection extends link.Connection {
  override def start(): Unit = ???

  override def stop(): Unit = ???

  override def asyncSend(
      path: String,
      `type`: Int,
      data: Array[Byte],
      callback: link.Connection.Callback
  ): Unit = ???

  override def subscribe(`type`: Int, data: Array[Byte], callback: link.Connection.Callback): Unit =
    ???
}
