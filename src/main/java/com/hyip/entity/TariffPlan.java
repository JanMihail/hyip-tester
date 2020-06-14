package com.hyip.entity;

import lombok.Data;

/**
 * Тарифный план хайпа. Один вывод = 1 день
 */
@Data
public class TariffPlan {
    /**
     * Количество дней инвестирования
     */
    private final long investDaysCount;

    /**
     * Дневная ставка в процентах от суммы инвестирования
     */
    private final double dayPercent;

    /**
     * Сумма инвестиций
     */
    private final double investSumma;

    /**
     * Количество совершенных выводов
     */
    private long withdrawCount;

    public enum Status {ACTIVE, CLOSED}

    /**
     * Статус плана
     */
    private Status status;

    public TariffPlan(long investDaysCount, double dayPercent, double investSumma) {
        this.investDaysCount = investDaysCount;
        this.dayPercent = dayPercent;
        this.investSumma = investSumma;
        this.withdrawCount = 0;
        this.status = Status.ACTIVE;
    }

    public double withdraw() {
        if (this.status.equals(Status.CLOSED)) {
            throw new RuntimeException("Tariff plan is closed! " + this.toString());
        }

        this.withdrawCount++;

        if (this.withdrawCount < investDaysCount) {
            return investSumma * dayPercent / 100.0;
        }

        status = Status.CLOSED;
        return investSumma + investSumma * dayPercent / 100.0;
    }
}
