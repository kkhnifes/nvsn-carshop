package at.kkhnifes.carshopbackend.util;

public enum JWTClaim {

    USER_ID("uid");

    private String value;
    private JWTClaim(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
