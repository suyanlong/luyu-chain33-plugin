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

// format: off
object ContractUtil {
  private val LEFT_RIGHT_BRACKETS                                  = "[]"
  private val CLASS_MAP: ConcurrentMap[String, java.lang.Class[_]] = Maps.newConcurrentMap()
  private val typeVec = Array {
    ((param: ContractParam) => ContractType.BOOL.equalsIgnoreCase(param.`type`), (param: ContractParam) => new Bool(Boolean.unbox(param.value)))
    ((param: ContractParam) => param.`type`.contains(ContractType.UINT) && !param.`type`.contains(LEFT_RIGHT_BRACKETS), (param: ContractParam) => reflectUintWithValue(param.`type`, param.value))
    ((param: ContractParam) => param.`type`.contains(ContractType.BYTES) && !param.`type`.contains(LEFT_RIGHT_BRACKETS), (param: ContractParam) => reflectBytesWithValue(param.`type`, param.value))
    ((param: ContractParam) => ContractType.ADDRESS.equalsIgnoreCase(param.`type`), (param: ContractParam) => new Address(param.value))
    ((param: ContractParam) => ContractType.STRING.equalsIgnoreCase(param.`type`), (param: ContractParam) => new Utf8String(param.value))
    (
      (param: ContractParam) => param.`type`.contains(ContractType.UINT) && param.`type`.contains(LEFT_RIGHT_BRACKETS),
      (param: ContractParam) => {
        val list  = List.empty
        val array = param.value.split(",")
        array.foreach(value => list :+ reflectUintWithValue(param.`type`.substring(0, param.`type`.length - 2), value))
        new DynamicArray[Uint](list)
      }
    )
    (
      (param: ContractParam) => param.`type`.contains(ContractType.BYTES) && param.`type`.contains(LEFT_RIGHT_BRACKETS),
      (param: ContractParam) => {
        val array = param.value.split(",")
        val list  = List.empty
        array.foreach(value => list :+ (reflectBytesWithValue(param.`type`.substring(0, param.`type`.length - 2), value)))
        new DynamicArray[Bytes](list)
      }
    )
    (
      (param: ContractParam) => ContractType.ADDRESS_ARRAY.equalsIgnoreCase(param.`type`),
      (param: ContractParam) => {
        val list  = List.empty
        val array = param.value.split(",")
        array.foreach(inner => list :+ (new Address(inner)))
        new DynamicArray[Address](list)
      }
    )
  }

  def convertInputParams(params: List[ContractParam]): List[Type[_]] = {
    val inputs = List.empty
    params.filter(param => !(StringUtils.isBlank(param.`type`) || StringUtils.isBlank(param.value))).foreach(value => inputs :+ convert(value))
    inputs
  }

  private def convert(param: ContractParam): Type[_] = typeVec.find(_._1(param)).map(_._2(param)).orNull

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
        output = output :+ {
          if (str.contains(ContractType.UINT) && !str.contains(LEFT_RIGHT_BRACKETS))
            (new TypeReference[Uint]() { override def getType: java.lang.reflect.Type = reflectUint(str) })
          else if (ContractType.BOOL.equalsIgnoreCase(str)) (new TypeReference[Bool]() {})
          else if (ContractType.ADDRESS.equalsIgnoreCase(str)) (new TypeReference[Address]() {})
          else if (ContractType.STRING.equalsIgnoreCase(str)) (new TypeReference[Utf8String]() {})
          else if (str.contains(ContractType.BYTES) && !str.contains(LEFT_RIGHT_BRACKETS))
            (new TypeReference[Bytes]() { override def getType: java.lang.reflect.Type = reflectBytes(str) })
          else if (str.contains(ContractType.UINT) && str.contains(LEFT_RIGHT_BRACKETS))(ClassTransferUtil.transfer(str.substring(0, str.length - 2)))
          else if (str.contains(ContractType.BYTES) && str.contains(LEFT_RIGHT_BRACKETS))(ClassTransferUtil.transfer(
            str.substring(0, str.length - 2)
          ))
          else if (ContractType.ADDRESS_ARRAY.equalsIgnoreCase(str)) (new TypeReference[DynamicArray[Address]]() {})
          else null
        }
      })
      new Function(methodName, inputs, output)
    }
  }

  private def reflectUintWithValue(name: String, value: String): Uint = {
    reflectUintChildren
      .find(_.getSimpleName.equalsIgnoreCase(name))
      .map(clazz => {
        CLASS_MAP.put(name, clazz)
        clazz.getConstructor(classOf[BigInteger]).newInstance(new BigInteger(value))
      })
      .orNull
  }

  private def is(name:String): Option[Class[_]] = if (CLASS_MAP.containsKey(name)) Some(CLASS_MAP.get(name)) else Option.empty

  private def getSetClass(name: String,fun: => Set[Class[_]])={
    is(name).getOrElse(fun.find(_.getSimpleName.equalsIgnoreCase(name)).map(clazz => {
      CLASS_MAP.put(name, clazz)
      clazz
    }).orNull)
  }

  private def reflectUint(name: String): Class[_] = {
//    getSetClass(name,reflectUintChildren)
    is(name).getOrElse(
      reflectUintChildren
      .find(_.getSimpleName.equalsIgnoreCase(name))
      .map(clazz => {
        CLASS_MAP.put(name, clazz)
        clazz
      })
      .orNull)
  }

  private def reflectBytes(name: String): Class[_] = {
    is(name).getOrElse(
      reflectBytesChildren
        .find(_.getSimpleName.equalsIgnoreCase(name))
        .map(clazz => {
          CLASS_MAP.put(name, clazz)
          clazz
        })
        .orNull
    )
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
    .find(_.getSimpleName.equalsIgnoreCase(name))
    .map(_.getConstructor(classOf[Array[Byte]]).newInstance(value.getBytes()))
    .orNull
}
