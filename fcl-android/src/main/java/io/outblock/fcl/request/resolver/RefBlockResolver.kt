package io.outblock.fcl.request.resolver

import io.outblock.fcl.FlowApi
import io.outblock.fcl.models.Interaction

class RefBlockResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        val block = FlowApi.get().getLatestBlock(sealed = true)
        ix.message.refBlock = block.id.base16Value
    }
}