package com.example.dynamicfeaturepoc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

const val DYNAMIC_FEATURE_MODULE = "dynamicfeature"
const val PROVIDER_CLASS = "com.example.dynamicfeature.DynamicModuleApiImpl\$Provider"
const val TAG = "DynamicFeaturePoc"

class MainActivity : AppCompatActivity() {
    private lateinit var launchDynamicActivityBtn: Button
    private lateinit var installDynamicModuleBtn: Button
    private lateinit var editText: EditText
    private lateinit var saveBtn: Button
    private lateinit var listener: SplitInstallStateUpdatedListener
    private lateinit var progress: ProgressBar
    private lateinit var dynamicModuleApi: DynamicModuleApi
    private var mySessionId: Int = -1
    private var installed: Boolean = false

    lateinit var splitInstallManager: SplitInstallManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchDynamicActivityBtn = findViewById(R.id.btn_launch_activity)
        installDynamicModuleBtn = findViewById(R.id.btn_dynamic_module)
        editText = findViewById(R.id.etName)
        saveBtn = findViewById(R.id.btnSave)
        progress = findViewById(R.id.progress)
        splitInstallManager = SplitInstallManagerFactory.create(this)
        setOnClickListener()
    }

    private fun isDynamicModuleInstalled(): Boolean {
        return splitInstallManager.installedModules.contains(DYNAMIC_FEATURE_MODULE)
    }

    private fun setOnClickListener() {

        saveBtn.setOnClickListener {
            if (isDynamicModuleInstalled() && ::dynamicModuleApi.isInitialized) {
                dynamicModuleApi.setName(editText.text.toString())
                Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Install the dynamic module first", Toast.LENGTH_SHORT).show()
            }
        }

        launchDynamicActivityBtn.setOnClickListener {
            if (isDynamicModuleInstalled() && ::dynamicModuleApi.isInitialized) {
                if (::listener.isInitialized) {
                    splitInstallManager.unregisterListener(listener)
                }
                val intent = Intent()
                intent.setClassName(
                    BuildConfig.APPLICATION_ID,
                    "com.example.dynamicfeature.DynamicModuleActivity"
                )
                intent.putExtra("status", dynamicModuleApi.getName())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Install the dynamic module first", Toast.LENGTH_SHORT).show()
            }
        }
        installDynamicModuleBtn.setOnClickListener {
            if (!isDynamicModuleInstalled()) {
                installDynamicModule()
            } else {
                Toast.makeText(this, "Dynamic module already installed", Toast.LENGTH_SHORT).show()
            }
        }

        listener = SplitInstallStateUpdatedListener { state: SplitInstallSessionState ->
            if (state.sessionId() == mySessionId) {
                when (state.status()) {
                    SplitInstallSessionStatus.DOWNLOADING -> {
                        Toast.makeText(this, "Downloading dynamic module", Toast.LENGTH_SHORT)
                            .show()
                    }
                    SplitInstallSessionStatus.FAILED -> {
                        progress.visibility = View.GONE
                        Toast.makeText(this, "Installation failed", Toast.LENGTH_SHORT).show()
                        return@SplitInstallStateUpdatedListener
                    }
                    SplitInstallSessionStatus.INSTALLED -> {
                        progress.visibility = View.GONE
                        val dynamicFeatureProvider =
                            Class.forName(PROVIDER_CLASS).kotlin.objectInstance as DynamicModuleApi.Provider
                        dynamicModuleApi = dynamicFeatureProvider.get()
                        Log.d(TAG, "Got the dynamic feature module")
                        Toast.makeText(this, "Dynamic module installed", Toast.LENGTH_SHORT)
                            .show()
                        installed = true
                    }
                    SplitInstallSessionStatus.CANCELED -> {
                        Toast.makeText(this, "Installation Cancelled", Toast.LENGTH_SHORT).show()
                        return@SplitInstallStateUpdatedListener
                    }
                }
            }
        }
        splitInstallManager.registerListener(listener)
    }

    private fun installDynamicModule() {
        val request = SplitInstallRequest.newBuilder().addModule("dynamicfeature").build()
        splitInstallManager.startInstall(request)
            .addOnSuccessListener { sessionId ->
                Log.d(TAG, "Installing dynamic module")
                mySessionId = sessionId
                progress.visibility = View.VISIBLE
                Toast.makeText(this, "Installation Started", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d(TAG, "Installing failed")
                progress.visibility = View.GONE
                Toast.makeText(this, "Installation failed $it", Toast.LENGTH_SHORT).show()
            }
    }

}