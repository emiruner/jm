package tr.rimerun.jm;

import java.util.List;

public class BasicLinkedInputStream extends LinkedInputStreamBase {
    private Object head;
    private LinkedInputStream tail;

    public BasicLinkedInputStream(Object head, LinkedInputStream tail) {
        this.head = head;
        this.tail = tail;
    }

    public Object head() {
        return head;
    }

    public LinkedInputStream tail() {
        return tail;
    }

    public static LinkedInputStream fromString(String input) {
        if (input == null || input.length() == 0) {
            return new EndLinkedInputStream();
        }

        LinkedInputStream current = new EndLinkedInputStream();

        for (int i = input.length() - 1; i >= 0; --i) {
            current = new BasicLinkedInputStream(input.charAt(i), current);
        }

        return current;
    }

    public static LinkedInputStream fromList(List list) {
        if (list == null || list.size() == 0) {
            return new EndLinkedInputStream();
        }

        LinkedInputStream current = new EndLinkedInputStream();

        for (int i = list.size() - 1; i >= 0; --i) {
            current = new BasicLinkedInputStream(list.get(i), current);
        }

        return current;
    }

    public static LinkedInputStream singleElementList(Object element) {
        return new BasicLinkedInputStream(element, new EndLinkedInputStream());
    }
}
