public class Driver {
    public static void main(String[] args) {
        System.out.println(SHA3.right_encode(256));
        System.out.println(SHA3.left_encode(256));
        System.out.println(SHA3.encode_string(""));
        System.out.println(SHA3.bytepad(SHA3.encode_string(""), 20));
    }
}
