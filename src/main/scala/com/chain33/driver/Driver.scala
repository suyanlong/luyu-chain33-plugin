package com.chain33.driver

import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.util.concurrent.{CompletableFuture, TimeUnit}
import java.util
import com.google.protobuf.ByteString
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import link.luyu.protocol.algorithm.ecdsa.secp256k1.SignatureData
import link.luyu.protocol.algorithm.sm2.SM2WithSM3
import link.luyu.protocol.common.STATUS
import link.luyu.protocol.link.{Driver => BaseDriver, _}
import link.luyu.protocol.network.{Account, CallRequest, CallResponse, Events, Receipt, Resource, Transaction}
import link.luyu.toolkit.abi.ContractABI
import com.citahub.cita.abi.FunctionEncoder
import com.citahub.cita.utils.Numeric
import com.citahub.cita.crypto.sm2.{SM2, SM2Keys}
import com.chain33.util._
import com.chain33.constant.Constant._
import com.chain33.contract.ContractCall
import cn.chain33.javasdk
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult
import cn.chain33.javasdk.model.protobuf.TransactionAllProtobuf.Signature
import cn.chain33.javasdk.model.protobuf.{EvmService, TransactionAllProtobuf}

case class Driver(connection: Connection) extends BaseDriver {
  override def start(): Unit = {}

  override def stop(): Unit = {}

  override def registerEvents(events: Events): Unit = {}

  override def getType: String = "chain33"

  override def getSignatureType: String = "SM2_WITH_SM3"

  override def listResources(callback: BaseDriver.ResourcesCallback): Unit = {
    // TODO: mock
    val resources: util.Collection[Resource] = new util.HashSet[Resource]
    val resource: Resource                   = new Resource
    resource.setPath("payment.chain33.0x98ab1ed8d9b9d928b5ebab99a4132363d6880b12")
    resource.setType(getType)
    resource.setMethods(Array.empty)
    resources.add(resource)
    callback.onResponse(0, "Success", resources.toArray(new Array[Resource](resources.size)))
  }

  override def getBlockByHash(blockHash: String, callback: BaseDriver.BlockCallback): Unit = {
    connection.asyncSend(
      blockHash,
      Type.GET_BLOCK_BY_HASH,
      null,
      (errorCode: Int, message: String, responseData: Array[Byte]) => {
        if (errorCode != STATUS.OK) callback.onResponse(errorCode, message, null)
        else {
          val block = Utils.toObject(responseData).asInstanceOf[InternalBlock]
          callback.onResponse(STATUS.OK, "Success", block.toBlock)
        }
      }
    )
  }

  override def getBlockNumber: Long = {
    val getBlockNumberFuture = new CompletableFuture[Array[Byte]]
    connection.asyncSend(
      "",
      Type.GET_BLOCK_NUMBER,
      null,
      new Connection.Callback() {
        override def onResponse(errorCode: Int, message: String, responseData: Array[Byte]): Unit = {
          if (errorCode != STATUS.OK) getBlockNumberFuture.complete(null)
          else getBlockNumberFuture.complete(responseData)
        }
      }
    )
    val bsBlockNumber = getBlockNumberFuture.get(20, TimeUnit.SECONDS)
    Utils.bytesToLong(bsBlockNumber)
  }

  override def getBlockByNumber(blockNumber: Long, callback: BaseDriver.BlockCallback): Unit = {
    connection.asyncSend(
      "/chain33/plugin", // TODO
      Type.GET_BLOCK_BY_NUMBER,
      Utils.longToBytes(blockNumber),
      (errorCode: Int, message: String, responseData: Array[Byte]) => {
        if (errorCode != STATUS.OK) callback.onResponse(errorCode, message, null)
        else {
          val block = Utils.toObject(responseData).asInstanceOf[InternalBlock]
          callback.onResponse(STATUS.OK, "Success", block.toBlock)
        }
      }
    )
  }

  override def getTransactionReceipt(txHash: String, callback: BaseDriver.ReceiptCallback): Unit = {
    connection.asyncSend(
      null,
      Type.GET_TRANSACTION_RECEIPT,
      txHash.getBytes,
      (_, _, responseData: Array[Byte]) => {
        Driver.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        val transactionReceipt = Driver.objectMapper.readValue(responseData, classOf[QueryTransactionResult])
        val receipt            = new Receipt
        receipt.setResult(Array[String](new String(responseData)))
        receipt.setBlockNumber(transactionReceipt.getHeight)
        receipt.setCode(0) // SUCCESS
        receipt.setMessage("Success")
        receipt.setTransactionBytes(responseData) // TODO
        receipt.setTransactionHash(txHash)
        callback.onResponse(STATUS.OK, "Success", receipt)
      }
    )
  }

  override def sendTransaction(
      account: Account,
      transaction: Transaction,
      callback: BaseDriver.ReceiptCallback
  ): Unit = {
    val contract = Utils.getResourceName(transaction.getPath)
    connection.asyncSend(
      contract,
      Type.GET_ABI,
      null,
      (errorCode: Int, msg: String, responseData: Array[Byte]) => {
        if (errorCode != STATUS.OK) callback.onResponse(errorCode, msg, null)
        else {
          val raw_abi = new String(responseData, StandardCharsets.UTF_8)
          val abi     = Utils.hexStr2Str(Utils.hexRemove0x(raw_abi))
          connection.asyncSend(
            "",
            Type.GET_BLOCK_NUMBER,
            null,
            (errorCode1: Int, msg1: String, responseData1: Array[Byte]) => {
              if (errorCode1 != STATUS.OK) callback.onResponse(errorCode1, msg1, null)
              else {
                val blockNumber = Utils.bytesToLong(responseData1)
                // TODO chain33 Transaction
                // src/test/java/cn/chain33/javasdk/model/EvmTest.java
                // 1、构造交易
                // 2、签名：原始交易交给，陆羽签名服务中心统一签名
                // 3、发送交易
                // 4、得回执，并返回
                // ------------------------------------------------------------------------------------------------------
                // 1、构造交易
                val contractAddr     = ""
                val callData         = javasdk.utils.EvmUtil.encodeParameter(abi, transaction.getMethod, transaction.getArgs)
                val evmActionBuilder = EvmService.EVMContractAction.newBuilder
                evmActionBuilder.setPara(ByteString.copyFrom(callData))
                evmActionBuilder.setContractAddr(contractAddr)
                val evmContractAction = evmActionBuilder.build
                val createTxWithoutSign = javasdk.utils.TransactionUtil
                  .createTxWithoutSign(javasdk.utils.EvmUtil.execer, evmContractAction.toByteArray, javasdk.utils.TransactionUtil.DEFAULT_FEE, 0)
                val fromHexString = javasdk.utils.HexUtil.fromHexString(createTxWithoutSign)
                var protobufTx    = TransactionAllProtobuf.Transaction.parseFrom(fromHexString)
                // ------------------------------------------------------------------------------------------------------
                // 2、签名
                val future         = new java.util.concurrent.CompletableFuture[Array[Byte]]
                val pubKey         = account.getPubKey
                val prepareMessage = SM2WithSM3.prepareMessage(pubKey, protobufTx.toByteArray)
                account.sign(
                  prepareMessage,
                  new Account.SignCallback() {
                    override def onResponse(status: Int, message: String, signBytes: Array[Byte]): Unit = {
                      if (status != STATUS.OK) future.complete(null)
                      else future.complete(signBytes)
                    }
                  }
                )
                val luyuSignBytes = future.get(30, TimeUnit.SECONDS)
                val luyuSignData  = SignatureData.parseFrom(luyuSignBytes)
                val signature     = new SM2.Signature(luyuSignData.getR, luyuSignData.getS)
                val sig           = Driver.join(javasdk.utils.HexUtil.hexStringToBytes(signature.getSign), pubKey) // 得签名
                protobufTx = protobufTx.toBuilder.setSignature(Signature.parseFrom(sig)).build()
                connection.asyncSend(
                  transaction.getPath,
                  Type.SEND_TRANSACTION,
                  javasdk.utils.HexUtil.toHexString(protobufTx.toByteArray).getBytes(StandardCharsets.UTF_8),
                  (errorCode2: Int, msg2: String, responseData2: Array[Byte]) => {
                    // todo verify transaction on-chain proof
                    if (errorCode2 != STATUS.OK) callback.onResponse(errorCode2, msg2, null)
                    else {
                      val receipt = new Receipt
                      receipt.setBlockNumber(123) // TODO bug
                      receipt.setMethod(transaction.getMethod)
                      receipt.setArgs(transaction.getArgs)
                      receipt.setPath(transaction.getPath)
                      receipt.setCode(0) // SUCCESS
                      receipt.setMessage("Success")
                      receipt.setTransactionBytes(responseData2)
                      receipt.setTransactionHash(new String(responseData2))
                      callback.onResponse(STATUS.OK, "Success", receipt)
                    }
                  }
                )
              }
            }
          )
        }
      }
    )
  }

  override def call(
      account: Account,
      callRequest: CallRequest,
      callback: BaseDriver.CallResponseCallback
  ): Unit = {
    val contract = Utils.getResourceName(callRequest.getPath)
    connection.asyncSend(
      contract,
      Type.GET_ABI,
      null,
      (errorCode: Int, msg: String, responseData: Array[Byte]) => {
        if (errorCode != STATUS.OK) callback.onResponse(errorCode, msg, null)
        else {
          val raw_abi = new String(responseData, StandardCharsets.UTF_8)
          val abi     = Utils.hexStr2Str(Utils.hexRemove0x(raw_abi))
          val ctAbi   = new ContractABI(abi)
          // TODO
          val funAbi   = ctAbi.getFunctions(callRequest.getMethod).get(0)
          val function = Utils.convertFunction(abi, callRequest.getMethod, callRequest.getArgs)
          val call     = new ContractCall(contract, FunctionEncoder.encode(function))
          val pubKey   = account.getPubKey
          val sender   = SM2Keys.getAddress(Numeric.toHexStringWithPrefixZeroPadded(new BigInteger(1, pubKey), 128))
          call.sender_=("0x" + sender)
          val data = Driver.objectMapper.writeValueAsBytes(call)
          connection.asyncSend(
            callRequest.getPath,
            Type.CALL_TRANSACTION,
            data,
            (_, _, responseData1: Array[Byte]) => {
              val callResponse = new CallResponse
              if (responseData1 != null) {
                val resp = new String(responseData1)
                if (!(resp == "0x")) callResponse.setResult(funAbi.decodeOutput(resp))
              }
              callResponse.setCode(0) // original receipt status
              callResponse.setMessage("Success")
              callResponse.setMethod(callRequest.getMethod)
              callResponse.setArgs(callRequest.getArgs)
              callResponse.setPath(callRequest.getPath)
              callback.onResponse(STATUS.OK, "Success", callResponse)
            }
          )
        }
      }
    )
  }
}

object Driver {
  private val objectMapper = new ObjectMapper

  private def join(params: Array[Byte]*) = {
    val baos = new ByteArrayOutputStream
    for (i <- 0 until params.length) {
      baos.write(params(i))
    }
    baos.toByteArray
  }
}
