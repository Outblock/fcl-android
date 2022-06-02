package io.outblock.fcl.resolve

import io.outblock.fcl.models.Interaction

interface Resolver {
    suspend fun resolve(ix: Interaction)
}