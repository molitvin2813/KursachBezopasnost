package helpers;

public class MyException {

    public static class AuthorizationException extends Exception{
        private String login;
        public String getLogin(){return login;}
        public AuthorizationException(String message, String login) {
            super(message);
            this.login=login;
        }
    }
    public static class DesKeyLengthException extends Exception{
        private String desKey;
        private int lengthKey;
        public String getDesKey() { return desKey; }
        public int getLengthKey() { return lengthKey; }

        public DesKeyLengthException(String message, String desKey){
            super(message);
            this.desKey = desKey;
            lengthKey=4;
        }
    }
    public static class PrimeNumberException extends Exception{
        private int number;
        public int getNumber() { return number; }
        public PrimeNumberException(String message, int number){
            super(message);
            this.number=number;
        }
    }
}
