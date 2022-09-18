package io.outblock.fcl.utils

import io.outblock.fcl.models.response.FCLServiceType
import io.outblock.fcl.models.response.Service


internal fun List<Service>?.serviceOfType(type: FCLServiceType): Service? {
    return this?.firstOrNull { it.type == type.value }
}