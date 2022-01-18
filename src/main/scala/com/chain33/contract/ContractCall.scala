package com.chain33.contract

class ContractCall {
  var contract: String = _
  var data: String     = _
  var sender           = "0x0000000000000000000000000000000000000000"

  def this(contract: String, data: String) {
    this()
    this.contract = contract
    this.data = data
  }
}
