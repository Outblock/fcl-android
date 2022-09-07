package io.outblock.fcl.request.resolver

import io.outblock.fcl.Fcl
import io.outblock.fcl.models.Interaction
import io.outblock.fcl.models.isScript
import io.outblock.fcl.models.isTransaction

class CadenceResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        if (!(ix.isTransaction() || ix.isScript())) {
            return
        }
        var cadence = ix.message.cadence ?: return

        Fcl.config.data().filter { it.key.startsWith("0x") }.forEach { cadence = cadence.replace(it.key, it.value) }

        ix.message.cadence = cadence
    }
}