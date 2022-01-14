package com.chain33.util

import com.citahub.cita.abi.TypeReference
import com.citahub.cita.abi.datatypes.Bytes
import com.citahub.cita.abi.datatypes.DynamicArray
import com.citahub.cita.abi.datatypes.Uint
import com.citahub.cita.abi.datatypes.generated._

object ClassTransferUtil {

  val typeMap = Map {
    classOf[Uint].getSimpleName.toUpperCase    -> { () => new TypeReference[DynamicArray[Uint]]() {} }
    classOf[Uint8].getSimpleName.toUpperCase   -> { () => new TypeReference[DynamicArray[Uint8]]() {} }
    classOf[Uint16].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint16]]() {} }
    classOf[Uint24].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint24]]() {} }
    classOf[Uint32].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint32]]() {} }
    classOf[Uint40].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint40]]() {} }
    classOf[Uint48].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint48]]() {} }
    classOf[Uint56].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint56]]() {} }
    classOf[Uint64].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint64]]() {} }
    classOf[Uint72].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint72]]() {} }
    classOf[Uint80].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint80]]() {} }
    classOf[Uint88].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint88]]() {} }
    classOf[Uint96].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Uint96]]() {} }
    classOf[Uint104].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint104]]() {} }
    classOf[Uint112].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint112]]() {} }
    classOf[Uint120].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint120]]() {} }
    classOf[Uint128].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint128]]() {} }
    classOf[Uint136].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint136]]() {} }
    classOf[Uint144].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint144]]() {} }
    classOf[Uint152].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint152]]() {} }
    classOf[Uint160].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint160]]() {} }
    classOf[Uint168].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint168]]() {} }
    classOf[Uint176].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint176]]() {} }
    classOf[Uint184].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint184]]() {} }
    classOf[Uint192].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint192]]() {} }
    classOf[Uint200].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint200]]() {} }
    classOf[Uint208].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint208]]() {} }
    classOf[Uint216].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint216]]() {} }
    classOf[Uint224].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint224]]() {} }
    classOf[Uint232].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint232]]() {} }
    classOf[Uint240].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint240]]() {} }
    classOf[Uint248].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint248]]() {} }
    classOf[Uint256].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Uint256]]() {} }
    classOf[Bytes].getSimpleName.toUpperCase   -> { () => new TypeReference[DynamicArray[Bytes]]() {} }
    classOf[Bytes1].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Bytes1]]() {} }
    classOf[Bytes2].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Bytes2]]() {} }
    classOf[Bytes3].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Bytes3]]() {} }
    classOf[Bytes4].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Bytes4]]() {} }
    classOf[Bytes5].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Bytes5]]() {} }
    classOf[Bytes6].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Bytes6]]() {} }
    classOf[Bytes7].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Bytes7]]() {} }
    classOf[Bytes8].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Bytes8]]() {} }
    classOf[Bytes9].getSimpleName.toUpperCase  -> { () => new TypeReference[DynamicArray[Bytes9]]() {} }
    classOf[Bytes10].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes10]]() {} }
    classOf[Bytes11].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes11]]() {} }
    classOf[Bytes12].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes12]]() {} }
    classOf[Bytes13].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes13]]() {} }
    classOf[Bytes14].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes14]]() {} }
    classOf[Bytes15].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes15]]() {} }
    classOf[Bytes16].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes16]]() {} }
    classOf[Bytes17].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes17]]() {} }
    classOf[Bytes18].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes18]]() {} }
    classOf[Bytes19].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes19]]() {} }
    classOf[Bytes20].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes20]]() {} }
    classOf[Bytes21].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes21]]() {} }
    classOf[Bytes22].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes22]]() {} }
    classOf[Bytes23].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes23]]() {} }
    classOf[Bytes24].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes24]]() {} }
    classOf[Bytes25].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes25]]() {} }
    classOf[Bytes26].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes26]]() {} }
    classOf[Bytes27].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes27]]() {} }
    classOf[Bytes28].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes28]]() {} }
    classOf[Bytes29].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes29]]() {} }
    classOf[Bytes30].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes30]]() {} }
    classOf[Bytes31].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes31]]() {} }
    classOf[Bytes32].getSimpleName.toUpperCase -> { () => new TypeReference[DynamicArray[Bytes32]]() {} }
  }

  def transfer(`type`: String): TypeReference[_] = typeMap.get(`type`.toUpperCase).map(f => f()).orNull
}
