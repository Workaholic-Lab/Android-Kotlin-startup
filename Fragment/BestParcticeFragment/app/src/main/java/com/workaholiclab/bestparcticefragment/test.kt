package com.workaholiclab.bestparcticefragment

/**
 * @Description:
 * @author: Gary
 * @date: Created on 2021/02/20 12:00
 * @since: Kotlin 1.4
 * @modified by:
 */


fun String.lettersCount():Int{
    var count=0
    for(char in this){
        if(char.isLetter())
            count++
    }
    return count
}


val count="ABDJ1234989!@)))".lettersCount()
