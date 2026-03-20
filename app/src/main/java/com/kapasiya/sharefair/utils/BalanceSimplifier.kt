package com.kapasiya.sharefair.utils

import kotlin.math.abs
import kotlin.math.min

data class Transaction(
    val fromUserId: String,
    val toUserId: String,
    val amount: Double
)

object BalanceSimplifier {
    /**
     * Simplifies the balances in a group to minimize the number of transactions.
     * This is the "Simplify Balances" feature.
     */
    fun simplify(netBalances: Map<String, Double>): List<Transaction> {
        // Only include users with significant balances
        val debtors = netBalances.filter { it.value < -0.01 }.toMutableMap()
        val creditors = netBalances.filter { it.value > 0.01 }.toMutableMap()
        
        val transactions = mutableListOf<Transaction>()
        
        // Convert to lists for easier iteration
        val sortedDebtors = debtors.toList().sortedBy { it.second }.toMutableList()
        val sortedCreditors = creditors.toList().sortedByDescending { it.second }.toMutableList()
        
        var i = 0
        var j = 0
        
        while (i < sortedDebtors.size && j < sortedCreditors.size) {
            val debtor = sortedDebtors[i]
            val creditor = sortedCreditors[j]
            
            val amount = min(abs(debtor.second), creditor.second)
            
            if (amount > 0.01) {
                transactions.add(Transaction(debtor.first, creditor.first, amount))
            }
            
            // Update balances
            val newDebtorBalance = debtor.second + amount
            val newCreditorBalance = creditor.second - amount
            
            sortedDebtors[i] = debtor.first to newDebtorBalance
            sortedCreditors[j] = creditor.first to newCreditorBalance
            
            // Move to next if balance is settled
            if (abs(sortedDebtors[i].second) < 0.01) i++
            if (abs(sortedCreditors[j].second) < 0.01) j++
        }
        
        return transactions
    }
}
