package com.backend.gns.core.systemstatus.dtos;

import com.backend.gns.core.parametrage.domain.enums.SystemStatus;

public record SystemStatusResponse(SystemStatus currentStatus, boolean isPaymentEnabled) {}
