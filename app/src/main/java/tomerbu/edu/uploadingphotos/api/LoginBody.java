package tomerbu.edu.uploadingphotos.api;

/**
 * Send the credentials out for the post login method
 */
public class LoginBody {
    private String name;

    @Override
    public String toString() {
        return "LoginBody{" +
                "name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String pass;

    public LoginBody(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }
}