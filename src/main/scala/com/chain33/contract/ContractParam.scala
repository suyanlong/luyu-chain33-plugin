package com.chain33.contract

class ContractParam {
  var `type`: String = _
  var value: String  = _

  override def toString: String =
    "ContractParam{" + "type='" + `type` + '\'' + ", value='" + value + '\'' + '}'
}
