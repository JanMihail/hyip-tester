package com.hyip;

import com.hyip.entity.Hyip;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) throws InterruptedException {

        StrategyInvest myStrategy = new StrategyInvest(
            5,
            1000,
            1,
            30,
            10
        );
        myStrategy.run();

    }
}
