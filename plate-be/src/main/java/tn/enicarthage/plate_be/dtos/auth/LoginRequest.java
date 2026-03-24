package tn.enicarthage.plate_be.dtos.auth;

public class LoginRequest {
    private String email;
    private final String password;

    public LoginRequest(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

}