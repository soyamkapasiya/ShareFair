package com.kapasiya.sharefair.utils

import kotlin.math.abs
import kotlin.math.min

data class SplitTransaction(
    val from: String, // User ID
    val to: String,   // User ID
    val amount: Double
)

object SplitOptimizer {

    /**
     * Simplifies the balances in a group to minimize the number of transactions.
     * @param netBalances A map of UserId to their net balance (positive means they are owed, negative means they owe).
     * @return A list of optimized transactions.
     */
    fun simplify(netBalances: Map<String, Double>): List<SplitTransaction> {
        val debtors = mutableListOf<Pair<String, Double>>()
        val creditors = mutableListOf<Pair<String, Double>>()

        // Filter out users with zero balance and separate into debtors and creditors
        netBalances.forEach { (userId, balance) ->
            if (balance < -0.01) {
                debtors.add(userId to abs(balance))
            } else if (balance > 0.01) {
                creditors.add(userId to balance)
            }
        }

        val transactions = mutableListOf<SplitTransaction>()
        var dIdx = 0
        var cIdx = 0

        val currentDebtors = debtors.toMutableList()
        val currentCreditors = creditors.toMutableList()

        while (dIdx < currentDebtors.size && cIdx < currentCreditors.size) {
            val (debtorId, debtAmount) = currentDebtors[dIdx]
            val (creditorId, creditAmount) = currentCreditors[cIdx]

            val amount = min(debtAmount, creditAmount)
            
            if (amount > 0.01) {
                transactions.add(SplitTransaction(debtorId, creditorId, amount))
            }

            // Update remaining balances
            currentDebtors[dIdx] = debtorId to (debtAmount - amount)
            currentCreditors[cIdx] = creditorId to (creditAmount - amount)

            if (currentDebtors[dIdx].second < 0.01) dIdx++
            if (currentCreditors[cIdx].second < 0.01) cIdx++
        }

        return transactions
    }
}
