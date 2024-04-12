package deque;

import java.util.Comparator;
import java.util.Deque;
import java.util.Objects;

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

    public T max(Comparator<T> comparator) {
        if (isEmpty()) {
            return null;
        }
        T res = get(0);
        for (int i = 1; i < size(); i++) {
            T x = get(i);
            if (comparator.compare(res, x) < 0) {
                res = x;
            }
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        if (o instanceof MaxArrayDeque) {
            return super.equals(o) && comparator.equals(((MaxArrayDeque<?>) o).comparator);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
