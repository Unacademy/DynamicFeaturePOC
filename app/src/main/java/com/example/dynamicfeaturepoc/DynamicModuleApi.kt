package com.example.dynamicfeaturepoc

interface DynamicModuleApi {
    fun setName(name: String)
    fun getName(): String
    interface Provider {
        fun get(): DynamicModuleApi
    }
}