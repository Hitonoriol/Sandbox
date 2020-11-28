package hitonoriol.sandbox;

public class Utils {

    public static Enum<?> nextEnum(Enum<?> enm) {
        Enum<?>[] vals = enm.getClass().getEnumConstants();
        return vals[(enm.ordinal() + 1) % vals.length];
    }

    public static void out(String str) {
        System.out.println(str);
    }
}
