package tytoo.weave.state;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.UnaryOperator;

public class ObservableList<T> implements List<T> {
    public interface ChangeListener<T> {
        void onChanged(Change<T> change);
    }

    public record Change<T>(Type type, int fromIndex, int toIndex, List<T> added, List<T> removed) {
        public enum Type { ADD, REMOVE, SET, CLEAR, ADD_ALL, REMOVE_RANGE }

        public Change {
            added = added == null ? List.of() : List.copyOf(added);
            removed = removed == null ? List.of() : List.copyOf(removed);
        }
    }

    private final List<T> delegate = new ArrayList<>();
    private final List<ChangeListener<T>> listeners = new ArrayList<>();

    public void addListener(ChangeListener<T> listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void removeListener(ChangeListener<T> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Change<T> change) {
        for (ChangeListener<T> l : new ArrayList<>(listeners)) l.onChanged(change);
    }

    @Override
    public int size() { return delegate.size(); }

    @Override
    public boolean isEmpty() { return delegate.isEmpty(); }

    @Override
    public boolean contains(Object o) { return delegate.contains(o); }

    @Override
    public @NotNull Iterator<T> iterator() { return Collections.unmodifiableList(delegate).iterator(); }

    @Override
    public @NotNull Object[] toArray() { return delegate.toArray(); }

    @Override
    public <U> @NotNull U[] toArray(@NotNull U[] a) { return delegate.toArray(a); }

    @Override
    public boolean add(T t) {
        int idx = delegate.size();
        delegate.add(t);
        notifyListeners(new Change<>(Change.Type.ADD, idx, idx + 1, List.of(t), List.of()));
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int idx = -1;
        T removed = null;
        ListIterator<T> it = delegate.listIterator();
        while (it.hasNext()) {
            int i = it.nextIndex();
            T t = it.next();
            if (Objects.equals(t, o)) {
                it.remove();
                idx = i;
                removed = t;
                break;
            }
        }
        if (idx == -1) return false;
        notifyListeners(new Change<>(Change.Type.REMOVE, idx, idx + 1, List.of(), List.of(removed)));
        return true;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        if (c.isEmpty()) return true;
        if (delegate.isEmpty()) return false;
        Set<T> set = new HashSet<>(delegate);
        for (Object e : c) if (!set.contains(e)) return false;
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        if (c.isEmpty()) return false;
        int from = delegate.size();
        delegate.addAll(c);
        notifyListeners(new Change<>(Change.Type.ADD_ALL, from, from + c.size(), new ArrayList<>(c), List.of()));
        return true;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        if (c.isEmpty()) return false;
        delegate.addAll(index, c);
        notifyListeners(new Change<>(Change.Type.ADD_ALL, index, index + c.size(), new ArrayList<>(c), List.of()));
        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        if (c.isEmpty()) return false;
        List<T> removed = new ArrayList<>();
        Iterator<T> it = delegate.iterator();
        while (it.hasNext()) {
            T t = it.next();
            if (c.contains(t)) {
                it.remove();
                removed.add(t);
            }
        }
        if (!removed.isEmpty()) notifyListeners(new Change<>(Change.Type.REMOVE_RANGE, 0, delegate.size(), List.of(), removed));
        return !removed.isEmpty();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        List<T> removed = new ArrayList<>();
        Iterator<T> it = delegate.iterator();
        while (it.hasNext()) {
            T t = it.next();
            if (!c.contains(t)) {
                it.remove();
                removed.add(t);
            }
        }
        if (!removed.isEmpty()) notifyListeners(new Change<>(Change.Type.REMOVE_RANGE, 0, delegate.size(), List.of(), removed));
        return !removed.isEmpty();
    }

    @Override
    public void replaceAll(@NotNull UnaryOperator<T> operator) {
        List<T> before = new ArrayList<>(delegate);
        delegate.replaceAll(operator);
        notifyListeners(new Change<>(Change.Type.SET, 0, delegate.size(), new ArrayList<>(delegate), before));
    }

    @Override
    public void sort(Comparator<? super T> c) {
        delegate.sort(c);
        notifyListeners(new Change<>(Change.Type.SET, 0, delegate.size(), new ArrayList<>(delegate), List.of()));
    }

    @Override
    public void clear() {
        if (delegate.isEmpty()) return;
        List<T> removed = new ArrayList<>(delegate);
        delegate.clear();
        notifyListeners(new Change<>(Change.Type.CLEAR, 0, 0, List.of(), removed));
    }

    @Override
    public T get(int index) { return delegate.get(index); }

    @Override
    public T set(int index, T element) {
        T old = delegate.set(index, element);
        notifyListeners(new Change<>(Change.Type.SET, index, index + 1, List.of(element), List.of(old)));
        return old;
    }

    @Override
    public void add(int index, T element) {
        delegate.add(index, element);
        notifyListeners(new Change<>(Change.Type.ADD, index, index + 1, List.of(element), List.of()));
    }

    @Override
    public T remove(int index) {
        T old = delegate.remove(index);
        notifyListeners(new Change<>(Change.Type.REMOVE, index, index + 1, List.of(), List.of(old)));
        return old;
    }

    @Override
    public int indexOf(Object o) { return delegate.indexOf(o); }

    @Override
    public int lastIndexOf(Object o) { return delegate.lastIndexOf(o); }

    @Override
    public @NotNull ListIterator<T> listIterator() { return Collections.unmodifiableList(delegate).listIterator(); }

    @Override
    public @NotNull ListIterator<T> listIterator(int index) { return Collections.unmodifiableList(delegate).listIterator(index); }

    @Override
    public @NotNull List<T> subList(int fromIndex, int toIndex) { return Collections.unmodifiableList(delegate.subList(fromIndex, toIndex)); }
}
