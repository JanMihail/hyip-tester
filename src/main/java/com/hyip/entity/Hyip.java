package com.hyip.entity;

import lombok.Data;

import java.util.UUID;

/**
 * Хайп проект
 */
@Data
public class Hyip {
    /**
     * Наименование проекта
     */
    private final String name;

    /**
     * Баланс
     */
    private final Wallet balans;

    /**
     * Тарифный план
     */
    private TariffPlan tariffPlan;

    public Hyip() {
        this.name = UUID.randomUUID().toString();
        this.balans = new Wallet();
    }

    public void openTariffPlan(long investDaysCount, double dayPercent, double investSumma) {
        tariffPlan = new TariffPlan(investDaysCount, dayPercent, investSumma);
        balans.withdraw(investSumma);
    }

    public double withdrawDailyProfit() {
        double sumWithdraw = tariffPlan.withdraw();
        balans.put(sumWithdraw);
        return sumWithdraw;
    }
}