package com.chain33.builder

import link.luyu.protocol.link._

import java.util

class Builder extends PluginBuilder {

  override def newConnection(properties: util.Map[String, AnyRef]): Connection = ???

  override def newDriver(connection: Connection, properties: util.Map[String, AnyRef]): Driver = ???

}
