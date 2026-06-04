package org.com.pet_spr.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.com.pet_spr.validator.annotation.EnumValue;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {
    private List<String> acceptedValues;
    @Override
    public void initialize(EnumValue enumValue) {
        acceptedValues = Stream.of(enumValue.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }
    //    (Enum::name) <=> e -> e.name() tuc la moi phan tu Enum goi ham .name() -> lay ra chuoi dang String
//    @Override
//    public void initialize(EnumValue enumValue) {
//        // Lấy toàn bộ giá trị trong Enum
//        Enum<?>[] constants = enumValue.enumClass().getEnumConstants();
//
//        // Tạo danh sách trống để lưu tên các Enum
//        List<String> list = new ArrayList<>();
//
//        // Duyệt từng giá trị trong Enum và thêm vào list
//        for (Enum<?> e : constants) {
//            list.add(e.name());
//        }
//
//        // Gán danh sách đó cho biến acceptedValues
//        acceptedValues = list;
//    }


    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        boolean isValid = acceptedValues.contains(value.toString().toUpperCase().trim());

        // Nếu sai định dạng, gán giá trị danh sách Enum vào biến {enumClass}
        if (!isValid) {
            HibernateConstraintValidatorContext hibernateContext =
                    context.unwrap(HibernateConstraintValidatorContext.class);

            // Ép biến {enumClass} tĩnh thành chuỗi danh sách trực quan [MALE, FEMALE, OTHER]
            hibernateContext.addMessageParameter("enumClass", acceptedValues.toString());
        }

        return isValid;
    }
}
