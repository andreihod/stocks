package com.andreih.stocks.commom

fun String.removeRepeatedWhiteSpaces() = replace("\\s+".toRegex(), " ")
