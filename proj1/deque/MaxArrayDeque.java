package deque;

import java.util.Comparator;

/**
 * @author blueofflaner <blueofflaner@gmail.com>
 * Created on 2024-04-12
 */
public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    public T max() {
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T res = get(0);
        for (int i = 1; i < size(); i++) {
            T x = get(i);
            if (c.compare(res, x) < 0) {
                res = x;
            }
        }
        return res;
    }
}
