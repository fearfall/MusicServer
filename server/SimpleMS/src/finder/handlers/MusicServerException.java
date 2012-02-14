package finder.handlers;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 2/13/12
 * Time: 11:14 PM
 */
public class MusicServerException extends Exception {
    private String message;
    public MusicServerException(String s) {
        this.message = s;
    }

    public String getMessage() {
        return message;
    }
}
