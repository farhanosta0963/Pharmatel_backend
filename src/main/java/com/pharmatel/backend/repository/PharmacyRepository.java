package com.pharmatel.backend.repository;

import com.pharmatel.backend.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {

    @Query(value = """
        select *
        from pharmacy p
        where p.location is not null
        order by ST_Distance(p.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography)
        """, nativeQuery = true)
    List<Pharmacy> findNearby(@Param("lat") double lat, @Param("lng") double lng);

    Optional<Pharmacy> findByAccountId(UUID accountId);
    Optional<Pharmacy> findByNameIgnoreCase(String name);
}
