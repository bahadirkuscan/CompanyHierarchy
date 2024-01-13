# CompanyHierarchy
Manually implemented data structures: HashMap, Queue, Doubly Linked List (for Queue implementation)

A company has various branches in various cities. Each branch has 1 manager and at least 1 courier, cashier and cook at all times. Initial state of the branches are given as an input file. At each month, there are several possible updates or information requests in the company:

1) New employee joins: A new given employee joins a given branch at the given position (courier, cashier or cook). There are possible promotions and dismissals after an employee joins the branch.

2) An employee leaves: A given employee wants to leave the company. If the employee is able to leave, the program outputs that the employee left the branch. If not, a bonus is given to the employee (no output in this case). There are possible promotions and dismissals after an employee leaves the branch.

3) Performance update: A given employee gains/loses performance points which turns into bonuses and promotion points. After the performance update, the employee might be promoted or dismissed depending on the promotion points and the situation of the branch. The program outputs the promotions and the dismissals.

4) Print monthly bonuses: Prints out the monthly bonuses of a given branch.

5) Print overall bonuses: Prints out the overall bonuses of a given branch.



Promotion and dismissal rules:
1) Manager: No promotions for manager, manager is set to be dismissed below -5 promotion points. Manager can only be dismissed if there is a cook that has enough points to replace it.
2) Cook: A cook is promoted if the manager is below -5 points, the cook has 10+ points and there are more than 1 cook in the branch. When the manager gets below -5 points, if there are more than 1 cook who has 10+ points, first one to get 10+ points is promoted (getting below 10 loses the priority). A cook is dismissed below -5 points if there are more than 1 cook in the branch.
3) Cashier: A cashier is promoted to cook if the cashier has 5+ promotion points and there are more than 1 cashier in the branch. A cashier is dismissed below -5 points if there are more than 1 cashier in the branch.
4) Courier: No promotions for couriers, the courier is dismissed below -5 points if there are more than 1 courier in the branch.
5) Promotions and dismissals are done instantly after each event (not at the end of the month).
6) Required promotion points are substracted from the promoted employee's promotions points and the rest is reserved.
7) Double promotions are possible.
8) If an employee who is set to be dismissed tries to leave the company, no bonus is given.
9) An employee won't be set to leave the company after taking a bonus. Leaving is only done instantly.


Note: I wasn't able to upload all the test cases (including larger ones) due to github restrictions.
