package com.example.hits_processes_2.common.resources

import androidx.annotation.StringRes

interface StringResourceProvider {

    fun getString(@StringRes resId: Int): String
}
