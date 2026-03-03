package com.gap.backendgap.dto;

import lombok.Data;


import java.math.BigDecimal;

@Data
public class CashItemDTO {

    private BigDecimal denomination; // 1000 , 5000 ...
    private Integer quantity;        // عدد الأوراق

}
