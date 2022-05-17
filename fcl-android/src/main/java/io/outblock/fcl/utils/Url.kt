package io.outblock.fcl.utils

import android.net.Uri

internal fun makeServiceUrl(url: String, params: Map<String, String>, location: String): Uri {
    val baseUri = Uri.parse(url).buildUpon()

    for ((key, value) in params) {
        baseUri.appendQueryParameter(key, value)
    }

    baseUri.appendQueryParameter("l6n", location)

    return baseUri.build()
}