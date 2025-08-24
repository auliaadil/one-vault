package com.adilstudio.project.onevault.service

import android.app.assist.AssistStructure
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.view.autofill.AutofillId
import android.widget.RemoteViews
import android.content.Intent
import android.app.PendingIntent
import android.os.CancellationSignal
import com.adilstudio.project.onevault.presentation.credential.CredentialListScreen
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import android.service.autofill.Dataset
import android.view.autofill.AutofillValue
import android.content.Context
import android.text.TextUtils
import com.adilstudio.project.onevault.data.security.SecurityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class OneVaultAutofillService : AutofillService() {
    private val credentialRepository: CredentialRepository by inject()
    private val securityManager: SecurityManager by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val structure = request.fillContexts.last().structure
        val fields = findAutofillFields(structure)

        if (fields.usernameId != null && fields.passwordId != null) {
            serviceScope.launch {
                try {
                    val credentials = credentialRepository.getCredentials().first()

                    val datasets = credentials.map { credential ->
                        val usernameValue = AutofillValue.forText(credential.username)
                        val passwordValue = AutofillValue.forText(credential.encryptedPassword)
                        Dataset.Builder()
                            .setValue(fields.usernameId, usernameValue, createRemoteViews(credential))
                            .setValue(fields.passwordId, passwordValue, createRemoteViews(credential))
                            .build()
                    }

                    val response = if (datasets.isNotEmpty()) {
                        FillResponse.Builder()
                            .apply { datasets.forEach { addDataset(it) } }
                            .build()
                    } else null

                    callback.onSuccess(response)
                } catch (e: Exception) {
                    callback.onSuccess(null) // Handle errors gracefully
                }
            }
        } else {
            callback.onSuccess(null)
        }
    }

    override fun onSaveRequest(
        request: android.service.autofill.SaveRequest,
        callback: android.service.autofill.SaveCallback
    ) {
        callback.onSuccess()
    }

    private fun findAutofillFields(structure: AssistStructure): AutofillFields {
        var usernameId: AutofillId? = null
        var passwordId: AutofillId? = null
        val nodes = structure.windowNodeCount
        for (i in 0 until nodes) {
            val windowNode = structure.getWindowNodeAt(i)
            val viewNodeCount = windowNode.rootViewNode.childCount
            for (j in 0 until viewNodeCount) {
                val viewNode = windowNode.rootViewNode.getChildAt(j)
                val hint = viewNode.autofillHints?.firstOrNull()
                if (hint != null) {
                    if (hint.equals("username", true) || hint.equals("email", true)) {
                        usernameId = viewNode.autofillId
                    } else if (hint.equals("password", true)) {
                        passwordId = viewNode.autofillId
                    }
                }
            }
        }
        return AutofillFields(usernameId, passwordId)
    }

    private fun createRemoteViews(credential: Credential): RemoteViews {
        val packageName = applicationContext.packageName
        val remoteViews = RemoteViews(packageName, android.R.layout.simple_list_item_1)
        remoteViews.setTextViewText(android.R.id.text1, credential.serviceName)
        return remoteViews
    }
}

data class AutofillFields(
    val usernameId: AutofillId?,
    val passwordId: AutofillId?
)
