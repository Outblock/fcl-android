package io.outblock.fcl.utils


fun String.removeAddressPrefix(): String = this.removePrefix("0x").removePrefix("Fx")

fun String.addAddressPrefix(): String = "0x" + removeAddressPrefix()