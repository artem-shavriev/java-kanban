package service;

class Node<T> {
    public T data;
    public service.Node<T> next;
    public service.Node<T> prev;

    public Node(service.Node<T> prev, T data, service.Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
