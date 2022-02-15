package com.chain33.constant

object Constant {
  object Type {
    val SEND_TRANSACTION        = 101
    val CALL_TRANSACTION        = 102
    val GET_TRANSACTION_RECEIPT = 103
    val GET_ABI                 = 104
    val GET_BLOCK_NUMBER        = 105
    val GET_BLOCK_BY_HASH       = 106
    val GET_BLOCK_BY_NUMBER     = 107
    val GET_TRANSACTION         = 108 // TODO
  }

  object Result {
    val SUCCESS    = 0
    val ERROR: Int = -1
  }

  object Event {
    val EVENT_NEW_BLOCK         = 201
    val EVENT_RESOURCES_CHANGED = 202
  }

  object ContractType {
    val UINT          = "uint"
    val ADDRESS       = "address"
    val STRING        = "string"
    val ADDRESS_ARRAY = "address[]"
    val BOOL          = "bool"
    val BYTES         = "bytes"
  }
}
