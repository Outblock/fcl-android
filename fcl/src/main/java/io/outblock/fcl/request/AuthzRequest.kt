package io.outblock.fcl.request

import io.outblock.fcl.FlowApi
import io.outblock.fcl.models.Argument
import io.outblock.fcl.models.Interaction
import io.outblock.fcl.models.toFclArgument
import io.outblock.fcl.models.toFlowTransaction
import io.outblock.fcl.request.builder.FclBuilder
import io.outblock.fcl.request.resolver.*

internal class AuthzSend {

    suspend fun send(builder: FclBuilder.() -> Unit): String {
        val ix = prepare(FclBuilder().apply { builder(this) })
        listOf(
            CadenceResolver(),
            AccountsResolver(),
            RefBlockResolver(),
            SequenceNumberResolver(),
            SignatureResolver(),
        ).forEach { it.resolve(ix) }

        val id = FlowApi.get().sendTransaction(ix.toFlowTransaction())
        return id.base16Value
    }

    private fun prepare(builder: FclBuilder): Interaction {
        return Interaction().apply {
            builder.cadence?.let {
                tag = Interaction.Tag.transaction.value
                message.cadence = it
            }
            builder.arguments.map { it.toFclArgument() }.apply {
                message.arguments = map { it.tempId }
                arguments = toLinkedMap()
            }

            builder.limit?.let { message.computeLimit = it }
        }
    }

    private fun List<Argument>.toLinkedMap(): LinkedHashMap<String, Argument> {
        val map = linkedMapOf<String, Argument>()
        forEach { map[it.tempId] = it }
        return map
    }
}