package com.hyip;

import com.hyip.entity.Hyip;
import com.hyip.entity.TariffPlan;
import com.hyip.entity.Wallet;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Стратегия инвестирования в хайп-проекты, когда мы диверсифицируем сумму между заданным количеством хайпов, и
 * после скама всегда поддерживаем диверсификацию на заранее заданном количестве хайпов вкладывая туда доход, который
 * успели получить с других работающих хайпов.
 * Стратегию скама мы задаем на входе.
 */
@Slf4j
@ToString
public class StrategyInvest {
    /**
     * Количество хайпов для диверсификации инвестиционной суммы
     */
    private final long hyipCount;

    /**
     * Сумма инвестирования
     */
    private final double investSumma;

    /**
     * Количество хайпов, которые будут скамиться
     */
    private final long cntHyipScam;

    /**
     * Периодичность скама. Когда будет проходить заданное количество дней, то будет скамиться
     * {@link #cntHyipScam} хайпов
     */
    private final long scamCycleDays;

    /**
     * Кол-во дней тестирования стратегии. Кодга это количество дней пройдет завершаем цикл инвестирования,
     * выводим все деньги, которые можно вывести и смотрим результаты
     */
    private final long cntDaysToTest;

    /**
     * Наш виртуальный кошелёк
     */
    private final Wallet myWallet;

    /**
     * Список хайпов
     */
    private final List<Hyip> hyips;

    /**
     * Количество пройденных дней по стратегии
     */
    private long dayCounter;

    public StrategyInvest(long hyipCount, double investSumma, long cntHyipScam, long scamCycleDays, long cntDaysToTest) {
        this.hyipCount = hyipCount;
        this.investSumma = investSumma;
        this.cntHyipScam = cntHyipScam;
        this.scamCycleDays = scamCycleDays;
        this.cntDaysToTest = cntDaysToTest;

        myWallet = new Wallet(investSumma);
        hyips = new ArrayList<>();
        dayCounter = 0;
    }

    public void run() {
        log.info("---");
        log.info("--------------------------- START ---------------------------");
        log.info("");
        log.info("Start state: {}", this.toString());

        // Открываем проекты и раскладываем инвестиционную сумму
        initHyips();

        // Запускаем тестирование стратегии
        while (!hyips.isEmpty()) {
            dayCounter++;

            // Выводим дневной профит со всех открытых проектов
            hyips.forEach(this::withdrawDailyProfit);

            log.info("Day {}. {}", dayCounter, myWallet);

            // Запускаем стратегию скама
            scamIfNeed();

            if (dayCounter > cntDaysToTest) {
                continue;
            }

            // Реинвестируем, если тарифный план закончился
            hyips.forEach(this::reinvestIfNeed);

            // Довкладываем, если хайпы скамнулись
            putToNewHyipsIfNeed();
        }

        log.info("");
        log.info("Finish state: {}", this.toString());
        log.info("");
        log.info("--------------------------- FINISH ---------------------------");
        log.info("---");
    }

    private void initHyips() {
        log.info("");
        log.info("Init hyips...");

        for (int i = 0; i < this.hyipCount; ++i) {
            addNewHyip();
        }
    }

    private void addNewHyip() {
        // Делим сумму инвестирования в равных долях между всеми проектами
        double sumToHyip = this.investSumma / this.hyipCount;

        Hyip hyip = new Hyip();

        MoneyTransfer.transfer(myWallet, hyip, sumToHyip);
        openTariffPlan(hyip, sumToHyip);

        this.hyips.add(hyip);
        log.info("New {}", hyip);
    }

    /**
     * Открывает в указанном хайпе тарифный план, который тестируется в этой стратегии
     */
    private void openTariffPlan(Hyip hyip, double summa) {
        hyip.openTariffPlan(20, 1.5, summa);
    }

    private void withdrawDailyProfit(Hyip hyip) {
        if (hyip.getTariffPlan() == null || hyip.getTariffPlan().getStatus().equals(TariffPlan.Status.CLOSED)) {
            return;
        }

        hyip.withdrawDailyProfit();
        MoneyTransfer.transfer(hyip, myWallet, hyip.getBalans().getSumma());
    }

    private void scamIfNeed() {
        if (dayCounter % scamCycleDays == 0) {
            int i = 0;
            while (i < cntHyipScam && !hyips.isEmpty()) {
                Hyip hyipScam = hyips.get(new Random().nextInt(hyips.size()));
                log.info("Scam. {}", hyipScam);
                hyips.remove(hyipScam);
                i++;
            }
        }
    }

    private void reinvestIfNeed(Hyip hyip) {
        if (hyip.getTariffPlan() == null || hyip.getTariffPlan().getStatus().equals(TariffPlan.Status.CLOSED)) {
            double sumToHyip = this.investSumma / this.hyipCount;

            MoneyTransfer.transfer(myWallet, hyip, sumToHyip);
            openTariffPlan(hyip, sumToHyip);
        }
    }

    private void putToNewHyipsIfNeed() {
        long needNew = hyipCount - hyips.size();

        for (int i = 0; i < needNew; ++i) {
            addNewHyip();
        }
    }
}
