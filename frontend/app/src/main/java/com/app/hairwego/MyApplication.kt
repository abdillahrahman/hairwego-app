package com.app.hairwego

import android.app.Application
import com.app.hairwego.data.local.HairWeGoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class MyApplication : Application (){
    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { HairWeGoDatabase.getDatabase(this, applicationScope) }
}