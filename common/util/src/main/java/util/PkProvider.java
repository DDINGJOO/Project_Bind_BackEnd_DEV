package util;

import de.huxhorn.sulky.ulid.ULID;

public class PkProvider {

    private static final PkProvider INSTANCE = new PkProvider();
    private final ULID ulid;

    private PkProvider() {
        this.ulid = new ULID();
    }

    public static PkProvider getInstance() {
        return INSTANCE;
    }

    public String generate() {
        return ulid.nextULID(); // ULID 문자열 반환
    }
}
