package com.anchor.api.util;

import com.anchor.api.data.currencies.*;
import com.anchor.api.data.info.DepositInfo;
import com.anchor.api.data.info.Fee;
import com.anchor.api.data.info.Info;
import com.anchor.api.data.info.WithdrawInfo;

import java.util.logging.Logger;

public class Util {
    public static final Logger LOGGER = Logger.getLogger(Util.class.getSimpleName());
    public static Info createTestInfo() {

        DepositInfo depositInfo = new DepositInfo();
        depositInfo.setUSD(new USD(true, 3.5, 1.5, 0.1, 100000.00));
        depositInfo.setZAR(new ZAR(true, 4.0, 2.0, 0.1, 100000.00));
        depositInfo.setEUR(new EUR(true, 4.5, 1.5, 0.1, 100000.00));
        depositInfo.setGBP(new GBP(true, 5.5, 1.5, 0.1, 100000.00));
        depositInfo.setCHF(new CHF(true, 3.5, 1.5, 0.1, 100000.00));
        depositInfo.setCNY(new CNY(true, 2.5, 1.5, 0.1, 100000.00));
        depositInfo.setBTC(new BTC(true, 2.5, 1.5, 0.1, 100000.00));
        depositInfo.setETH(new ETH(true, 2.5, 1.5, 0.1, 100000.00));

        WithdrawInfo withdrawInfo = new WithdrawInfo();
        withdrawInfo.setUSD(new USD(true, 3.5, 1.5, 0.1, 100000.00));
        withdrawInfo.setZAR(new ZAR(true, 4.0, 2.0, 0.1, 100000.00));
        withdrawInfo.setEUR(new EUR(true, 4.5, 1.5, 0.1, 100000.00));
        withdrawInfo.setGBP(new GBP(true, 5.5, 1.5, 0.1, 100000.00));
        withdrawInfo.setCHF(new CHF(true, 3.5, 1.5, 0.1, 100000.00));
        withdrawInfo.setCNY(new CNY(true, 2.5, 1.5, 0.1, 100000.00));
        withdrawInfo.setBTC(new BTC(true, 2.5, 1.5, 0.1, 100000.00));
        withdrawInfo.setETH(new ETH(true, 2.5, 1.5, 0.1, 100000.00));


        Info info = new Info();
        info.setDeposit(depositInfo);
        info.setWithdraw(withdrawInfo);
        info.setFee(new Fee(true));
        info.setAnchorId("anchor865869876876");

        LOGGER.info("\uD83D\uDCA6 \uD83D\uDCA6 Test Anchor Info: ".concat(info.toJson()).concat(" \uD83D\uDCA6 "));
        return info;
    }


}
