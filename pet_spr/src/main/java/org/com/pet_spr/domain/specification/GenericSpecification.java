package org.com.pet_spr.domain.specification;

import jakarta.persistence.criteria.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class GenericSpecification<T> implements Specification<T> {
    private final SpecSearchCriteria specSearchCriteria;
    public GenericSpecification(SpecSearchCriteria specSearchCriteria){
        this.specSearchCriteria = specSearchCriteria;
    }

    @Override
    public @Nullable Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<?> path = getPath(root, specSearchCriteria.getKey());
        Object value = castToRequiredType(path.getJavaType(), specSearchCriteria.getValue());

        return switch (specSearchCriteria.getSearchOperation()){
            case EQUALITY -> criteriaBuilder.equal(path, value);
            case NEGATION -> criteriaBuilder.notEqual(path, value);
            case GREATER_THAN -> criteriaBuilder.greaterThanOrEqualTo((Path<Comparable>)path, (Comparable) value);
            case LESS_THAN -> criteriaBuilder.lessThanOrEqualTo((Path<Comparable>)path, (Comparable) value);
            case CONTAINS -> criteriaBuilder.like(path.as(String.class), "%" + value + "%");
            case LIKE ->  criteriaBuilder.like(path.as(String.class), value.toString());
            case STARTS_WITH -> criteriaBuilder.like(path.as(String.class), value + "%");
            case ENDS_WITH -> criteriaBuilder.like(path.as(String.class), "%" + value);
            default -> null;
        };


    }

    private Path<?> getPath(Root<T> root, String key){
        if(key.contains(".")){
            String[] parts = key.split("\\.");

            Join<Object, Object> join = root.join(parts[0], JoinType.LEFT);

            return join.get(parts[1]);
        }

        return root.get(key);
    }

    private Object castToRequiredType(Class<?> type, Object value){
        String stringValue = value.toString();

        if(type.isAssignableFrom(String.class)){
            return stringValue;
        } else if(type == Integer.class || type == int.class){
            return Integer.parseInt(stringValue);
        } else if(type == Double.class || type == double.class){
            return Double.parseDouble(stringValue);
        } else if(type == Boolean.class || type == boolean.class){
            return Boolean.parseBoolean(stringValue);
        } else if (type == java.util.UUID.class) {
            return java.util.UUID.fromString(stringValue);
        } else if( type == java.time.LocalDateTime.class) {
            return java.time.LocalDateTime.parse(stringValue);
        } else if(type.isEnum()){
            return Enum.valueOf((Class<Enum>) type, stringValue);
        }

        return stringValue;
    }
}
