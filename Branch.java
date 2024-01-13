public class Branch extends Entry{
    public double total_monthly_bonus = 0;
    public double total_overall_bonus = 0;
    public int cashier_count = 0;
    public int cook_count = 0;
    public int courier_count = 0;
    public Worker manager;
    public MyHashMap<Worker> workers = new MyHashMap<>();
    public Worker cashier_to_promote;
    public Worker cashier_to_dismiss;
    public DLLQueue cooks_promotion_queue = new DLLQueue();
    public Worker cook_to_dismiss;
    public Worker courier_to_dismiss;
    public boolean manager_will_dismiss = false;

    Branch(String name){
        this.name = name;
    }

    public void addWorker(Worker worker){
        if (workers.find(worker.name) != null){
            project2.output_writer.write("Existing employee cannot be added again.\n");
            return;
        }
        workers.add(worker);
        switch (worker.profession) {
            case "CASHIER" -> cashier_count++;
            case "COOK" -> cook_count++;
            case "COURIER" -> courier_count++;
            case "MANAGER" -> manager = worker;
        }
    }



    public void monthlyBonusReset(){
        total_monthly_bonus = 0;
    }


}
