package io.outblock.fcl.send

import androidx.annotation.WorkerThread
import com.nftco.flow.sdk.FlowId
import com.nftco.flow.sdk.cadence.Field
import com.nftco.flow.sdk.cadence.JsonCadenceBuilder
import io.outblock.fcl.FlowApi
import io.outblock.fcl.models.Argument
import io.outblock.fcl.models.Interaction
import io.outblock.fcl.models.toFclArgument
import io.outblock.fcl.models.toFlowTransaction
import io.outblock.fcl.resolve.*

internal class AuthzSend {
    @WorkerThread
    suspend fun send(builder: ScriptBuilder.() -> Unit): String {
        val ix = prepare(ScriptBuilder().apply { builder(this) })
        listOf(
            CadenceResolver(),
            AccountsResolver(),
            RefBlockResolver(),
            SequenceNumberResolver(),
            SignatureResolver(),
        ).forEach { it.resolve(ix) }

        val id = sendIX(ix)
        return id.base16Value
    }

    private fun prepare(builder: ScriptBuilder): Interaction {
        return Interaction().apply {
            builder.script?.let {
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

    private fun sendIX(ix: Interaction): FlowId {
        return FlowApi.get().sendTransaction(ix.toFlowTransaction())
    }

    private fun List<Argument>.toLinkedMap(): LinkedHashMap<String, Argument> {
        val map = linkedMapOf<String, Argument>()
        forEach { map[it.tempId] = it }
        return map
    }
}

class ScriptBuilder {

    internal var script: String? = null

    internal var arguments: MutableList<Field<*>> = mutableListOf()

    internal var limit: Int? = null

    fun script(script: String) {
        this.script = script
    }

    fun arguments(arguments: MutableList<Field<*>>) {
        this.arguments = arguments
    }

    fun arguments(arguments: JsonCadenceBuilder.() -> Iterable<Field<*>>) {
        val builder = JsonCadenceBuilder()
        this.arguments = arguments(builder).toMutableList()
    }

    fun arg(argument: Field<*>) = arguments.add(argument)

    fun arg(argument: JsonCadenceBuilder.() -> Field<*>) = arg(argument(JsonCadenceBuilder()))

    fun gaslimit(limit: Int) {
        this.limit = limit
    }
}