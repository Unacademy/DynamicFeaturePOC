package com.example.dynamicfeature

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.mylibrary.LibraryUtils
import com.google.android.play.core.splitcompat.SplitCompat

class DynamicModuleActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var libraryBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_module)
        textView = findViewById(R.id.tv_dynamic_module)
        libraryBtn = findViewById(R.id.btn_library_module)
        val text = intent.getStringExtra("status")
        textView.text = text
        setOnClickListener()
    }

    private fun setOnClickListener() {
        libraryBtn.setOnClickListener {
            Toast.makeText(this, LibraryUtils.getClassName(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
    }
}