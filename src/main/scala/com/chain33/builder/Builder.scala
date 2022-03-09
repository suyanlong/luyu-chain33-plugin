package com.chain33.builder

import link.luyu.protocol.link._

import java.util
import com.chain33.driver
import com.chain33.connection
import scala.collection.JavaConversions._

@LuyuPlugin("chain33")
sealed class Builder extends PluginBuilder {
  private var properties: Map[String, AnyRef] = _

  override def newConnection(conProperties: util.Map[String, AnyRef]): Connection = {
    // properties 指代 connection.toml配置文件内容，具体见：https://gitee.com/luyu-community/router/blob/master/README.md
    // init work
    properties = conProperties.toMap
    new connection.Connection(this.properties)
  }

  override def newDriver(connection: Connection, DriverProperties: util.Map[String, AnyRef]): Driver = {
    // properties 指代 driver.toml配置文件内容，具体见：https://gitee.com/luyu-community/router/blob/master/README.md
    val version = DriverProperties.get("version")
    if (PluginBuilder.getProtocolVersion != version) {
      System.exit(1)
    }
    driver.Driver(connection, this.properties)
  }
}
