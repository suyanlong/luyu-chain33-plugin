package com.chain33.util

import com.citahub.cita.abi.TypeReference
import com.citahub.cita.abi.datatypes.Bytes
import com.citahub.cita.abi.datatypes.DynamicArray
import com.citahub.cita.abi.datatypes.Uint
import com.citahub.cita.abi.datatypes.generated._

object ClassTransferUtil {
  def transfer(`type`: String): TypeReference[_] = {
    if (classOf[Uint].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint]]() {}
    else if (classOf[Uint8].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint8]]() {}
    else if (classOf[Uint16].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint16]]() {}
    else if (classOf[Uint24].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint24]]() {}
    else if (classOf[Uint32].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint32]]() {}
    else if (classOf[Uint40].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint40]]() {}
    else if (classOf[Uint48].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint48]]() {}
    else if (classOf[Uint56].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint56]]() {}
    else if (classOf[Uint64].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint64]]() {}
    else if (classOf[Uint72].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint72]]() {}
    else if (classOf[Uint80].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint80]]() {}
    else if (classOf[Uint88].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint88]]() {}
    else if (classOf[Uint96].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint96]]() {}
    else if (classOf[Uint104].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint104]]() {}
    else if (classOf[Uint112].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint112]]() {}
    else if (classOf[Uint120].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint120]]() {}
    else if (classOf[Uint128].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint128]]() {}
    else if (classOf[Uint136].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint136]]() {}
    else if (classOf[Uint144].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint144]]() {}
    else if (classOf[Uint152].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint152]]() {}
    else if (classOf[Uint160].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint160]]() {}
    else if (classOf[Uint168].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint168]]() {}
    else if (classOf[Uint176].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint176]]() {}
    else if (classOf[Uint184].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint184]]() {}
    else if (classOf[Uint192].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint192]]() {}
    else if (classOf[Uint200].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint200]]() {}
    else if (classOf[Uint208].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint208]]() {}
    else if (classOf[Uint216].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint216]]() {}
    else if (classOf[Uint224].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint224]]() {}
    else if (classOf[Uint232].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint232]]() {}
    else if (classOf[Uint240].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint240]]() {}
    else if (classOf[Uint248].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint248]]() {}
    else if (classOf[Uint256].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Uint256]]() {}
    else if (classOf[Bytes].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes]]() {}
    else if (classOf[Bytes1].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes1]]() {}
    else if (classOf[Bytes2].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes2]]() {}
    else if (classOf[Bytes3].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes3]]() {}
    else if (classOf[Bytes4].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes4]]() {}
    else if (classOf[Bytes5].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes5]]() {}
    else if (classOf[Bytes6].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes6]]() {}
    else if (classOf[Bytes7].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes7]]() {}
    else if (classOf[Bytes8].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes8]]() {}
    else if (classOf[Bytes9].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes9]]() {}
    else if (classOf[Bytes10].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes10]]() {}
    else if (classOf[Bytes11].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes11]]() {}
    else if (classOf[Bytes12].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes12]]() {}
    else if (classOf[Bytes13].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes13]]() {}
    else if (classOf[Bytes14].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes14]]() {}
    else if (classOf[Bytes15].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes15]]() {}
    else if (classOf[Bytes16].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes16]]() {}
    else if (classOf[Bytes17].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes17]]() {}
    else if (classOf[Bytes18].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes18]]() {}
    else if (classOf[Bytes19].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes19]]() {}
    else if (classOf[Bytes20].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes20]]() {}
    else if (classOf[Bytes21].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes21]]() {}
    else if (classOf[Bytes22].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes22]]() {}
    else if (classOf[Bytes23].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes23]]() {}
    else if (classOf[Bytes24].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes24]]() {}
    else if (classOf[Bytes25].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes25]]() {}
    else if (classOf[Bytes26].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes26]]() {}
    else if (classOf[Bytes27].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes27]]() {}
    else if (classOf[Bytes28].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes28]]() {}
    else if (classOf[Bytes29].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes29]]() {}
    else if (classOf[Bytes30].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes30]]() {}
    else if (classOf[Bytes31].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes31]]() {}
    else if (classOf[Bytes32].getSimpleName.equalsIgnoreCase(`type`))
      return new TypeReference[DynamicArray[Bytes32]]() {}
    null
  }
}
