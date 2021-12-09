package com.chain33.contract

sealed class ABI {
  var name: String                       = _
  var inputTypes: List[AbiFunctionType]  = _
  var outputTypes: List[AbiFunctionType] = _
}
