CP1 – Milestone 2



MotorPH Employee Payroll System



Basic Payroll Program



This program reads employee information and attendance records from CSV files, calculates the total hours worked per payroll cutoff, computes the employee’s salary, applies government deductions, and displays a payroll summary for each month from June to December.



Project References



Milestone 1 Output

https://www.canva.com/design/DAHAKoxzoNg/J4gFdD2tMWLMiLaWfnSkKQ/edit



Effort Estimation and Project Plan Document

https://docs.google.com/spreadsheets/d/1-5q2XM30vREjyo-NGScc0rmIr1Fxj1u1aIpHaCDceBE/edit?gid=638087839#gid=638087839



How the Program Works



Imports

import java.io.BufferedReader;

import java.io.FileReader;

import java.io.IOException;

import java.time.LocalDate;

import java.time.LocalTime;

import java.time.Month;

import java.time.format.DateTimeFormatter;

import java.util.Scanner;



BufferedReader and FileReader are used to read CSV files line by line.



LocalDate and LocalTime handle date and time values from attendance records.



Month converts numeric month values to readable month names.



DateTimeFormatter formats date and time strings from the CSV files.



Scanner allows the program to receive input from the user.



Main Class and Method

public class MotorPHEmployeePayrollSystem {

&nbsp;   public static void main(String\[] args) {



MotorPHEmployeePayrollSystem is the main class of the program.



main() is the entry point where program execution begins.



File Paths and Scanner

String EmployeeFile = "resources/MotorPH\_Employee Data - Employee Details.csv";

String AttendanceFile = "resources/MotorPH\_Employee Data - Attendance Record.csv";



EmployeeFile stores the path to the employee information CSV file.



AttendanceFile stores the path to the attendance records CSV file.



Scanner scanner = new Scanner(System.in);



Scanner reads user input from the keyboard.



Login System

System.out.print("Enter Employee Number: ");

String username = scanner.nextLine();



System.out.print("Enter Password: ");

String password = scanner.nextLine();



The program asks the user to input their employee number and password.



In this version, the password is the same as the employee number.



Reading Employee Details

BufferedReader br = new BufferedReader(new FileReader(EmployeeFile))



The employee CSV file is opened and read line by line.



br.readLine();



The first line (header) is skipped.



String\[] data = line.split(",");



Each line is split into fields using a comma delimiter.



The program retrieves the following employee information:



Employee Number



First Name



Last Name



Birthday



Hourly Rate



If the employee number matches the login credentials, the program displays the employee information.



Display Employee Information



Example output:



LOGIN SUCCESSFUL



Employee Number: 10001

Employee Name: John Doe

Birthday: 05/12/1995



This confirms that the login credentials are valid.



Date and Time Format

DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");



These formatters tell Java how to read:



Dates such as 06/15/2024



Times such as 8:30 or 17:00



Process Attendance by Month

for (int month = 6; month <= 12; month++)



The program processes payroll for each month from June to December.



For each month, two variables store total hours worked:



double firstPayHours = 0;

double secondPayHours = 0;



firstPayHours → hours worked from day 1 to 15



secondPayHours → hours worked from day 16 to end of month



Reading Attendance Records

BufferedReader br = new BufferedReader(new FileReader(AttendanceFile))



The attendance file is read line by line.



Only records matching the logged-in employee number are processed.



if (!employeeNo.equals(username)) {

&nbsp;   continue;

}



This ensures the program ignores attendance records of other employees.



Parsing Date and Time

LocalDate date = LocalDate.parse(data\[3], dateFormat);

LocalTime logIn = LocalTime.parse(data\[4], timeFormat);

LocalTime logOut = LocalTime.parse(data\[5], timeFormat);



The program converts the CSV values into date and time objects.



Limiting Work Hours



The program only counts work hours between:



8:10 AM (10-minute grace period)



5:00 PM



LocalTime workStart = LocalTime.of(8, 10);

LocalTime workEnd = LocalTime.of(17, 0);



If an employee logs in before 8:10 AM, it is adjusted to 8:10 AM.



If an employee logs out after 5:00 PM, it is adjusted to 5:00 PM.



This ensures extra hours are not counted, following the project requirements.



Computing Hours Worked

double hoursWorked = logOut.getHour() - logIn.getHour();

int minuteDiff = logOut.getMinute() - logIn.getMinute();

hoursWorked += minuteDiff / 60.0;



The program calculates total hours worked using the difference between login and logout times.



Assigning Hours to Pay Periods

if (date.getDayOfMonth() <= 15) {

&nbsp;   firstPayHours += hoursWorked;

} else {

&nbsp;   secondPayHours += hoursWorked;

}



Days 1–15 belong to the first pay period



Days 16–end of month belong to the second pay period



Salary Computation

Gross Salary

double firstGross = firstPayHours \* hrlyRate;

double secondGross = secondPayHours \* hrlyRate;



Gross salary is calculated by multiplying:



Hours Worked × Hourly Rate



Monthly gross salary is the sum of both pay periods.



double monthlyGross = firstGross + secondGross;

Government Deductions



The program calculates the following deductions based on Philippine payroll rules:



SSS



SSS contribution is determined using a salary bracket table.



Example:



if (monthlyGross < 3250) sss = 135;

...

else sss = 1125;

PhilHealth

double philhealth = monthlyGross \* 0.03;



PhilHealth is 3% of monthly salary.



Minimum contribution: ₱300



Maximum contribution: ₱1800



Pag-IBIG

if (monthlyGross <= 1500)

&nbsp;   pagibig = monthlyGross \* 0.01;

else

&nbsp;   pagibig = monthlyGross \* 0.02;



1% if salary ≤ ₱1500



2% if salary > ₱1500



Withholding Tax



The program calculates withholding tax using the Philippine TRAIN Law tax brackets.



Example:



if (monthlyGross <= 20832)

&nbsp;   tax = 0;

else if (monthlyGross < 33333)

&nbsp;   tax = (monthlyGross - 20833) \* 0.20;



Higher salary brackets apply progressively higher tax rates.



Net Salary

double netSalary = monthlyGross - (sss + philhealth + pagibig + tax);



Net salary is calculated by subtracting all deductions from the monthly gross salary.



Payroll Output



For each month, the program displays:



Example output structure:



=== June ===



First Pay Period

Total Hours Worked:

Gross Salary:



Second Pay Period

Total Hours Worked:

Gross Salary:



Deductions

SSS:

PhilHealth:

Pag-IBIG:

Withholding Tax:



Net Salary:



This provides a clear payroll summary for the employee.



Notes



CSV files must exist inside the resources folder.



The program only processes payroll for June to December.



Login credentials currently use Employee Number as both username and password.



Work hours are restricted to 8:10 AM – 5:00 PM to follow the project rule that extra hours are not counted.



Government deductions are calculated automatically based on the employee’s monthly gross salary.



Members



Pauline Mae Mangilit

Aleah Venice Declarador

Abigail Lausing

Juliana Martina Relox

