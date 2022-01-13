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
import com.google.common.collect.Maps

import java.math.BigInteger
import java.util.concurrent.ConcurrentMap
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.reflections.Reflections
import com.chain33.contract._
import com.chain33.constant._

import scala.collection.convert.ImplicitConversions.`set asScala`
import scala.collection.convert.ImplicitConversions.`seq AsJavaList`

object ContractUtil {
  private val LEFT_RIGHT_BRACKETS = "[]"
//  private val CLASS_MAP:ConcurrentMap[String,Class[_]]           = Maps.newConcurrentMap[String,Class[_]]()
  private val CLASS_MAP: ConcurrentMap[String, java.lang.Class[_]] = Maps.newConcurrentMap()

  def convertInputParams(params: List[ContractParam]): List[Type[_]] = {
    val inputs = List.empty
    params
      .filter(param => !(StringUtils.isBlank(param.`type`) || StringUtils.isBlank(param.value)))
      .foreach(value => inputs :+ convert(value))
    inputs
  }

  private def convert(param: ContractParam): Type[_] = {
    val `type` = param.`type`
    val value  = param.value
    if (ContractType.BOOL.equalsIgnoreCase(`type`)) return new Bool(Boolean.unbox(value))
    else if (`type`.contains(ContractType.UINT) && !`type`.contains(LEFT_RIGHT_BRACKETS))
      return reflectUintWithValue(param.`type`, param.value)
    else if (`type`.contains(ContractType.BYTES) && !`type`.contains(LEFT_RIGHT_BRACKETS))
      return reflectBytesWithValue(param.`type`, param.value)
    else if (ContractType.ADDRESS.equalsIgnoreCase(`type`)) return new Address(value)
    else if (ContractType.STRING.equalsIgnoreCase(`type`)) return new Utf8String(value)

    val array = value.split(",")
    if (`type`.contains(ContractType.UINT) && `type`.contains(LEFT_RIGHT_BRACKETS)) {
      val list = List.empty
      array.foreach(value =>
        list :+ reflectUintWithValue(`type`.substring(0, `type`.length - 2), value)
      )
      return new DynamicArray[Uint](list)
    } else if (`type`.contains(ContractType.BYTES) && `type`.contains(LEFT_RIGHT_BRACKETS)) {
      val list = List.empty
      array.foreach(value =>
        list :+ (reflectBytesWithValue(`type`.substring(0, `type`.length - 2), value))
      )
      return new DynamicArray[Bytes](list)
    } else if (ContractType.ADDRESS_ARRAY.equalsIgnoreCase(`type`)) {
      val list = List.empty
      array.foreach(inner => list :+ (new Address(inner)))
      return new DynamicArray[Address](list)
    }
    null
  }

  def convertFunction(
      methodName: String,
      params: List[ContractParam],
      out: List[String]
  ): Function = {
    val inputs = convertInputParams(params)
    if (CollectionUtils.isEmpty(out)) {
      new Function(methodName, inputs, List[TypeReference[_]]())
    } else {
      var output: List[TypeReference[_]] = List.empty
      out.foreach(str => {
        if (str.contains(ContractType.UINT) && !str.contains(LEFT_RIGHT_BRACKETS))
          output = output :+ (new TypeReference[Uint]() {
            override def getType: java.lang.reflect.Type = reflectUint(str)
          })
        else if (ContractType.BOOL.equalsIgnoreCase(str))
          output = output :+ (new TypeReference[Bool]() {})
        else if (ContractType.ADDRESS.equalsIgnoreCase(str))
          output = output :+ (new TypeReference[Address]() {})
        else if (ContractType.STRING.equalsIgnoreCase(str))
          output = output :+ (new TypeReference[Utf8String]() {})
        else if (str.contains(ContractType.BYTES) && !str.contains(LEFT_RIGHT_BRACKETS))
          output = output :+ (new TypeReference[Bytes]() {
            override def getType: java.lang.reflect.Type = reflectBytes(str)
          })
        else if (str.contains(ContractType.UINT) && str.contains(LEFT_RIGHT_BRACKETS))
          output = output :+ (ClassTransferUtil.transfer(str.substring(0, str.length - 2)))
        else if (str.contains(ContractType.BYTES) && str.contains(LEFT_RIGHT_BRACKETS))
          output = output :+ (ClassTransferUtil.transfer(str.substring(0, str.length - 2)))
        else if (ContractType.ADDRESS_ARRAY.equalsIgnoreCase(str))
          output = output :+ (new TypeReference[DynamicArray[Address]]() {})
      })
      new Function(methodName, inputs, output)
    }
  }

  private def reflectUintWithValue(name: String, value: String): Uint = {
    reflectUintChildren
      .find(clazz => clazz.getSimpleName.equalsIgnoreCase(name))
      .map(clazz => {
        CLASS_MAP.put(name, clazz)
        clazz.getConstructor(classOf[BigInteger]).newInstance(new BigInteger(value))
      })
      .orNull
  }

  private def reflectUint(name: String): Class[_] = {
    if (CLASS_MAP.containsKey(name)) {
      CLASS_MAP.get(name)
    } else {
      reflectUintChildren
        .find(clazz => clazz.getSimpleName.equalsIgnoreCase(name))
        .map(clazz => {
          CLASS_MAP.put(name, clazz)
          clazz
        })
        .orNull
    }
  }

  private def reflectBytes(name: String): Class[_] = {
    if (CLASS_MAP.containsKey(name)) {
      CLASS_MAP.get(name)
    } else {
      reflectBytesChildren
        .find(clazz => clazz.getSimpleName.equalsIgnoreCase(name))
        .map(clazz => {
          CLASS_MAP.put(name, clazz)
          clazz
        })
        .orNull
    }
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

  private def reflectBytesWithValue(name: String, value: String): Bytes = reflectBytesChildren
    .find(clazz => clazz.getSimpleName.equalsIgnoreCase(name))
    .map(clazz => clazz.getConstructor(classOf[Array[Byte]]).newInstance(value.getBytes()))
    .orNull
}
