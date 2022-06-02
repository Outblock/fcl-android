package io.outblock.fcl.utils

import io.outblock.fcl.FCL
import io.outblock.fcl.models.response.FCLServiceType
import io.outblock.fcl.models.response.Service


internal fun FCL.serviceOfType(services: List<Service>?, type: FCLServiceType): Service? {
    return services?.firstOrNull { it.type == type.value }
}