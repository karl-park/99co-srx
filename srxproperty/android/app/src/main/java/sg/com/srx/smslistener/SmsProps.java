package sg.com.srx.smslistener;

public class SmsProps {

    private String address;
    private String message;

    public SmsProps(String address, String message) {
        this.address = address;
        this.message = message;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
