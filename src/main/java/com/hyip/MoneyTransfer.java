package com.hyip;

import com.hyip.entity.Hyip;
import com.hyip.entity.Wallet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoneyTransfer {
    public static void transfer(Wallet walletFrom, Hyip hyipTo, double sum) {
        walletFrom.withdraw(sum);
        hyipTo.getBalans().put(sum);

        log.info("Transfer {} from {} to {}", sum, walletFrom, hyipTo);
    }

    public static void transfer(Hyip hyipFrom, Wallet walletTo, double sum) {
        hyipFrom.getBalans().withdraw(sum);
        walletTo.put(sum);

        log.info("Transfer {} from {} to {}", sum, hyipFrom, walletTo);
    }
}
