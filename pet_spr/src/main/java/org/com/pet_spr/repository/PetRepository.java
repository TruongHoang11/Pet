package org.com.pet_spr.repository;

import org.com.pet_spr.domain.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {
    Optional<Pet> findByUserId(String userId);

    List<Pet> findByUserIdAndDeleteFlagFalseAndActiveFlagTrue(String userId);
}