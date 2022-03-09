package com.chain33.util

import link.luyu.protocol.network.Resource

//  zone = "pay"
//  chain = "chain33"
//  contract-sender = "0x0000000000000000000000000000000000000000" #合约地址
//  code = "" #
//  abi = ""  #
//  name = "hello"
//  methods = ["set(1)", "get(0)"]

sealed case class Contract(
    zone: String,
    chain: String,
    contractSender: String,
    code: String,
    name: String,
    methods: Array[String]
) {
  def toResource: Resource = {
    val resource = new Resource
    resource.setPath(zone + "." + chain + "." + contractSender)
    resource.setType(chain)
    resource.setMethods(methods)
    resource
  }
}

sealed case class Contracts(contracts: Array[Contract]) {
  def toMap: Map[String, Contract] = contracts.map(c => (c.contractSender, c)).toMap

  def toArray: Array[Contract] = contracts
}

object Contracts {

  def toContracts(properties: Map[String, AnyRef]): Contracts = {
    val resources = properties("luyu-resources").asInstanceOf[Array[Map[String, AnyRef]]]
    Contracts(
      resources.map(elem =>
        Contract(
          elem("zone").toString,
          elem("chain").toString,
          elem("contractSender").toString,
          elem("code").toString,
          elem("name").toString,
          elem("methods").asInstanceOf[Array[String]]
        )
      )
    )
  }
}
