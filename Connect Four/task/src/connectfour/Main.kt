package connectfour


fun main() {
    println(
        "Connect Four\n" +
                "First player's name:"
    )
    val firstName = readLine()!!
    println("Second player's name:")
    val secondName = readLine()!!
    var result: List<Int>?
    while (true) {
        println(
            "Set the board dimensions (Rows x Columns)\n" +
                    "Press Enter for default (6 x 7)"
        )
        val dimensionSet = readLine()!!
        result = checkDimension(dimensionSet)
        when {
            result == null -> println("Invalid input")
            (result[0] in (5..9)) && (result[1] in (5..9)) -> break
            (result[0] < 5) || (result[0] > 9) -> println("Board rows should be from 5 to 9")
            (result[1] < 5) || (result[1] > 9) -> println("Board columns should be from 5 to 9")
        }
    }
    val rowNumber = result!![0]
    val colNumber = result[1]


    var gameNumber = checkGameNumber()
    while (gameNumber == null) {
        println("Invalid input")
        gameNumber = checkGameNumber()
    }

    println("$firstName VS $secondName")
    println("$rowNumber X $colNumber board")

    fun gamePlay(isFirstPlayerStart: Boolean): Map<String, Int>? {
        val colStack = MutableList(rowNumber) { ' ' }
        val stateStack = MutableList(colNumber) { colStack.toMutableList() }
        printState(stateStack, rowNumber, colNumber)

        var isFirstPlayer = isFirstPlayerStart
        while (true) {
            val userName = if (isFirstPlayer) firstName else secondName
            val token = if (isFirstPlayer) 'o' else '*'
            println("$userName's turn:")
            val pos = readLine()
            when {
                Regex("\\d+").matches(pos.toString()) -> {
                    when (val col = pos?.toInt()) {
                        in 1..colNumber -> {
                            when (val index = stateStack[col!! - 1].indexOfFirst { it == ' ' }) {
                                -1 -> println("Column $col is full")
                                else -> {
                                    stateStack[col - 1][index] = token
                                    printState(stateStack, rowNumber, colNumber)
                                    if (stateStack.all { list -> list.all { it == '*' || it == 'o' } }) {
                                        println("It is a draw")
                                        return mapOf(firstName to 1, secondName to 1)
                                    }
                                    when {
                                        // vertical
                                        index >= 3 -> {
                                            if (token == stateStack[col - 1][index - 1] &&
                                                token == stateStack[col - 1][index - 2] &&
                                                token == stateStack[col - 1][index - 3]
                                            ) {
                                                println("Player $userName won")
                                                return mapOf(userName to 2)
                                            }
                                        }
                                    }
                                    when {
                                        // horizon
                                        col - 1 - 3 >= 0 -> {
                                            if (token == stateStack[col - 2][index] &&
                                                token == stateStack[col - 3][index] &&
                                                token == stateStack[col - 4][index]
                                            ) {
                                                println("Player $userName won")
                                                return mapOf(userName to 2)
                                            }
                                            when {
                                                index >= 3 -> {
                                                    if (token == stateStack[col - 2][index - 1] &&
                                                        token == stateStack[col - 3][index - 2] &&
                                                        token == stateStack[col - 4][index - 3]
                                                    ) {
                                                        println("Player $userName won")
                                                        return mapOf(userName to 2)
                                                    }
                                                }
                                                rowNumber - 1 - index >= 3 -> {
                                                    if (token == stateStack[col - 2][index + 1] &&
                                                        token == stateStack[col - 3][index + 2] &&
                                                        token == stateStack[col - 4][index + 3]
                                                    ) {
                                                        println("Player $userName won")
                                                        return mapOf(userName to 2)
                                                    }
                                                }
                                            }
                                        }
                                        ((col - 1) + 3) < colNumber -> {
                                            if (token == stateStack[col][index]
                                                && token == stateStack[col + 1][index] &&
                                                token == stateStack[col + 2][index]
                                            ) {
                                                println("Player $userName won")
                                                return mapOf(userName to 2)
                                            }
                                            when {
                                                index >= 3 -> {
                                                    if (token == stateStack[col][index - 1] &&
                                                        token == stateStack[col + 1][index - 2] &&
                                                        token == stateStack[col + 2][index - 3]
                                                    ) {
                                                        println("Player $userName won")
                                                        return mapOf(userName to 2)
                                                    }
                                                }
                                                rowNumber - 1 - index >= 3 -> {
                                                    if (token == stateStack[col][index + 1] &&
                                                        token == stateStack[col + 1][index + 2] &&
                                                        token == stateStack[col + 2][index + 3]
                                                    ) {
                                                        println("Player $userName won")
                                                        return mapOf(userName to 2)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    isFirstPlayer = !isFirstPlayer
                                }
                            }
                        }
                        else -> println("The column number is out of range (1 - $colNumber)")
                    }
                }
                pos == "end" -> return null
                else -> println("Incorrect column number")
            }
        }
    }
    when (gameNumber) {
        1 -> {
            println("Single game")
            gamePlay(true)
        }
        else -> {
            println("Total $gameNumber games")
            var firstNameScore = 0
            var secondNameScore = 0
            var isFirstPlayerStart = true
            for (i in 1..gameNumber) {
                println("Game #$i")
                when (val gameScore = gamePlay(isFirstPlayerStart)) {
                    null -> break
                    else -> {
                        for (k in gameScore.entries) {
                            when (k.key) {
                                firstName -> firstNameScore += k.value
                                secondName -> secondNameScore += k.value
                            }
                        }
                    }
                }
                println(
                    "Score\n" +
                            "$firstName: $firstNameScore $secondName: $secondNameScore"
                )
                isFirstPlayerStart = !isFirstPlayerStart
            }
        }
    }

    println("Game over!")
}

fun checkDimension(dim: String): List<Int>? {
    if (dim == "") return listOf(6, 7)
    val dimCheck = Regex("\\s*(\\d+)\\s*[xX]\\s*(\\d+)\\s*")
    return if (dimCheck.matches(dim)) {
        val res = mutableListOf<Int>()
        for (i in "\\d".toRegex().findAll(dim)) {
            res.add(i.value.toInt())
        }
        res
    } else null
}

fun checkGameNumber(): Int? {

    println(
        """
        Do you want to play single or multiple games?
        For a single game, input 1 or press Enter
        Input a number of games:
    """.trimIndent()
    )
    val num = readLine()!!
    return when {
        num == "" -> 1
        Regex("\\d+").matches(num) -> {
            when {
                num.toInt() > 0 -> num.toInt()
                else -> null
            }
        }
        else -> null
    }
}

fun printState(stateStack: MutableList<MutableList<Char>>, rowNumber: Int, colNumber: Int) {
    for (i in 1..colNumber) {
        print(" $i")
    }
    println()
    var testGrid = ""
    for (i in 1..rowNumber) {
        for (j in 0 until colNumber) {
            val cell = stateStack[j][rowNumber - i]
            testGrid += "║$cell"
        }
        testGrid += "║\n"
    }
    print(testGrid)
    println("╚" + "═╩".repeat(colNumber - 1) + "═╝")
}

