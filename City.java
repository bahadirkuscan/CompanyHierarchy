public class City extends Entry{
    public MyHashMap<Branch> branches = new MyHashMap<>();

    City(String name){
        this.name = name;
    }

    public void addBranch(Branch branch){
        branches.add(branch);
    }
}
