package com.chain33.util

import com.citahub.cita.abi.TypeReference
import com.citahub.cita.abi.datatypes.Address
import com.citahub.cita.abi.datatypes.Bool
import com.citahub.cita.abi.datatypes.Bytes
import com.citahub.cita.abi.datatypes.DynamicArray
import com.citahub.cita.abi.datatypes.Function
import com.citahub.cita.abi.datatypes.Type
import com.citahub.cita.abi.datatypes.Uint
import com.citahub.cita.abi.datatypes.Utf8String
import com.citahub.constant.ContractType
import com.citahub.contract.ContractParam
import com.google.common.collect.Lists
import com.google.common.collect.Maps

import java.lang.reflect.Constructor
import java.math.BigInteger
import java.util
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.reflections.Reflections

import java.util.{List, Map, Set}

object ContractUtil {
  private val LEFT_RIGHT_BRACKETS = "[]"
  private val CLASS_MAP           = Maps.newConcurrentMap

  def convertInputParams(params: util.List[ContractParam]): util.List[Type[_]] = {
    if (CollectionUtils.isEmpty(params)) return Lists.newArrayList
    val inputs = Lists.newArrayListWithCapacity(params.size)
    import scala.collection.JavaConversions._
    for (param <- params) {
      if (StringUtils.isBlank(param.getType) || StringUtils.isBlank(param.getValue))
        continue // todo: continue is not supported
      val `type` = convert(param)
      if (null == `type`) throw new RuntimeException("")
      inputs.add(`type`)
    }
    inputs
  }

  private def convert(param: ContractParam): Type[_] = {
    val `type` = param.getType
    val value  = param.getValue
    if (ContractType.BOOL.equalsIgnoreCase(`type`)) return new Bool(Boolean.parseBoolean(value))
    else if (`type`.contains(ContractType.UINT) && !`type`.contains(LEFT_RIGHT_BRACKETS))
      return reflectUintWithValue(param.getType, param.getValue)
    else if (`type`.contains(ContractType.BYTES) && !`type`.contains(LEFT_RIGHT_BRACKETS))
      return reflectBytesWithValue(param.getType, param.getValue)
    else if (ContractType.ADDRESS.equalsIgnoreCase(`type`)) return new Address(value)
    else if (ContractType.STRING.equalsIgnoreCase(`type`)) return new Utf8String(value)
    val array = value.split(",")
    if (`type`.contains(ContractType.UINT) && `type`.contains(LEFT_RIGHT_BRACKETS)) {
      val list = Lists.newArrayListWithCapacity(array.length)
      for (inner <- array) {
        list.add(reflectUintWithValue(`type`.substring(0, `type`.length - 2), inner))
      }
      return new DynamicArray[Uint](list)
    } else if (`type`.contains(ContractType.BYTES) && `type`.contains(LEFT_RIGHT_BRACKETS)) {
      val list = Lists.newArrayListWithCapacity(array.length)
      for (inner <- array) {
        list.add(reflectBytesWithValue(`type`.substring(0, `type`.length - 2), inner))
      }
      return new DynamicArray[Bytes](list)
    } else if (ContractType.ADDRESS_ARRAY.equalsIgnoreCase(`type`)) {
      val list = Lists.newArrayListWithCapacity(array.length)
      for (inner <- array) {
        list.add(new Address(inner))
      }
      return new DynamicArray[Address](list)
    }
    null
  }

  def convertFunction(
      methodName: String,
      params: util.List[ContractParam],
      out: util.List[String]
  ): Function = {
    val inputs = convertInputParams(params)
    if (CollectionUtils.isEmpty(out)) return new Function(methodName, inputs, Lists.newArrayList)
    val output = Lists.newArrayListWithCapacity(out.size)
    import scala.collection.JavaConversions._
    for (string <- out) {
      if (string.contains(ContractType.UINT) && !string.contains(LEFT_RIGHT_BRACKETS))
        output.add(TypeReference.create(reflectUint(string)))
      else if (ContractType.BOOL.equalsIgnoreCase(string)) output.add(new TypeReference[Bool]() {})
      else if (ContractType.ADDRESS.equalsIgnoreCase(string))
        output.add(new TypeReference[Address]() {})
      else if (ContractType.STRING.equalsIgnoreCase(string))
        output.add(new TypeReference[Utf8String]() {})
      else if (string.contains(ContractType.BYTES) && !string.contains(LEFT_RIGHT_BRACKETS))
        output.add(TypeReference.create(reflectBytes(string)))
      else if (string.contains(ContractType.UINT) && string.contains(LEFT_RIGHT_BRACKETS))
        output.add(ClassTransferUtil.transfer(string.substring(0, string.length - 2)))
      else if (string.contains(ContractType.BYTES) && string.contains(LEFT_RIGHT_BRACKETS))
        output.add(ClassTransferUtil.transfer(string.substring(0, string.length - 2)))
      else if (ContractType.ADDRESS_ARRAY.equalsIgnoreCase(string))
        output.add(new TypeReference[DynamicArray[Address]]() {})
    }
    new Function(methodName, inputs, output)
  }

  private def reflectUintWithValue(name: String, value: String): Uint = {
    import scala.collection.JavaConversions._
    for (clazz <- reflectUintChildren) {
      if (clazz.getSimpleName.equalsIgnoreCase(name)) try {
        CLASS_MAP.put(name, clazz)
        val constructor = clazz.getConstructor(classOf[BigInteger])
        return constructor.newInstance(new BigInteger(value))
      } catch {
        case e: Exception =>
          throw new RuntimeException("get class constructor failed")
      }
    }
    throw new RuntimeException("can't find class match")
  }

  private def reflectUint(name: String): Class[_] = {
    val cache = CLASS_MAP.get(name)
    if (null != cache) return cache
    import scala.collection.JavaConversions._
    for (clazz <- reflectUintChildren) {
      if (clazz.getSimpleName.equalsIgnoreCase(name)) try {
        CLASS_MAP.put(name, clazz)
        return clazz
      } catch {
        case e: Exception =>
          throw new RuntimeException("get class constructor failed")
      }
    }
    throw new RuntimeException("reflect uint failed")
  }

  private def reflectBytes(name: String): Class[_] = {
    val cache = CLASS_MAP.get(name)
    if (null != cache) return cache
    import scala.collection.JavaConversions._
    for (clazz <- reflectBytesChildren) {
      if (clazz.getSimpleName.equalsIgnoreCase(name)) try {
        CLASS_MAP.put(name, clazz)
        return clazz
      } catch {
        case e: Exception =>
          throw new RuntimeException("get class constructor failed")
      }
    }
    throw new RuntimeException("reflect bytes failed")
  }

  private def reflectUintChildren = {
    val className = classOf[Uint].getName
    val reflections = new Reflections(
      className.substring(0, className.indexOf(classOf[Uint].getSimpleName) - 1)
    )
    val set = reflections.getSubTypesOf(classOf[Uint])
    set.add(classOf[Uint])
    set
  }

  private def reflectBytesChildren = {
    val className = classOf[Bytes].getName
    val reflections = new Reflections(
      className.substring(0, className.indexOf(classOf[Bytes].getSimpleName) - 1)
    )
    reflections.getSubTypesOf(classOf[Bytes])
  }

  private def reflectBytesWithValue(name: String, value: String): Bytes = {
    import scala.collection.JavaConversions._
    for (clazz <- reflectBytesChildren) {
      if (clazz.getSimpleName.equalsIgnoreCase(name)) try {
        val constructor = clazz.getConstructor(classOf[Array[Byte]])
        return constructor.newInstance(value.getBytes)
      } catch {
        case e: Exception =>
          throw new RuntimeException("")
      }
    }
    null
  }
}
