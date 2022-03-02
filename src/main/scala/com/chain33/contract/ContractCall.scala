package com.chain33.contract

class ContractCall {
  var contract: String = _
  var data: String     = _
  var sender: String   = "0x0000000000000000000000000000000000000000" // TODO 默认配置文件

  def this(contract: String, data: String) {
    this()
    this.contract = contract
    this.data = data
  }
}
