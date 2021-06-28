package com.awad.gazaplace.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val TAG = "FCM, myTag"
class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
//        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d(TAG, "onMessageReceived: ")
        Log.d(TAG, "onMessageReceived: ${p0.messageType}")
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.d(TAG, "onDeletedMessages: ")
    }
}