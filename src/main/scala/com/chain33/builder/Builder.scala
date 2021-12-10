package com.chain33.builder

import link.luyu.protocol.link._

import java.util
import com.chain33.driver
import com.chain33.connection

@LuyuPlugin("chain33")
class Builder extends PluginBuilder {

  override def newConnection(properties: util.Map[String, AnyRef]): Connection = {
    // TODO
    val url = properties.get("chainUrl").toString
    new connection.Connection(url)
  }

  override def newDriver(connection: Connection, properties: util.Map[String, AnyRef]): Driver =
    driver.Driver(connection)
}
