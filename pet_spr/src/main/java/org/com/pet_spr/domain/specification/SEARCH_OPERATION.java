package org.com.pet_spr.domain.specification;

import java.util.Set;

public enum SEARCH_OPERATION {
    EQUALITY, NEGATION,  GREATER_THAN, LESS_THAN, LIKE, STARTS_WITH, ENDS_WITH, CONTAINS;

    public static final Set<Character> SIMPLE_OPERATION_SET =  Set.of('=', '!', '>', '<', '~');

    public static final String OR_PREDICATE_FLAG = "'";

    public static final String ZERO_OR_MORE_REGEX = "*";

    public static final String LEFT_PARENTHESIS = "(";

    public static final String RIGHT_PARENTHESIS = ")";

    public static SEARCH_OPERATION getSimpleOperation(char simpleOperation){
        return switch (simpleOperation) {
            case '=' -> EQUALITY;
            case '!' -> NEGATION;
            case '>' -> GREATER_THAN;
            case '<' -> LESS_THAN;
            case '~' -> LIKE;
            default -> null;
        };
    }



}
