package org.btbox.common.license.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义需要校验的License参数
 */
@Data
public class LicenseCheckModel implements Serializable{

    private static final long serialVersionUID = 8600137500316662317L;
    /**
     * 可被允许的IP地址(可不填)
     */
    private List<String> ipAddress;

    /**
     * 可被允许的MAC地址(可不填)
     */
    private List<String> macAddress;

    /**
     * 可被允许的CPU序列号(可不填)
     */
    private String cpuSerial;

    /**
     * 可被允许的主板序列号(可不填)
     */
    private String mainBoardSerial;


}
