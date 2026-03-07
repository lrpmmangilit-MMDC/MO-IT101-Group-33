**CP1 – Milestone 2**



**MotorPH Employee Payroll System**



**Basic Payroll Program**



This program reads employee information and attendance records from CSV files, calculates the total hours worked per payroll cutoff, computes the employee’s salary, applies government deductions, and displays a payroll summary for each month from June to December.



**Project References**



**Milestone 1 Output**

https://www.canva.com/design/DAHAKoxzoNg/J4gFdD2tMWLMiLaWfnSkKQ/edit



**Effort Estimation and Project Plan Document**

https://docs.google.com/spreadsheets/d/1-5q2XM30vREjyo-NGScc0rmIr1Fxj1u1aIpHaCDceBE/edit?gid=638087839#gid=638087839



**How the Program Works**

The program is a simple Java component of the MotorPH Payroll System that computes the total number of hours an employee worked in a day. The program demonstrates the use of variables, arithmetic operators, and clear output formatting to perform a basic payroll-related calculation.

The program begins by declaring variables that store important information such as the employee number, employee’s name, log in, and log out. The time values are stored using the double data type so that fractional hours can be represented accurately.

To compute the total hours worked, the program applies a simple arithmetic formula:

Total Hours Worked = Time Out − Time In (with a grace period of 10 min)

This calculation ensures that the employee’s break time is not included in the total working hours.

Inline comments are included throughout the code to explain the purpose of each variable and the steps involved in the calculation. This helps improve readability and makes the program easier to understand and maintain.

Finally, the program displays the results using System.out.println(), clearly labeling the output with the employee’s name and the computed total hours worked. A verification message is also printed to confirm that the computation was executed successfully.

Overall, this program demonstrates how Java variables and arithmetic operators can be used to perform practical payroll calculations in a clear and organized manner.



**Members**



Pauline Mae Mangilit (Compilation, Employee Login, Computed Gross Salary per Cut-off)

Aleah Venice Declarador (Computation of Deductions to be Applied to Month's Second Cut-off Gross Salary)

Abigail Lausing (Computation of Deductions to be Applied to Month's Second Cut-off Gross Salary)

Juliana Martina Relox (Computed Work Hours using Login and Logout)





