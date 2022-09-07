package io.outblock.fcl.request.resolver

import io.outblock.fcl.models.Interaction

interface Resolver {
    suspend fun resolve(ix: Interaction)
}