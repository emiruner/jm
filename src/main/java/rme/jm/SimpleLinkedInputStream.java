package rme.jm;

import java.util.List;

public class SimpleLinkedInputStream extends LinkedInputStreamBase {
    private final Object head;
    private final LinkedInputStream tail;

    public SimpleLinkedInputStream(Object head, LinkedInputStream tail) {
        this.head = head;
        this.tail = tail;
    }

    public Object head() {
        return head;
    }

    public LinkedInputStream tail() {
        return tail;
    }

    public static LinkedInputStream fromList(List<?> list) {
        if (list == null || list.size() == 0) {
            return new EndLinkedInputStream();
        }

        LinkedInputStream current = new EndLinkedInputStream();

        for (int i = list.size() - 1; i >= 0; --i) {
            current = new SimpleLinkedInputStream(list.get(i), current);
        }

        return current;
    }

    public static LinkedInputStream singleElementList(Object element) {
        return new SimpleLinkedInputStream(element, new EndLinkedInputStream());
    }
}
