package com.hyip.entity;

import lombok.Data;

/**
 * Кошелёк
 */
@Data
public class Wallet {
    /**
     * Количество денег в кошельке
     */
    private double summa;

    public Wallet() {
        this.summa = 0.0;
    }

    public Wallet(double summa) {
        this.summa = summa;
    }

    /**
     * Положить деньги в кошелёк
     *
     * @param summaToPut сумма, которую необходимо положить
     */
    synchronized public void put(double summaToPut) {
        this.summa += summaToPut;
    }

    /**
     * Снять деньги с кошелька
     *
     * @param summaToWithdraw сумма, которую необходимо снять с кошелька
     */
    synchronized public void withdraw(double summaToWithdraw) {
        if (summaToWithdraw < 0) {
            throw new RuntimeException(String.format(
                "Withdrawal summa must be greater than zero. %f",
                summaToWithdraw));
        }

        if (this.summa < summaToWithdraw) {
            throw new RuntimeException(String.format(
                "Not enough money. Summa = %f. Summa to withdraw = %f",
                this.summa,
                summaToWithdraw));
        }

        this.summa -= summaToWithdraw;
    }
}
