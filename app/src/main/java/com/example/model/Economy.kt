package com.example.model

data class TransactionRecord(
    val id: String,
    val description: String,
    val amountTaka: Int,
    val timestampMs: Long,
    val isDebit: Boolean
)

data class PlayerEconomy(
    var takaBalance: Int = 12750, // Initial in-game currency earned from training & lobby matches
    var earnedCoins: Int = 350,
    val transactionHistory: MutableList<TransactionRecord> = mutableListOf()
) {
    fun canAfford(price: Int): Boolean = takaBalance >= price

    fun purchase(itemId: String, itemName: String, price: Int): Boolean {
        if (canAfford(price)) {
            takaBalance -= price
            transactionHistory.add(
                0,
                TransactionRecord(
                    id = "tx_${System.currentTimeMillis()}",
                    description = "Purchased $itemName ($itemId)",
                    amountTaka = price,
                    timestampMs = System.currentTimeMillis(),
                    isDebit = true
                )
            )
            return true
        }
        return false
    }

    fun awardMatchEarnings(lootTaka: Int, eliminations: Int, isVictory: Boolean): Int {
        var total = lootTaka + (eliminations * 500)
        if (isVictory) total += 2000
        takaBalance += total
        transactionHistory.add(
            0,
            TransactionRecord(
                id = "tx_match_${System.currentTimeMillis()}",
                description = "Match Reward: ${if (isVictory) "BOOYAH Victory!" else "Survival"} ($eliminations kills)",
                amountTaka = total,
                timestampMs = System.currentTimeMillis(),
                isDebit = false
            )
        )
        return total
    }
}
