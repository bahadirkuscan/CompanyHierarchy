import java.util.ArrayList;

public class MyHashMap<E extends Entry> {
    private ArrayList<E> hash_table;
    private int TABLE_SIZE = 997;
    private int element_count = 0;
    private double load_factor = 0;
    private static final double MAX_LOAD_FACTOR = 0.75;

    // collision resolution strategy is linear probing


    MyHashMap() {
        hash_table = new ArrayList<>(TABLE_SIZE);
        for (int i = 0; i < TABLE_SIZE; i++){
            hash_table.add(null);
        }
    }

    public void add(E element){
        int probe = 0;
        E target = hash_table.get(hash(element.name, probe));
        while (target != null){     // find empty place
            if (target.name.equals("deleted")){     // former place of a worker is available to new workers
                hash_table.set(hash(element.name, probe), element);
                element_count++;
                load_factor = (double) element_count / TABLE_SIZE;
                if (load_factor > MAX_LOAD_FACTOR){
                    rehash();
                }
                return;
            }
            target = hash_table.get(hash(element.name, ++probe));
        }
        hash_table.set(hash(element.name, probe), element);
        element_count++;
        load_factor = (double) element_count / TABLE_SIZE;
        if (load_factor > MAX_LOAD_FACTOR){
            rehash();
        }
    }

    public void remove(String name){
        E target = find(name);
        target.name = "deleted";
    }

    private int hash(String name, int probe){
        return (Math.abs(name.hashCode()) + probe) % TABLE_SIZE;
    }

    private void rehash(){
        TABLE_SIZE = nextPrime(TABLE_SIZE * 2);
        ArrayList<E> new_table = new ArrayList<>();
        for (int i = 0; i < TABLE_SIZE; i++){
            new_table.add(null);
        }
        for (E element : hash_table){
            int probe = 0;
            if (element == null){
                continue;
            }
            if(element.name.equals("deleted")){
                continue;
            }
            int new_pos = hash(element.name, probe);
            while (new_table.get(new_pos) != null){
                new_pos = hash(element.name, ++probe);
            }
            new_table.set(new_pos, element);

        }
        hash_table = new_table;
        load_factor = (double) element_count / TABLE_SIZE;
    }

    public E find(String name){     // search the entry by name, return its object or null if not found
        int probe = 0;
        E target = hash_table.get(hash(name, probe));
        while (target != null){
            if (target.name.equals(name)){  // deleted workers' name are "deleted", so no match will happen
                return target;
            }
            target = hash_table.get(hash(name, ++probe));
        }
        return null;
    }

    private int nextPrime(int number){
        boolean found;
        while (true){
            number += 1;
            found = true;
            for (int x = 2; x <= Math.floor(Math.sqrt(number)); x++){
                if (number % x == 0){
                    found = false;
                    break;
                }
            }
            if (found){
                return number;
            }
        }
    }

}
