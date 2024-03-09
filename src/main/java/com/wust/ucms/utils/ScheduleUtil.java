package com.wust.ucms.utils;

import com.wust.ucms.mapper.ClubInfoMapper;
import com.wust.ucms.pojo.ClubInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ScheduleUtil {

    @Autowired
    ClubInfoMapper club;

    @Scheduled(cron = "0 0 0 1 3 ?", zone = "Asia/Shanghai")
    public void resetFund() {
        List<ClubInfo> clubList = club.selectAllActiveClubInfo();
        for (ClubInfo c : clubList) {
            c.setTotalFund(BigDecimal.ZERO);
            c.setSurplusFund(BigDecimal.ZERO);
            int flag = club.updateById(c);
            while (flag <= 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                flag = club.updateById(c);
            }
        }
    }
}
