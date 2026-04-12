package com.example.hits_processes_2.common.resources

import android.content.Context
import androidx.annotation.StringRes

class AndroidStringResourceProvider(
    private val context: Context,
) : StringResourceProvider {

    override fun getString(@StringRes resId: Int): String = context.getString(resId)
}
