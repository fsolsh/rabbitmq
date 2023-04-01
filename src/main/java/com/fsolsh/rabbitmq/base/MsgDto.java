package com.fsolsh.rabbitmq.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @author andy
 * 消息实体
 */
@Data
public class MsgDto implements Serializable {

    private String platform;
    private String bussType;
    private String bussId;
    private String jsonMsg;

}
