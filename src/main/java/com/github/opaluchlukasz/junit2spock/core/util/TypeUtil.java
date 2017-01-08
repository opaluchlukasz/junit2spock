package com.github.opaluchlukasz.junit2spock.core.util;

import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;

public final class TypeUtil {

    private TypeUtil() {
        // NOOP
    }

    public static boolean isVoid(Type type) {
        return type.isPrimitiveType() && ((PrimitiveType) type).getPrimitiveTypeCode().equals(PrimitiveType.VOID);
    }
}
