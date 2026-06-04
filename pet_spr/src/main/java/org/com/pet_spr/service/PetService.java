package org.com.pet_spr.service;

import org.com.pet_spr.domain.dto.pagination.ResultPaginationDto;
import org.com.pet_spr.domain.dto.request.ReqCreatePet;
import org.com.pet_spr.domain.dto.request.ReqUpdatePet;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.PetDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PetService {
    PetDto createPet(ReqCreatePet req);

    PetDto updatePet(ReqUpdatePet req);

    CommonResponseDto  deletePet(Long id);

    CommonResponseDto deactivatePet(Long id);

    CommonResponseDto activatePet(Long id);


    List<PetDto> getMyPets();

    PetDto getPetDetail(Long id);

    ResultPaginationDto getAllPet(List<String> filter, Pageable pageable);




}