package com.example.dynamicfeature

import com.example.dynamicfeaturepoc.DynamicModuleApi

class DynamicModuleApiImpl : DynamicModuleApi {
    var className = ""
    override fun setName(name: String) {
        className = name
    }

    override fun getName(): String {
        return className
    }

    companion object Provider : DynamicModuleApi.Provider {
        override fun get(): DynamicModuleApi = DynamicModuleApiImpl()
    }
}