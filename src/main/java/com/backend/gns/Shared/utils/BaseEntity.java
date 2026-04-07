package com.backend.gns.Shared.utils;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    @Column(updatable = false, length = 100)
    @CreatedBy
    private String createdBy;

    @Column(length = 100)
    @LastModifiedBy
    private String updatedBy;

    @Column(updatable = false, length = 36)
    @CreatedDate
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(length = 36)
    @LastModifiedDate
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
