package org.com.pet_spr.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.pet_spr.constant.ErrorMessage;
import org.com.pet_spr.domain.dto.request.ReqCreateShippingAddress;
import org.com.pet_spr.domain.dto.request.ReqUpdateShippingAddress;
import org.com.pet_spr.domain.dto.response.CommonResponseDto;
import org.com.pet_spr.domain.dto.response.ShippingAddressDto;
import org.com.pet_spr.domain.entity.ShippingAddress;
import org.com.pet_spr.domain.entity.User;
import org.com.pet_spr.domain.mapper.ShippingAddressMapper;
import org.com.pet_spr.exception.ForbiddenException;
import org.com.pet_spr.exception.NotFoundException;
import org.com.pet_spr.repository.ShippingAddressRepository;
import org.com.pet_spr.service.ShippingAddressService;
import org.com.pet_spr.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingAddressImpl implements ShippingAddressService {

    private final ShippingAddressRepository shippingAddressRepository;
    private final UserService userService;
    private final ShippingAddressMapper shippingAddressMapper;

    @Override
    @Transactional
    public ShippingAddressDto createShippingAddress(ReqCreateShippingAddress req) {
        User currentUser = userService.getUserLogin();
        long countAddress = shippingAddressRepository.countByUserId(currentUser.getId());

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setFullName(req.getFullName());
        shippingAddress.setPhone(req.getPhone());
        shippingAddress.setAddressDetail(req.getAddressDetail());
        shippingAddress.setWard(req.getWard());
        shippingAddress.setDistrict(req.getDistrict());
        shippingAddress.setProvince(req.getProvince());
        shippingAddress.setUser(currentUser);
        if(countAddress == 0){
            // Địa chỉ đầu tiên → auto set default
            log.info("[ADDRESS] Địa chỉ đầu tiên → auto set isDefault = true");
            shippingAddress.setIsDefault(true);
        } else if(Boolean.TRUE.equals(req.getIsDefault())){
            // User muốn set làm default → bỏ default địa chỉ cũ
            log.info("[ADDRESS] Set địa chỉ mới làm default, bỏ default địa chỉ cũ");
            shippingAddressRepository.findByUserIdAndIsDefaultTrue(currentUser.getId())
                    .ifPresent(oldDefaultAddress -> {
                        oldDefaultAddress.setIsDefault(false);
                        shippingAddressRepository.save(oldDefaultAddress);
                    });
            shippingAddress.setIsDefault(true);
        } else {
            shippingAddress.setIsDefault(false);
        }

        shippingAddressRepository.save(shippingAddress);
        log.info("[ADDRESS] Thêm địa chỉ thành công | User ID: {}", currentUser.getId());

        return shippingAddressMapper.toDto(shippingAddress);
    }

    @Override
    @Transactional
    public ShippingAddressDto updateShippingAddress(ReqUpdateShippingAddress req) {
        User currentUser = userService.getUserLogin();
        ShippingAddress shippingAddress = shippingAddressRepository.findById(req.getId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.ShippingAddress.ERR_NOT_FOUND_ID, new String[]{String.valueOf(req.getId())})
        );
        if(!currentUser.getId().equals(shippingAddress.getUser().getId())){
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }
        //isDefault = true Chỉ xử lý đổi default khi địa chỉ hiện tại chưa phải default
        //isDefault = false Không cần xử lý gì thêm
        if(Boolean.TRUE.equals(req.getIsDefault()) && !Boolean.TRUE.equals(shippingAddress.getIsDefault())){
            // User muốn set làm default → bỏ default địa chỉ cũ
            log.info("[ADDRESS] Đổi default → bỏ default địa chỉ cũ");
             shippingAddressRepository.findByUserIdAndIsDefaultTrue(currentUser.getId())
                    .ifPresent(oldDefaultAddress -> {
                        oldDefaultAddress.setIsDefault(false);
                        shippingAddressRepository.save(oldDefaultAddress);
                    });
        }
        shippingAddress.setFullName(req.getFullName());
        shippingAddress.setPhone(req.getPhone());
        shippingAddress.setAddressDetail(req.getAddressDetail());
        shippingAddress.setWard(req.getWard());
        shippingAddress.setDistrict(req.getDistrict());
        shippingAddress.setProvince(req.getProvince());
        shippingAddress.setIsDefault(req.getIsDefault());
        return shippingAddressMapper.toDto(shippingAddress);
    }

    @Override
    public CommonResponseDto deleteShippingAddress(Long addressId) {

        ShippingAddress shippingAddress = shippingAddressRepository.findById(addressId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.ShippingAddress.ERR_NOT_FOUND_ID, new String[]{String.valueOf(addressId)})
        );

        User currentUser = userService.getUserLogin();
        if(!currentUser.getId().equals(shippingAddress.getUser().getId())){
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }
        // nếu xoá địa chỉ set là mặc định thì sẽ set địa chỉ khác làm mặc định
        // không phải mặc định thì xoá luôn
        if(Boolean.TRUE.equals(shippingAddress.getIsDefault())){
            shippingAddressRepository.findByUserId(currentUser.getId()).stream()
                    //loai tru dia chi dang xoa
                    .filter(address -> !address.getId().equals(shippingAddress.getId()))
                    .findFirst()
                    .ifPresent(newDefault ->{
                        newDefault.setIsDefault(true);
                        shippingAddressRepository.save(newDefault);
                        log.info("[ADDRESS] Set địa chỉ ID: {} làm default mới", newDefault.getId());
                    });

        }
        shippingAddressRepository.delete(shippingAddress);
        return new CommonResponseDto(true, "Delete shipping address successfully");
    }

    @Override
    public List<ShippingAddressDto> getAllShippingAddress() {

        User currentUser = userService.getUserLogin();
         List<ShippingAddressDto> shippingAddressDtos = shippingAddressRepository.findByUserId(currentUser.getId())
                 .stream()
                 .map(address -> shippingAddressMapper.toDto(address))
                 .toList();

        return shippingAddressDtos;
    }

    @Override
    @Transactional
    public CommonResponseDto setDefaultShippingAddress(Long addressId) {
        log.info("[ADDRESS] Đặt địa chỉ ID: {} làm mặc định", addressId);
        User currentUser = userService.getUserLogin();
        ShippingAddress address = shippingAddressRepository.findById(addressId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.ShippingAddress.ERR_NOT_FOUND_ID, new String[]{String.valueOf(addressId)})
        );
        //đã là địa chi mặc định rồi thì k cần làm gì
        if(Boolean.TRUE.equals(address.getIsDefault())){
            log.info("[ADDRESS] Địa chỉ ID: {} đã là mặc định", addressId);
            return new CommonResponseDto(true, "This address is already default");
        }

        // chưa phải địa chỉ mặc định
        if(!Boolean.TRUE.equals(address.getIsDefault()) && currentUser.getId().equals(address.getUser().getId())){
            shippingAddressRepository.findByUserIdAndIsDefaultTrue(currentUser.getId()).ifPresent(
                    oldDefaultAddress -> {
                        oldDefaultAddress.setIsDefault(false);
                        shippingAddressRepository.save(oldDefaultAddress);
                        log.info("[ADDRESS] Bỏ default địa chỉ ID: {}", oldDefaultAddress.getId());
                    }
            );
        }
        address.setIsDefault(true);
        shippingAddressRepository.save(address);
        log.info("[ADDRESS] Đặt thành công địa chỉ ID: {} làm mặc định", addressId);

        return new CommonResponseDto(true, "Set default address successfully, addressId: " + addressId);
    }
}
