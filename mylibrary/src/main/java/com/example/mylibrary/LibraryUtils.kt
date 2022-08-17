package com.example.mylibrary

object LibraryUtils {
    fun printLibraryMessage(message: String) {
        println("Accessing library modules $message")
    }

    fun getClassName(): String {
        return LibraryUtils::class.java.name
    }
}