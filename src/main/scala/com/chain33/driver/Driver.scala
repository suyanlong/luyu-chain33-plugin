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
    callback.onResponse(STATUS.OK, Msg.SUCCESS, resources.toArray(new Array[Resource](resources.size)))
  }

  override def getBlockByHash(blockHash: String, callback: BaseDriver.BlockCallback): Unit = {
    connection.asyncSend(
      "",
      Type.GET_BLOCK_BY_HASH,
      blockHash.getBytes,
      (code, msg, block) => {
        if (code != STATUS.OK) callback.onResponse(code, msg, null)
        else {
          val blk = Utils.toObject(block).asInstanceOf[InternalBlock]
          callback.onResponse(code, Msg.SUCCESS, blk.toBlock)
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
      (code, _, height) => {
        if (code != STATUS.OK) getBlockNumberFuture.complete(null)
        else getBlockNumberFuture.complete(height)
      }
    )
    val bsBlockNumber = getBlockNumberFuture.get(20, TimeUnit.SECONDS)
    Utils.bytesToLong(bsBlockNumber)
  }

  override def getBlockByNumber(blockNumber: Long, callback: BaseDriver.BlockCallback): Unit = {
    connection.asyncSend(
      "",
      Type.GET_BLOCK_BY_NUMBER,
      Utils.longToBytes(blockNumber),
      (code, msg, block) => {
        if (code != STATUS.OK) callback.onResponse(code, msg, null)
        else {
          val blk = Utils.toObject(block).asInstanceOf[InternalBlock]
          callback.onResponse(code, Msg.SUCCESS, blk.toBlock)
        }
      }
    )
  }

  override def getTransactionReceipt(txHash: String, callback: BaseDriver.ReceiptCallback): Unit = {
    connection.asyncSend(
      "",
      Type.GET_TRANSACTION_RECEIPT,
      txHash.getBytes,
      (code, msg, r) => {
        if (code != STATUS.OK) callback.onResponse(code, msg, null)
        else {
          Driver.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          val transactionReceipt = Driver.objectMapper.readValue(r, classOf[QueryTransactionResult]) // TODO
          val builder            = new ReceiptBuilder
          builder
            .setResult(Array[String](new String(r))) // TODO
            .setBlockNumber(transactionReceipt.getHeight)
            .setCode(Result.SUCCESS) // SUCCESS
            .setMessage(Msg.SUCCESS)
            .setTransactionBytes(transactionReceipt.getTx.getRawpayload.getBytes()) // TODO
            .setTransactionHash(txHash)
          callback.onResponse(code, Msg.SUCCESS, builder.builder())
        }
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
      (code, msg, abiRaw) => {
        if (code != STATUS.OK) callback.onResponse(code, msg, null)
        else {
          val a   = new String(abiRaw, StandardCharsets.UTF_8)
          val abi = Utils.hexStr2Str(Utils.hexRemove0x(a))
          // TODO chain33 Transaction
          // src/test/java/cn/chain33/javasdk/model/EvmTest.java
          // 1、构造交易
          // 2、签名：原始交易交给，陆羽签名服务中心统一签名
          // 3、发送交易
          // 4、得回执，并返回
          // ------------------------------------------------------------------------------------------------------
          // 1、构造交易
          val contractAddr     = "" // TODO
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
            (code, _, signBytes) =>
              if (code != STATUS.OK) future.complete(null)
              else future.complete(signBytes)
          )
          val luyuSignBytes = future.get(30, TimeUnit.SECONDS)
          val luyuSignData  = SignatureData.parseFrom(luyuSignBytes)
//                val signature     =  new javasdk.model.gm.SM2Util.SM2Signature(luyuSignData.getR, luyuSignData.getS)
//                val sig           = Driver.join(javasdk.utils.HexUtil.hexStringToBytes(signature.), pubKey) // 得签名
          val signature = new SM2.Signature(luyuSignData.getR, luyuSignData.getS)
          val sig       = Driver.join(javasdk.utils.HexUtil.hexStringToBytes(signature.getSign), pubKey) // 得签名
          protobufTx = protobufTx.toBuilder.setSignature(Signature.parseFrom(sig)).build()
          connection.asyncSend(
            transaction.getPath,
            Type.SEND_TRANSACTION,
            javasdk.utils.HexUtil.toHexString(protobufTx.toByteArray).getBytes(StandardCharsets.UTF_8),
            (_, _, txHash) => {
              connection.asyncSend(
                "",
                Type.GET_TRANSACTION_RECEIPT,
                txHash,
                (code, msg, receiptRaw) => {
                  // todo verify transaction on-chain proof
                  if (code != STATUS.OK) callback.onResponse(code, msg, null)
                  else {
                    val r       = Utils.toObject(receiptRaw).asInstanceOf[QueryTransactionResult]
                    val builder = new ReceiptBuilder
                    builder
                      .setBlockNumber(r.getHeight)
                      .setMethod(transaction.getMethod)
                      .setArgs(transaction.getArgs)
                      .setPath(transaction.getPath)
                      .setCode(Result.SUCCESS)
                      .setMessage(Msg.SUCCESS)
                      .setTransactionBytes(r.getTx.getRawpayload.getBytes)
                      .setTransactionHash(txHash.toString)
                    callback.onResponse(code, Msg.SUCCESS, builder.builder())
                  }
                }
              )
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
    val address  = contract // TODO???

    connection.asyncSend(
      address, // TODO???
      Type.GET_ABI,
      null,
      (code, msg, abiRaw) => {
        if (code != STATUS.OK) callback.onResponse(code, msg, null)
        else {
          val a     = new String(abiRaw, StandardCharsets.UTF_8)
          val abi   = Utils.hexStr2Str(Utils.hexRemove0x(a))
          val ctAbi = new ContractABI(abi)
          // TODO bug
          val funAbi   = ctAbi.getFunctions(callRequest.getMethod).get(0)
          val function = Utils.convertFunction(abi, callRequest.getMethod, callRequest.getArgs)
          val call     = new ContractCall(contract, FunctionEncoder.encode(function))
          val pubKey   = account.getPubKey
          // TODO bug
          val sender = SM2Keys.getAddress(Numeric.toHexStringWithPrefixZeroPadded(new BigInteger(1, pubKey), 128))
          call.sender_=("0x" + sender)
          val data = Driver.objectMapper.writeValueAsBytes(call)
          connection.asyncSend(
            callRequest.getPath, // TODO
            Type.CALL_TRANSACTION,
            data,
            (code, msg, fun) => {
              if (code != STATUS.OK) callback.onResponse(code, msg, null)
              else {
                val callResponse = new CallResponse
                if (fun != null) {
                  val resp = new String(fun)
                  if (!(resp == "0x")) callResponse.setResult(funAbi.decodeOutput(resp))
                }
                callResponse.setCode(Result.SUCCESS) // original receipt status
                callResponse.setMessage(Msg.SUCCESS)
                callResponse.setMethod(callRequest.getMethod)
                callResponse.setArgs(callRequest.getArgs)
                callResponse.setPath(callRequest.getPath)
                callback.onResponse(code, Msg.SUCCESS, callResponse)
              }
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

class ReceiptBuilder {
  private val receipt = new Receipt

  def setResult(result: Array[String]): ReceiptBuilder = {
    receipt.setResult(result)
    this
  }

  def setCode(code: Int): ReceiptBuilder = {
    receipt.setCode(code)
    this
  }

  def setMessage(message: String): ReceiptBuilder = {
    receipt.setMessage(message)
    this
  }

  def setPath(path: String): ReceiptBuilder = {
    receipt.setPath(path)
    this
  }

  def setMethod(method: String): ReceiptBuilder = {
    receipt.setMethod(method)
    this
  }

  def setArgs(args: Array[String]): ReceiptBuilder = {
    receipt.setArgs(args)
    this
  }

  def setTransactionHash(transactionHash: String): ReceiptBuilder = {
    receipt.setTransactionHash(transactionHash)
    this
  }

  def setTransactionBytes(transactionBytes: Array[Byte]): ReceiptBuilder = {
    receipt.setTransactionBytes(transactionBytes)
    this
  }

  def setBlockNumber(blockNumber: Long): ReceiptBuilder = {
    receipt.setBlockNumber(blockNumber)
    this
  }
  def builder(): Receipt = {
    receipt
  }
}
