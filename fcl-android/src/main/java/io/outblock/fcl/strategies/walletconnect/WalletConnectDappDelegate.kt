package io.outblock.fcl.strategies.walletconnect

import com.google.gson.Gson
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import io.outblock.fcl.utils.logd
import io.outblock.fcl.utils.loge

private const val TAG = "WalletConnectDappDelegate"

internal class WalletConnectDappDelegate : SignClient.DappDelegate {

    override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
        logd(TAG, "onConnectionStateChange() state:$state")
        logd(TAG, "onConnectionStateChange() state:${Gson().toJson(state)}")
    }

    override fun onError(error: Sign.Model.Error) {
        logd(TAG, "onError() error:$error")
        error.throwable.printStackTrace()
    }

    override fun onSessionApproved(approvedSession: Sign.Model.ApprovedSession) {
        logd(TAG, "onSessionApproved() approvedSession:$approvedSession")
        logd(TAG, "onSessionApproved() approvedSession:${Gson().toJson(approvedSession)}")
        val account = approvedSession.namespaces["flow"]!!.accounts.first()
        val chainId = account.replaceAfterLast(":", "").removeSuffix(":")
        val address = account.replaceBeforeLast(":", "").removePrefix(":")
        SignClient.request(
            Sign.Params.Request(
                sessionTopic = approvedSession.topic,
                method = "flow_authn",
                params = """[{"addr":"$address"}]""",
                chainId = chainId,
            )
        ) { error -> loge(error.throwable) }
    }

    override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {
        logd(TAG, "onSessionDelete() deletedSession:$deletedSession")
        logd(TAG, "onSessionDelete() deletedSession:${Gson().toJson(deletedSession)}")
    }

    override fun onSessionEvent(sessionEvent: Sign.Model.SessionEvent) {
        logd(TAG, "onSessionEvent() sessionEvent:$sessionEvent")
        logd(TAG, "onSessionEvent() sessionEvent:${Gson().toJson(sessionEvent)}")
    }

    override fun onSessionExtend(session: Sign.Model.Session) {
        logd(TAG, "onSessionExtend() session:$session")
        logd(TAG, "onSessionExtend() session:${Gson().toJson(session)}")
    }

    override fun onSessionRejected(rejectedSession: Sign.Model.RejectedSession) {
        logd(TAG, "onSessionRejected() rejectedSession:$rejectedSession")
        logd(TAG, "onSessionRejected() rejectedSession:${Gson().toJson(rejectedSession)}")
    }

    override fun onSessionRequestResponse(response: Sign.Model.SessionRequestResponse) {
        logd(TAG, "onSessionRequestResponse() response:$response")
        logd(TAG, "onSessionRequestResponse() response:${Gson().toJson(response)}")
        dispatchWcRequestResponse(response)
    }

    override fun onSessionUpdate(updatedSession: Sign.Model.UpdatedSession) {
        logd(TAG, "onSessionUpdate() updatedSession:$updatedSession")
        logd(TAG, "onSessionUpdate() updatedSession:${Gson().toJson(updatedSession)}")
    }
}