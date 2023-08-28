package com.elveum.effects.example.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elveum.effects.core.AttachSideEffects
import com.elveum.effects.example.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var attachSideEffects: AttachSideEffects

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}