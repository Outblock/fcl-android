package io.outblock.fcl.request.builder

import com.nftco.flow.sdk.cadence.Field
import com.nftco.flow.sdk.cadence.JsonCadenceBuilder

class FclBuilder {

    internal var cadence: String? = null

    internal var arguments: MutableList<Field<*>> = mutableListOf()

    internal var limit: Int? = null

    fun cadence(cadence: String) {
        this.cadence = cadence
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

    fun gasLimit(limit: Int) {
        this.limit = limit
    }

    override fun toString(): String {
        return "FclBuilder(cadence=$cadence, arguments=${arguments.map { Pair(it.type, it.value) }}, limit=$limit)"
    }
}