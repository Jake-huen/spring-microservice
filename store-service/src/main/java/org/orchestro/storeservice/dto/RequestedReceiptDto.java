package org.orchestro.storeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestedReceiptDto {
    private String coffeeId;
    private String coffeeName;
    private Integer qty;
    private String userId;
    private String orderId;
    private Date createdAt;
}
