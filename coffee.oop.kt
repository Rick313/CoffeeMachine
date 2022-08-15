package machine

data class Coffee(val water: Int = 0, val milk: Int = 0, val beans: Int = 0, val price: Int = 0) {
  companion object {
      val ESPRESSO = Coffee(250, 0, 16, 4)
      val LATTE = Coffee(350, 75, 20, 7)
      val CAPPUCCINO = Coffee(200, 100, 12, 6)
  }
}

class Machine(var water: Int = 400, var milk: Int = 540, var beans: Int = 120,var cups: Int = 9, var money: Int = 550) {
    private val drinks = listOf(Pair("1", Coffee.ESPRESSO), Pair("2", Coffee.LATTE), Pair("3", Coffee.CAPPUCCINO))
    fun start() {
        print("Write action (buy, fill, take, remaining, exit): > ")
        when(readln().trim()) {
            "buy"     -> buy()
            "fill"    -> fill()
            "take"    -> take()
            "remaining" -> remaining()
            "exit"    -> Unit
            else      -> start()
        }
    }

    private fun buy(): Unit {
        println("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: > ")
        val choice = readln()
        if(choice == "back") return start()
        val existing = drinks.find { (code) ->  code == choice }
        if(existing == null) return start()
        val (_, drink) = existing
        when(true) {
            (drink.water > water) -> println("Sorry, not enough water!\n")
            (drink.milk  > milk)  -> println("Sorry, not enough milk!\n") 
            (drink.beans > beans) -> println("Sorry, not enough beans!\n") 
            else                         -> {
                println("I have enough resources, making you a coffee!\n")
                water -= drink.water
                milk -= drink.milk
                beans -= drink.beans
                cups -= 1
                money += drink.price 
            }
        }
        return start()
    }
    private fun fill(): Unit {
        listOf(
            "Write how many ml of water do you want to add: > ",
            "Write how many ml of milk do you want to add: > ",
            "Write how many grams of coffee beans do you want to add: > ",
            "Write how many disposable cups of coffee do you want to add: > "
        )
        .map { x -> 
            print(x)
            readln().toInt()
        }
        .let { (water, milk, beans, cups) -> 
            this.water += water
            this.milk += milk
            this.beans += beans
            this.cups += cups
        }
        return start()
    }
    private fun take(): Unit {
        println("\nI gave you $${money}")
        money = 0
        return start()
    }
    private fun remaining(): Unit {
        listOf(
            "\nThe coffee machine has:",
            "${water} ml of water",
            "${milk} ml of milk",
            "${beans} g of coffee beans",
            "${cups} disposable cups",
            "$${money} of money\n",
        ).forEach(::println)
        return start()
    }
}

fun main() { 
    Machine().start()
}