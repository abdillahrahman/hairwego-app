package com.app.hairwego

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class MyApplication : Application (){
    private val applicationScope = CoroutineScope(SupervisorJob())
}