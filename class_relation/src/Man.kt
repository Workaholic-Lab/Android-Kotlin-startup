class Man(override var name: String, override var age: Int,val height:Double) : Human{
    override fun eat() {
        print("Man named $name is eating, who is $age years old")
    }

    override fun drink() {
        println("Man named $name is drinking, who is $age years old")
    }

    fun play()=when(name){
        "Tom"->"Basketball"
        "Alan"->"badminton"
        "Gary"->"tennis"
        else -> "play nothing!"
    }

    fun isAgeEnough():Boolean{
        return age>=18
    }

    fun buyFreeTicket():Boolean{
        if(isAgeEnough()||height>=1.4||name=="Alan"){
            println("you must pay, my dear")
            return false
        }
        else{
            println("you can take a free ticket")
            return true
        }
    }

}