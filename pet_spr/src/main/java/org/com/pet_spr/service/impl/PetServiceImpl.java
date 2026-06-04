package org.com.pet_spr.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.constant.GenderEnum;
import org.com.pet_spr.constant.RoleConstant;
import org.com.pet_spr.domain.dto.pagination.ResultPaginationDto;
import org.com.pet_spr.domain.dto.request.ReqCreatePet;
import org.com.pet_spr.domain.dto.request.ReqUpdatePet;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.PetDto;
import org.com.pet_spr.domain.dto.response.UserDto;
import org.com.pet_spr.domain.entity.Pet;
import org.com.pet_spr.domain.entity.User;
import org.com.pet_spr.domain.mapper.PetMapper;
import org.com.pet_spr.domain.specification.FilterProcessor;
import org.com.pet_spr.domain.specification.SpecificationBuilder;
import org.com.pet_spr.exception.BadRequestException;
import org.com.pet_spr.exception.ForbiddenException;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.repository.PetRepository;
import org.com.pet_spr.service.PetService;
import org.com.pet_spr.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserService userService;
    private final PetMapper petMapper;

    @Override
    @Transactional
    public PetDto createPet(ReqCreatePet req) {
        log.info("[PET] Thêm thú cưng mới cho user hiện tại");

        User currentUser = userService.getUserLogin();

        Pet pet = new Pet();
        pet.setName(req.getName());
        pet.setSpecie(req.getSpecie());
        pet.setGender(GenderEnum.valueOf(req.getGender()));
        pet.setBirthday(req.getBirthday());
        pet.setWeight(req.getWeight());
        pet.setHealthStatus(req.getHealthStatus());
        pet.setUser(currentUser);

        petRepository.save(pet);
        log.info("[PET] Thêm thú cưng thành công | User ID: {}", currentUser.getId());
        return petMapper.toDto(pet);
    }

    @Override
    @Transactional
    public PetDto updatePet(ReqUpdatePet req) {
        log.info("[PET] Cập nhật thú cưng ID: {}", req.getId());

        User currentUser = userService.getUserLogin();
        Pet pet = getPetAndValidate(req.getId(), currentUser);

        pet.setName(req.getName());
        pet.setSpecie(req.getSpecie());
        pet.setGender(GenderEnum.valueOf(req.getGender()));
        pet.setBirthday(req.getBirthday());
        pet.setWeight(req.getWeight());
        pet.setHealthStatus(req.getHealthStatus());

        petRepository.save(pet);
        log.info("[PET] Cập nhật thành công thú cưng ID: {}", req.getId());
        return petMapper.toDto(pet);
    }

    @Override
    @Transactional
    public CommonResponseDto deletePet(Long id) {
        log.info("[PET] Xóa thú cưng ID: {}", id);

        User currentUser = userService.getUserLogin();
        Pet pet = getPetAndValidate(id, currentUser);

        pet.setDeleteFlag(Boolean.TRUE);
        pet.setActiveFlag(Boolean.FALSE);
        petRepository.save(pet);

        log.info("[PET] Xóa thành công thú cưng ID: {}", id);
        return new CommonResponseDto(true, "Deleted pet: " + pet.getName() + " successfully");
    }

    @Override
    @Transactional
    public CommonResponseDto deactivatePet(Long id) {
        log.info("[PET] Khóa thú cưng ID: {}", id);

        // Security config đã đảm bảo chỉ Admin vào được
        Pet pet = getPetById(id);

        if (Boolean.FALSE.equals(pet.getActiveFlag())) {
            throw new BadRequestException(ErrorMessage.Pet.ERR_PET_ALREADY_INACTIVE);
        }
        pet.setActiveFlag(Boolean.FALSE);
        petRepository.save(pet);

        log.info("[PET] Khóa thành công thú cưng ID: {}", id);
        return new CommonResponseDto(true, "Lock pet: " + pet.getName() + " successfully");
    }

    @Override
    @Transactional
    public CommonResponseDto activatePet(Long id) {
        log.info("[PET] Kích hoạt thú cưng ID: {}", id);

        // Security config đã đảm bảo chỉ Admin vào được
        Pet pet = getPetById(id);

        if (Boolean.TRUE.equals(pet.getActiveFlag())) {
            throw new BadRequestException(ErrorMessage.Pet.ERR_PET_ALREADY_ACTIVE,
                    new String[]{String.valueOf(id)});
        }
        pet.setActiveFlag(Boolean.TRUE);
        petRepository.save(pet);

        log.info("[PET] Mở khóa thành công thú cưng ID: {}", id);
        return new CommonResponseDto(true, "Unlock pet: " + pet.getName() + " successfully");
    }

    @Override
    public List<PetDto> getMyPets() {
        log.info("[PET] Lấy danh sách thú cưng của user hiện tại");

        User currentUser = userService.getUserLogin();
        List<Pet> pets = petRepository
                .findByUserIdAndDeleteFlagFalseAndActiveFlagTrue(currentUser.getId());

        log.info("[PET] User ID: {} | Số thú cưng: {}", currentUser.getId(), pets.size());
        return petMapper.toDtoList(pets);
    }

    @Override
    public PetDto getPetDetail(Long id) {
        log.info("[PET] Lấy chi tiết thú cưng ID: {}", id);

        User currentUser = userService.getUserLogin();
        Pet pet = getPetAndValidate(id, currentUser);

        log.info("[PET] Lấy chi tiết thành công thú cưng ID: {}", id);
        return petMapper.toDto(pet);
    }

    @Override
    public ResultPaginationDto getAllPet(List<String> filter, Pageable pageable) {
        SpecificationBuilder<Pet> spec = new SpecificationBuilder<>();
        FilterProcessor.process(spec, filter);

        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdDate").descending()
        );

        Page<Pet> petPage = petRepository.findAll(spec.build(), pageable);

        ResultPaginationDto resultPaginationDTO = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(petPage.getTotalPages());
        meta.setTotal(petPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(petMapper.toDtoList(petPage.getContent()));
        return resultPaginationDTO;
    }

    // Helper - tìm pet + check owner (dùng cho User)
    private Pet getPetAndValidate(Long petId, User currentUser) {
        Pet pet = getPetById(petId);

        if (!pet.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }
        return pet;
    }

    // Helper - chỉ tìm pet + check deleteFlag + activeFlag (dùng cho Admin)
    private Pet getPetById(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> {
                    log.warn("[NOT_FOUND] Không tìm thấy thú cưng ID: {}", petId);
                    return new NotFoundException(ErrorMessage.Pet.ERR_NOT_FOUND_ID,
                            new String[]{String.valueOf(petId)});
                });

        if (Boolean.TRUE.equals(pet.getDeleteFlag())) {
            throw new NotFoundException(ErrorMessage.Pet.ERR_NOT_FOUND_ID,
                    new String[]{String.valueOf(petId)});
        }

        if (Boolean.FALSE.equals(pet.getActiveFlag())) {
            throw new BadRequestException(ErrorMessage.Pet.ERR_PET_IS_LOCKED,
                    new String[]{String.valueOf(petId)});
        }

        return pet;
    }
}