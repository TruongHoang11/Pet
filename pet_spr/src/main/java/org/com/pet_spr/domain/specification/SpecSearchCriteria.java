package org.com.pet_spr.domain.specification;



import lombok.Getter;
import org.com.pet_spr.domain.specification.SEARCH_OPERATION;


import static org.com.pet_spr.domain.specification.SEARCH_OPERATION.*;


@Getter
public class SpecSearchCriteria {
    private final String key;
    private final SEARCH_OPERATION operation;
    private final Object value;
    private final boolean orPredicate;

    private SpecSearchCriteria(String key, SEARCH_OPERATION searchOperation, Object value){
        this(null, key, searchOperation, value);
    }

    public SpecSearchCriteria(String orPredicate, String key, SEARCH_OPERATION searchOperation, Object value){
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
        this.orPredicate = orPredicate != null && orPredicate.equals(OR_PREDICATE_FLAG);
    }

    public SpecSearchCriteria(String orPredicate, String key, String operation, Object value, String prefix, String suffix) {
        if(operation != null && operation.isEmpty()){
            throw new IllegalArgumentException("Search operation cannot be empty or null");
        }
        SEARCH_OPERATION searchOperation = SEARCH_OPERATION.getSimpleOperation(operation.charAt(0));
        if(searchOperation != null){
            if(searchOperation == EQUALITY){
                final boolean startWithAsterisks = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
                final boolean endWithAsterisks = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX);
                if(startWithAsterisks && endWithAsterisks){
                    searchOperation = CONTAINS;
                } else if (startWithAsterisks) {
                    searchOperation = ENDS_WITH;
                } else if(endWithAsterisks){
                    searchOperation = STARTS_WITH;
                }
            }

        }
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
        this.orPredicate = orPredicate != null && orPredicate.equals(OR_PREDICATE_FLAG);
        // khong co predicate mac dinh la AND
    }

    public static void handleWildCardSearch(String valueStr, String orPredicate,String prefix, String suffix, boolean isOrPredicate){
        if(valueStr.startsWith("*")){
            prefix = "*";
            valueStr = valueStr.substring(1); // 0 - 1
        }
        if(valueStr.endsWith("*")){
            suffix = "*";
            valueStr = valueStr.substring(0, valueStr.length() - 1);
        }
        isOrPredicate = orPredicate != null && orPredicate.equals(OR_PREDICATE_FLAG);
    }
    // substring: begin index  ( index start 0)
//    "hello".substring(1)  →  "ello"
//    "hello".substring(2)  →  "llo"
    // substring: begin index to end index
    // "hello".substring(0,3) -> "hel"
    // "hello".substring(1,3) -> "el"
    // "hello".substring(0, hello.length() - 1) -> "hell"
}
