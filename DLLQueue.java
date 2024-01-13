public class DLLQueue {     // Doubly Linked List implementation of Queue
    private final QueueNode front; // empty node
    private final QueueNode rear;  // empty node
    public int size = 0;
    DLLQueue(){
        front = new QueueNode();
        rear = new QueueNode();
        front.next_node = rear;
        rear.prev_node = front;
    }

    private static class QueueNode{
        public Worker value;
        public QueueNode prev_node;
        public QueueNode next_node;
        QueueNode(){}
        QueueNode(Worker element){
            value = element;
        }
    }

    public void enqueue(Worker element){
        QueueNode new_node = new QueueNode(element);
        rear.prev_node.next_node = new_node;
        new_node.prev_node = rear.prev_node;
        rear.prev_node = new_node;
        new_node.next_node = rear;
        size++;
    }

    public Worker dequeue(){    // dequeue() is never called when queue is empty
        Worker return_value = front.next_node.value;
        front.next_node.next_node.prev_node = front;
        front.next_node = front.next_node.next_node;
        size--;
        return return_value;
    }

    public void remove(Worker element){
        QueueNode node = front;
        while (node.value != element){
            if (node == rear){  // remove is called with an element not in the queue
                return;
            }
            node = node.next_node;
        }
        node.prev_node.next_node = node.next_node;
        node.next_node.prev_node = node.prev_node;
        size--;
    }
}
