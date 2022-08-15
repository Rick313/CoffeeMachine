package machine

class Do<T>(val value:  T) {
    fun fold() = value
    fun <R> map(f: (value: T) -> R): Do<R> = Do(f(value))
    fun <R> chain(f: (value: T) -> Do<R>): Do<R> = f(value)
    fun <R> apply(f: (value: T) -> R): R = f(value)
    fun tap(f: (value: T) -> Unit): Do<T> {
        f(value)
        return Do(value)
    }
}

data class Coffee(val water: Int = 0, val milk: Int = 0, val beans: Int = 0, val price: Int = 0) {
    companion object {
        val ESPRESSO = Coffee(250, 0, 16, 4)
        val LATTE = Coffee(350, 75, 20, 7)
        val CAPPUCCINO = Coffee(200, 100, 12, 6)
    }
}

data class Machine(val water: Int = 400, val milk: Int = 540, val beans: Int = 120,val cups: Int = 9, val money: Int = 550) {
    companion object {   
        val DRINKS = listOf(Pair(1, Coffee.ESPRESSO), Pair(2, Coffee.LATTE), Pair(3, Coffee.CAPPUCCINO))
        
        val BUY = "buy"
        val FILL = "fill"
        val TAKE = "take"
        val EXIT = "exit"
        val REAMING = "remaining"
        val ACTIONS = listOf(BUY, FILL, TAKE, REAMING, EXIT)
    
        fun concat(a: Machine, b: Machine): Machine = Machine(
            a.water + b.water,
            a.milk + b.milk,
            a.beans + b.beans,
            a.cups + b.cups,
            a.money + b.money
        )
        fun infos(machine: Machine): List<String> = listOf(
            "${machine.water} ml of water",
            "${machine.milk} ml of milk",
            "${machine.beans} g of coffee beans",
            "${machine.cups} disposable cups",
            "$${machine.money} of money",
        )
        fun from(list: List<Int>): Machine = if(list.size < 5) from(list.plus(0)) else list.let { (water, milk,beans, cups, money) -> Machine(water, milk,beans, cups, money) }
        fun prepare(drink: Coffee, machine: Machine): Machine = Machine.concat(machine, Machine(-drink.water, -drink.milk, -drink.beans, -1, drink.price))
    }
}

fun input(x: String): Do<Int> = Do(x)
    .tap(::print)
    .map { _ -> readln().toInt() }

fun show(machine: Machine): Do<Machine> = Do("\nThe coffee machine has:")
    .tap(::println)
    .tap { _ -> Machine.infos(machine).plus("").forEach(::println) }
    .map { _ -> machine }

/* MACHINE ACTIONS */
fun buy(machine: Machine): Do<Machine> = Do("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: > ")
    .map { x -> 
        print(x)
        readln()
    }    
    .chain { choice -> 
        if(choice == "back") Do(machine)
        else if(choice in "1".."3") Do(Machine.DRINKS.find { (code) -> code == choice.trim().toInt() }!!)
            .chain { (_, drink) -> 
                when(true) {
                    (drink.water > machine.water) -> Do("Sorry, not enough water!\n").map(::println).map { _ -> machine } 
                    (drink.milk  > machine.milk)  -> Do("Sorry, not enough milk!\n").map(::println).map { _ -> machine } 
                    (drink.beans > machine.beans) -> Do("Sorry, not enough beans!\n").map(::println).map { _ -> machine } 
                    else                          -> Do("I have enough resources, making you a coffee!\n").map(::println).map { _ -> Machine.prepare(drink, machine) }
                }
            }
        else Do(machine)
    }

fun take(machine: Machine): Do<Machine> = Do("I gave you $${machine.money}\n")
    .map(::println)
    .map { _  -> Machine.concat(machine, Machine(0, 0, 0, 0, -machine.money))}

fun fill(machine: Machine) = Do(
        listOf(
            "Write how many ml of water do you want to add: > ",
            "Write how many ml of milk do you want to add: > ",
            "Write how many grams of coffee beans do you want to add: > ",
            "Write how many disposable cups of coffee do you want to add: > "
        ).map(::input)
         .map { x -> x.fold() }
    )
    .tap { _ -> println() }
    .map { supply -> Machine.concat(machine, Machine.from(supply)) }  
       
fun command(machine: Machine): Do<Machine> = Do(Machine.ACTIONS)
    .tap { actions -> print("Write action (${actions.joinToString(", ")}): > ") }
    .map { actions -> Pair(actions, readln().trim()) }
    .chain { (actions, action) -> 
        if(actions.contains(action))
            when(action) {
                Machine.BUY     -> buy(machine).chain(::command)
                Machine.TAKE    -> take(machine).chain(::command)
                Machine.FILL    -> fill(machine).chain(::command)
                Machine.REAMING -> Do(machine).tap(::show).chain(::command)
                Machine.EXIT    -> Do(machine)
                else            -> Do(machine)
            }
        else command(machine)
    }
*/
/* ---------------- */

fun main() {
    Do(Machine()).chain(::command)
}