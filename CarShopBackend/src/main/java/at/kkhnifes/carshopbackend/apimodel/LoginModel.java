package at.kkhnifes.carshopbackend.apimodel;

public class LoginModel {

    public String userName;
    public String password;

    public static class LoginResult {
        /* much wow */
        public String token;

        public LoginResult(String jwt) {
            this.token = jwt;
        }
    }
}


