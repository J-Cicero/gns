package com.backend.gns.user.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BanqueInfoResponse {
    private UUID trackingId;
    private String code;
    private String nom;
    private String logoUrl;
    private String compteCentralGns;
}
