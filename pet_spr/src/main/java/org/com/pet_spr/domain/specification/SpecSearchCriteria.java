package org.com.pet_spr.domain.specification;

import lombok.Getter;

@Getter
public class SpecSearchCriteria {
    private final String key;

    private final SEARCH_OPERATION searchOperation;

    private final Object value;

    private final boolean orPredicate;

    public SpecSearchCriteria(String key, SEARCH_OPERATION operation, Object value){
        this(null, key, operation, value);
    }


    public SpecSearchCriteria(String orPredicate,String key, SEARCH_OPERATION operation, Object value){
        this.key = key;
        this.searchOperation = operation;
        this.value = value;
        this.orPredicate = orPredicate != null && orPredicate.equals(SEARCH_OPERATION.OR_PREDICATE_FLAG);
         // khong co predicate mac dinh la AND

    }

    public SpecSearchCriteria(String orPredicate, String key, String operation, Object value, String prefix, String suffix){
        SEARCH_OPERATION search_operation = SEARCH_OPERATION.getSimpleOperation(operation.charAt(0));
        if(search_operation != null){
            if(search_operation.equals(SEARCH_OPERATION.EQUALITY)){
                boolean startsWith = prefix.contains(SEARCH_OPERATION.ZERO_OR_MORE_REGEX);
                boolean endsWith = suffix.contains(SEARCH_OPERATION.ZERO_OR_MORE_REGEX);
                if(startsWith && endsWith){
                    search_operation = SEARCH_OPERATION.CONTAINS;
                } else if(endsWith){
                    search_operation = SEARCH_OPERATION.STARTS_WITH;
                } else if(startsWith){
                    search_operation = SEARCH_OPERATION.ENDS_WITH;
                }
            }
        }
        this.key = key;
        this.searchOperation = search_operation;
        this.value = value;
        this.orPredicate = orPredicate != null && orPredicate.equalsIgnoreCase(SEARCH_OPERATION.OR_PREDICATE_FLAG);
        // khong co predicate mac dinh la AND
    }
}
