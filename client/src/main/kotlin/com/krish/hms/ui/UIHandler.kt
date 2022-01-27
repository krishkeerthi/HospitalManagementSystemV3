
package com.krish.hms.ui

class UIHandler {

    fun readData(field: String): String{
        println("Enter $field:")
        var input = readLine()
        while(input == null || input == "")
            input = readLine()
        return input
    }

    fun writeData(message: String){
        println(message)
    }

    fun <T> readOptions(options: Array<T>): Int{
        var i= 1
        for(option in options)
            println("${i++} . $option")
        val result = readData("option").toIntOrNull()
        return result?.minus(1) ?: -1
    }

}