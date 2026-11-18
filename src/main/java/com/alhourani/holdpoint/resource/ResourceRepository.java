package com.alhourani.holdpoint.resource;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;




public interface ResourceRepository extends JpaRepository<Resource , UUID> {


    Optional<Resource> findByResourceKey(String resourceKey);

    boolean existsByResourceKey(String resourceKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r FROM Resource r
        WHERE r.resourceKey = :resourceKey
    """)

    Optional<Resource> findByResourceKeyForUpdate(
        @Param("resourceKey") String resourceKey
    );



    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r FROM Resource r
        WHERE r.id = :resourceId
    """)
    Optional<Resource> findByIdForUpdate(
        @Param("resourceId") UUID resourceId
    );


}
