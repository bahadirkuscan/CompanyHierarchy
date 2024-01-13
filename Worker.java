public class Worker extends Entry{
    public String profession;
    public Branch branch;
    public int promotion_points = 0;



    Worker(String name, String profession, Branch branch){
        this.name = name;
        this.profession = profession;
        this.branch = branch;
    }

    public void performanceUpdate (double score){
        int former_promotion_points = this.promotion_points;
        promotion_points += (int) score / 200;
        double bonus = score % 200;
        if (bonus > 0){
            branch.total_monthly_bonus += bonus;
            branch.total_overall_bonus += bonus;
        }
        switch (profession) {
            case "CASHIER":
                if (former_promotion_points < 3 && promotion_points > -5){  // from dismissal or normal TO promotion or normal
                    if (promotion_points >= 3){     // to promotion
                        if (branch.cashier_count == 1){     // cannot instantly promote
                            branch.cashier_to_promote = this;
                        }
                        else{
                            promote(this);
                        }
                    }

                    if (former_promotion_points <= -5){     // from dismissal
                        branch.cashier_to_dismiss = null;
                    }
                }
                else if (former_promotion_points > -5 && promotion_points < 3){   // from promotion or normal TO dismissal or normal
                    if (promotion_points <= -5){        // to dismissal
                        if (branch.cashier_count == 1){     // cannot instantly dismiss
                            branch.cashier_to_dismiss = this;
                        }
                        else{
                            dismiss(this, "dismissed");
                        }
                    }
                    if (former_promotion_points >= 3){      // from promotion
                        branch.cashier_to_promote = null;
                    }
                }
                break;

            case "COOK":
                if (former_promotion_points < 10 && promotion_points > -5){  // from dismissal or normal TO promotion or normal
                    if (promotion_points >= 10){        // to promotion
                        if (branch.cook_count == 1 || !branch.manager_will_dismiss){  // cannot instantly promote
                            branch.cooks_promotion_queue.enqueue(this);
                        }
                        else{
                            dismiss(branch.manager, "dismissed");
                            promote(this);
                        }
                    }

                    if (former_promotion_points <= -5){     // from dismissal
                        branch.cook_to_dismiss = null;
                    }
                }
                else if (former_promotion_points > -5 && promotion_points < 10){   // from promotion or normal TO dismissal or normal
                    if (promotion_points <= -5){    // to dismissal
                        if (branch.cook_count == 1){    // cannot instantly dismiss
                            branch.cook_to_dismiss = this;
                        }
                        else {
                            dismiss(this, "dismissed");
                        }
                    }
                    if (former_promotion_points >= 10){     // from promotion
                        branch.cooks_promotion_queue.remove(this);
                    }
                }
                break;

            case "MANAGER":
                if (former_promotion_points <= -5 && promotion_points > -5){    // from dismissal to normal
                    branch.manager_will_dismiss = false;
                }
                else if (former_promotion_points > -5 && promotion_points <= -5){     // from normal to dismissal
                    if (branch.cook_count == 1 || branch.cooks_promotion_queue.size == 0){   // cannot instantly dismiss
                        branch.manager_will_dismiss = true;
                    }
                    else{
                        dismiss(this, "dismissed");
                        promote(branch.cooks_promotion_queue.dequeue());    // replace the manager
                    }
                }
                break;

            case "COURIER":
                if (promotion_points <= -5 && former_promotion_points > -5){
                    if (branch.courier_count == 1){     // cannot instantly dismiss
                        branch.courier_to_dismiss = this;
                    }
                    else {
                        dismiss(this, "dismissed");
                    }
                }
                else if (promotion_points > -5 && former_promotion_points <= -5){
                    branch.courier_to_dismiss = null;
                }

                break;

        }


    }
    public static void promote(Worker worker){      // make the promotions, conditions are checked outside the function call
        switch (worker.profession){
            case "CASHIER":
                worker.profession = "COOK";
                worker.promotion_points -= 3;
                worker.branch.cashier_count--;
                worker.branch.cook_count++;
                Main.output_writer.write(worker.name + " is promoted from Cashier to Cook.\n");

                if (worker.branch.cook_count == 2){     // cook count increased, check the cooks waiting for promotion or dismissal
                    if (worker.branch.manager_will_dismiss && worker.branch.cooks_promotion_queue.size > 0){    // promotion
                        dismiss(worker.branch.manager, "dismissed");
                        promote(worker.branch.cooks_promotion_queue.dequeue());
                    }
                    if (worker.branch.cook_to_dismiss != null){     // dismissal
                        dismiss(worker.branch.cook_to_dismiss, "dismissed");
                        worker.branch.cook_to_dismiss = null;
                    }
                }

                if (worker.promotion_points >= 10){     // check for double promotion
                    if (worker.branch.cook_count == 1 || !worker.branch.manager_will_dismiss){  // cannot instantly promote
                        worker.branch.cooks_promotion_queue.enqueue(worker);
                    }
                    else{   // manager waiting for dismissal with more than 1 cook implies empty cook promotion queue
                        dismiss(worker.branch.manager, "dismissed");
                        promote(worker);
                    }
                }
                break;

            case "COOK":
                worker.profession = "MANAGER";
                worker.promotion_points -= 10;
                worker.branch.cook_count--;
                worker.branch.manager = worker;
                Main.output_writer.write(worker.name + " is promoted from Cook to Manager.\n");
                break;
        }
    }

    public static void dismiss(Worker worker, String out_message){      // make the dismissals, conditions are checked outside function call
        switch (worker.profession){
            case "COURIER":
                worker.branch.courier_count--;
                Main.output_writer.write(worker.name + " is " + out_message + " from branch: " + worker.branch.name + ".\n");
                break;
            case "CASHIER":
                worker.branch.cashier_count--;
                Main.output_writer.write(worker.name + " is " + out_message + " from branch: " + worker.branch.name + ".\n");
                break;
            case "COOK":
                worker.branch.cook_count--;
                Main.output_writer.write(worker.name + " is " + out_message + " from branch: " + worker.branch.name + ".\n");
                break;
            case "MANAGER":
                Main.output_writer.write(worker.name + " is " + out_message + " from branch: " + worker.branch.name + ".\n");
                worker.branch.manager_will_dismiss = false;
                break;
        }
        worker.branch.workers.remove(worker.name);  // update the branch's workers hash table
    }


}
