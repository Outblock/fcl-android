package io.outblock.fcl.utils

import com.nftco.flow.sdk.FlowBlock
import io.outblock.fcl.Fcl
import io.outblock.fcl.FlowApi


fun Fcl.getLatestBlock(sealed: Boolean = true): FlowBlock {
    return FlowApi.get().getLatestBlock()
}
