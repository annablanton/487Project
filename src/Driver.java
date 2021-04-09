public class Driver {
    public static void main(String[] args) {
        System.out.println(Utilities.right_encode(256));
        System.out.println(Utilities.left_encode(256));
        System.out.println(Utilities.encode_string(""));
        System.out.println(Utilities.bytepad(Utilities.encode_string(""), 20));
    }
}
