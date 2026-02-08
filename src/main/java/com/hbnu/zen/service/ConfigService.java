package com.hbnu.zen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.dto.ReservationRules;
import com.hbnu.zen.mapper.SysConfigMapper;
import com.hbnu.zen.mybatis.entity.SysConfig;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {
    private static final String RESERVATION_RULES = "reservation_rules";

    private final SysConfigMapper sysConfigMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConfigService(SysConfigMapper sysConfigMapper) {
        this.sysConfigMapper = sysConfigMapper;
    }

    public ReservationRules getReservationRules() {
        SysConfig config = sysConfigMapper.selectByKey(RESERVATION_RULES);
        if (config == null) {
            return defaultRules();
        }
        try {
            return objectMapper.readValue(config.getConfigValue(), ReservationRules.class);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "预约规则解析失败");
        }
    }

    public void updateReservationRules(ReservationRules rules) {
        if (rules.getTimeSlotMinutes() == null || rules.getTimeSlotMinutes() <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "时间粒度不合法");
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(RESERVATION_RULES);
        try {
            config.setConfigValue(objectMapper.writeValueAsString(rules));
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "预约规则保存失败");
        }
        sysConfigMapper.upsert(config);
    }

    private ReservationRules defaultRules() {
        ReservationRules rules = new ReservationRules();
        rules.setTimeSlotMinutes(30);
        rules.setAdvanceDays(7);
        rules.setDailyLimit(2);
        rules.setMinDurationMinutes(30);
        rules.setMaxDurationMinutes(180);
        rules.setApprovalRequired(true);
        return rules;
    }
}
