package top.itsglobally.circlenetwork.circlepractice.utils;


import java.util.List;
import java.util.Random;

public final class RandomUtil {

    private static final Random RANDOM = new Random();

    private RandomUtil() {}

    public static <T> T getRandom(List<T> list) {
        if (list == null || list.isEmpty()) return null;
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static int getRandomInt(int min, int max) {
        if (min >= max) throw new IllegalArgumentException("min is larger than max");
        return RANDOM.nextInt(max - min) + min;
    }

    public static double getRandomDouble(double min, double max) {
        if (min >= max) throw new IllegalArgumentException("min is larger than max");
        return min + (max - min) * RANDOM.nextDouble();
    }

    public static boolean getRandomBoolean() {
        return RANDOM.nextBoolean();
    }
}
