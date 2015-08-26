package SQL;

import java.util.Iterator;

public class LazyIterator<T extends SQLRow> implements Iterator<T> {
    LazyList<T> list;

    public LazyIterator(LazyList<T> list){
        this.list = list;
    }

    @Override
    public boolean hasNext() {
        return list.hasNext();
    }

    @Override
    public T next() {
        return list.next();
    }
}
