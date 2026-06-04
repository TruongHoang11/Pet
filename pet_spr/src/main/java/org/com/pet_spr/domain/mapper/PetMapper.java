package org.com.pet_spr.domain.mapper;

import org.com.pet_spr.domain.dto.response.PetDto;
import org.com.pet_spr.domain.entity.Pet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PetMapper {

    PetDto toDto(Pet pet);

    List<PetDto> toDtoList(List<Pet> pets);
}