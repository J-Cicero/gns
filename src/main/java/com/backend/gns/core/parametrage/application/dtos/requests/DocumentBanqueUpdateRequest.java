package com.backend.gns.core.parametrage.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.StatutDocument;

public record DocumentBanqueUpdateRequest(
        StatutDocument status
) {}