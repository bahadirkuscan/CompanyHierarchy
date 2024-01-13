import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class project2 {
    public static PrintWriter output_writer;
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 3) {
            System.out.println("Usage: java Main <input1.txt> <input2.txt> <output.txt>");
            System.exit(1);
        }

        String input1FileName = args[0];
        String input2FileName = args[1];
        String outputFileName = args[2];

        output_writer = new PrintWriter(outputFileName);
        File file1 = new File(input1FileName);
        File file2 = new File(input2FileName);
        Scanner initial = new Scanner(file1);
        Scanner updates = new Scanner(file2);
        MyHashMap<City> cities = new MyHashMap<>();     // cities are stored in a hash table
        ArrayList<Branch> branches = new ArrayList<>(); // for monthly bonus resets
        while (initial.hasNext()){
            String[] words = initial.nextLine().split(",");
            City city = cities.find(words[0]);      // check if the city is already inserted in the hash table
            if (city == null){
                city = new City(words[0]);      // create new city and insert in the hash table
                cities.add(city);
            }

            Branch branch = city.branches.find(words[1].trim());    // check if the branch is already inserted in the city's hash table
            if (branch == null){
                branch = new Branch(words[1].trim());   // create new branch and insert in the hash table
                city.addBranch(branch);
                branches.add(branch);
            }

            Worker worker = branch.workers.find(words[2].trim());   // check if the worker is already inserted in the branch's hash table
            if (worker == null){
                worker = new Worker(words[2].trim(), words[3].trim(), branch);  // create new worker and insert in the hash table
                branch.addWorker(worker);
            }
        }

        while (updates.hasNext()){
            String[] line = updates.nextLine().split(":");

            switch (line[0]){
                case "PERFORMANCE_UPDATE":
                    String[] x = line[1].split(",");
                    String worker_name = x[2].trim();
                    Worker worker0 = cities.find(x[0].trim()).branches.find(x[1].trim()).workers.find(worker_name);
                    if (worker0 == null){
                        output_writer.write("There is no such employee.\n");
                    }
                    else{
                        worker0.performanceUpdate(Double.parseDouble(x[3].trim()));
                    }
                    break;



                case "ADD":
                    String[] y = line[1].split(",");
                    String profession = y[3].trim();
                    Branch branch0 = cities.find(y[0].trim()).branches.find(y[1].trim());
                    branch0.addWorker(new Worker(y[2].trim(), profession, branch0));    // already existing case is checked in addWorker function
                    switch (profession){
                        case "CASHIER":
                            if (branch0.cashier_count == 2){    // promote or dismiss the cashiers who couldn't because there were no other cashiers
                                if (branch0.cashier_to_promote != null){
                                    Worker.promote(branch0.cashier_to_promote);
                                    branch0.cashier_to_promote = null;
                                }
                                if (branch0.cashier_to_dismiss != null){
                                    Worker.dismiss(branch0.cashier_to_dismiss, "dismissed");
                                    branch0.cashier_to_dismiss = null;
                                }
                            }
                            break;

                        case "COOK":
                            if (branch0.cook_count == 2){   // promote or dismiss the cooks who couldn't because there were no other cooks
                                if (branch0.manager_will_dismiss && branch0.cooks_promotion_queue.size > 0){
                                    Worker.dismiss(branch0.manager, "dismissed");
                                    Worker.promote(branch0.cooks_promotion_queue.dequeue());
                                }
                                if (branch0.cook_to_dismiss != null){
                                    Worker.dismiss(branch0.cook_to_dismiss, "dismissed");
                                    branch0.cook_to_dismiss = null;
                                }
                            }
                            break;

                        case "COURIER":
                            if (branch0.courier_count == 2 && branch0.courier_to_dismiss != null){  // dismiss the courier who couldn't because there were no other couriers
                                Worker.dismiss(branch0.courier_to_dismiss, "dismissed");
                                branch0.courier_to_dismiss = null;
                            }
                            break;
                    }
                    break;


                case "LEAVE":
                    String[] z = line[1].split(",");
                    Branch branch1 = cities.find(z[0].trim()).branches.find(z[1].trim());
                    Worker worker = cities.find(z[0].trim()).branches.find(z[1].trim()).workers.find(z[2].trim());
                    if (worker == null){
                        output_writer.write("There is no such employee.\n");
                    }
                    else{
                        switch (worker.profession){
                            case "CASHIER":
                                if (branch1.cashier_count > 1){     // leaving condition
                                    if (worker == branch1.cashier_to_promote){  // in case the worker was waiting for promotion
                                        branch1.cashier_to_promote = null;
                                    }
                                    Worker.dismiss(worker,"leaving");
                                }
                                else{   // only cashier in the branch
                                    if (worker.promotion_points > -5){   // no bonus for cashier waiting for dismissal
                                        branch1.total_monthly_bonus += 200;
                                        branch1.total_overall_bonus += 200;
                                    }
                                }
                                break;

                            case "COOK":
                                if (branch1.cook_count > 1){    // leaving condition
                                    branch1.cooks_promotion_queue.remove(worker);   // in case the worker was waiting for promotion
                                    Worker.dismiss(worker,"leaving");
                                }
                                else{
                                    if(worker.promotion_points > -5){   // no bonus for cook waiting for dismissal
                                        branch1.total_monthly_bonus += 200;
                                        branch1.total_overall_bonus += 200;
                                    }
                                }
                                break;

                            case "COURIER":
                                if (branch1.courier_count > 1){     // leaving condition
                                    Worker.dismiss(worker,"leaving");
                                }
                                else{
                                    if(worker.promotion_points > -5){    // no bonus for courier waiting for dismissal
                                        branch1.total_monthly_bonus += 200;
                                        branch1.total_overall_bonus += 200;
                                    }
                                }
                                break;

                            case "MANAGER":
                                if (branch1.cooks_promotion_queue.size > 0 && branch1.cook_count > 1){  // leaving condition
                                    Worker.dismiss(worker,"leaving");
                                    Worker.promote(branch1.cooks_promotion_queue.dequeue());    // replace with the new manager
                                }
                                else{
                                    if (!branch1.manager_will_dismiss){ // no bonus for manager waiting for dismissal
                                        branch1.total_monthly_bonus += 200;
                                        branch1.total_overall_bonus += 200;
                                    }
                                }
                                break;
                        }
                    }

                    break;



                case "PRINT_MONTHLY_BONUSES":
                    String[] t = line[1].split(",");
                    Branch branch2 = cities.find(t[0].trim()).branches.find(t[1].trim());
                    output_writer.write("Total bonuses for the " + branch2.name + " branch this month are: " + (int) branch2.total_monthly_bonus + "\n");
                    break;


                case "PRINT_OVERALL_BONUSES":
                    String[] k = line[1].split(",");
                    Branch branch3 = cities.find(k[0].trim()).branches.find(k[1].trim());
                    output_writer.write("Total bonuses for the " + branch3.name + " branch are: " + (int) branch3.total_overall_bonus + "\n");
                    break;

                case "PRINT_MANAGER":
                    String[] l = line[1].split(",");
                    Branch branch4 = cities.find(l[0].trim()).branches.find(l[1].trim());
                    output_writer.write("Manager of the "+ branch4.name + " branch is " + branch4.manager.name + ".\n");
                    break;

                case "":    // for empty lines in the input
                    break;

                default:    // new month
                    for (Branch branchh : branches){
                        branchh.monthlyBonusReset();
                    }
            }
        }
        output_writer.close();
    }

}
