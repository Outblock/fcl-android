package io.outblock.fcl.send

import com.nftco.flow.sdk.cadence.Field
import com.nftco.flow.sdk.cadence.JsonCadenceBuilder
import io.outblock.fcl.FCL
import io.outblock.fcl.models.Interaction
import io.outblock.fcl.models.toFclArgument
import io.outblock.fcl.utils.ioScope

fun FCL.send(builder: ScriptBuilder.() -> Unit) {
    ioScope {
        val ix = prepare(ScriptBuilder().apply { builder(this) })
    }
}

private fun prepare(builder: ScriptBuilder): Interaction {
    return Interaction().apply {
        builder.script?.let {
            tag = Interaction.Tag.script.value
            message.cadence = it
        }
        builder.arguments.map { it.toFclArgument() }.apply {
            message.arguments = map { it.tempId }
            arguments = associate { it.tempId to it }
        }
        builder.limit?.let { message.computeLimit = it }
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

    fun limit(limit: Int) {
        this.limit = limit
    }

}