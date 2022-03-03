package com.chain33.util

import java.io._
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.citahub.cita.abi.datatypes.Function
import com.chain33.contract.ABI
import com.chain33.contract.AbiFunctionType
import com.chain33.contract.ContractParam

object Utils {
  def getResourceName(path: String): String = {
    val sp = path.split("\\.")
    sp(sp.length - 1)
  }

  def convertFunction(abiString: String, name: String, args: Array[String]): Function = {
    val abis = parseAbi(abiString)
    for (abi <- abis) {
      if (abi.name.equalsIgnoreCase(name)) {
        val outs = List.empty
        abi.outputTypes.foreach((`type`: AbiFunctionType) => outs :+ (`type`.`type`))
        val inputs = abi.inputTypes
        if (null == args || args.length == 0) return ContractUtil.convertFunction(name, null, outs)
        if (args.length != inputs.size)
          throw new RuntimeException("input args number not equals to abi")
        var params: List[ContractParam] = List.empty
        for (i <- inputs.indices) {
          val param = new ContractParam
          param.`type` = inputs(i).`type`
          param.value = args(i)
          params = params :+ param
        }
        return ContractUtil.convertFunction(name, params, outs)
      }
    }
    null
  }

  def parseAbi(abi: String): List[ABI] = {
    var abis         = List.empty[ABI]
    val objectMapper = new ObjectMapper
    val trees        = objectMapper.readTree(abi)
    trees.forEach((tree: JsonNode) => {
      val `type` = tree.get("type").asText
      if ("function".equalsIgnoreCase(`type`)) {
        val inner = new ABI()
        inner.name = tree.get("name").asText
        inner.inputTypes = makeType(tree.get("inputs"))
        inner.outputTypes = makeType(tree.get("outputs"))
        abis = abis :+ inner
      }
    })
    abis
  }

  def makeType(node: JsonNode): List[AbiFunctionType] = {
    var result = List.empty[AbiFunctionType]
    node.forEach((input: JsonNode) => {
      val functionType = new AbiFunctionType
      functionType.`type` = input.get("type").asText
      functionType.name = input.get("name").asText
      result = result :+ functionType
    })
    result
  }

  def hexRemove0x(hex: String): String = {
    if (hex.contains("0x"))
      hex.substring(2)
    else
      hex
  }

  def hexStr2Str(s: String): String = {
    val baKeyword = new Array[Byte](s.length / 2)
    for (i <- baKeyword.indices) {
      baKeyword(i) = (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16)).toByte
    }
    new String(baKeyword, StandardCharsets.UTF_8)
  }

  def longToBytes(x: Long): Array[Byte] = {
    val buffer = ByteBuffer.allocate(8)
    buffer.putLong(0, x)
    buffer.array
  }

  def bytesToLong(bytes: Array[Byte]): Long = {
    val buffer = ByteBuffer.allocate(8)
    buffer.put(bytes, 0, bytes.length)
    buffer.flip
    buffer.getLong
  }

  def toByteArray(obj: Any): Array[Byte] = {
    val bos = new ByteArrayOutputStream
    val oos = new ObjectOutputStream(bos)
    oos.writeObject(obj)
    oos.flush()
    val bytes = bos.toByteArray
    oos.close()
    bos.close()
    bytes
  }

  def toObject(bytes: Array[Byte]): Any = {
    val bis = new ByteArrayInputStream(bytes)
    val ois = new ObjectInputStream(bis)
    val obj = ois.readObject()
    ois.close()
    bis.close()
    obj
  }
}
