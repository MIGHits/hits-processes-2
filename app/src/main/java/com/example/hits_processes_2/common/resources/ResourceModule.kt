package com.example.hits_processes_2.common.resources

import org.koin.dsl.module

val resourceModule = module {
    single<StringResourceProvider> { AndroidStringResourceProvider(get()) }
}
