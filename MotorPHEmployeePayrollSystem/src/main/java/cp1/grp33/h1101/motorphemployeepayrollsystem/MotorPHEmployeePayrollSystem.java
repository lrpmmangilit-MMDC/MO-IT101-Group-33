package cp1.grp33.h1101.motorphemployeepayrollsystem;

/**
 *
 * @author cp1.grp33.h1101
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MotorPHEmployeePayrollSystem {

    public static void main(String[] args) {
     
        String EmployeeFile = "resources/MotorPH_Employee Data - Employee Details.csv";
        String AttendanceFile = "resources/MotorPH_Employee Data - Attendance Record.csv";

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

        try (Scanner scanner = new Scanner(System.in)) {

            // LOGIN
            System.out.print("Enter Employee Number: ");
            String username = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            boolean loginSuccess = false;
            String hourlyRate = "";

            try (BufferedReader br = new BufferedReader(new FileReader(EmployeeFile))) {
                br.readLine(); // skip header
                String line;

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");

                    String employeeNo = data[0];
                    String firstName = data[2];
                    String lastName = data[1];
                    String birthday = data[3];
                    hourlyRate = data[data.length - 1];

                    if (username.equals(employeeNo) && password.equals(employeeNo)) {
                        loginSuccess = true;
                        System.out.println("\nLOGIN SUCCESSFUL!");
                        System.out.println("\n********************");
                        System.out.println("Employee Number: " + employeeNo);
                        System.out.println("Employee Name: " + firstName + " " + lastName);
                        System.out.println("Birthday: " + birthday);
                        System.out.println("\n********************");
                        break;
                    }
                }

                if (!loginSuccess) {
                    System.out.println("\nInvalid login credentials.");
                }

            } catch (IOException e) {
                System.out.println("Error reading Employee file.");
                return;
            }

            if (loginSuccess) {
                
                double hrlyRate = Double.parseDouble(hourlyRate);

                // Loop through months (June = 6 to December = 12)
                for (int month = 6; month <= 12; month++) {

                    double firstPayHours = 0;
                    double secondPayHours = 0;

                    try (BufferedReader br = new BufferedReader(new FileReader(AttendanceFile))) {
                        br.readLine(); // skip header
                        String line;

                        while ((line = br.readLine()) != null) {
                            String[] data = line.split(",");
                            String employeeNo = data[0];

                            if (!employeeNo.equals(username)) {
                                continue; // skip other employees
                            }

                            LocalDate date = LocalDate.parse(data[3], dateFormat);
                            if (date.getMonthValue() != month) {
                                continue; // skip other months
                            }

                            LocalTime logIn = LocalTime.parse(data[4], timeFormat);
                            LocalTime logOut = LocalTime.parse(data[5], timeFormat);

                            // Limit login/out to 8:00 - 17:00
                            LocalTime workStart = LocalTime.of(8, 10); // Grace Period - 10 minutes
                            LocalTime workEnd = LocalTime.of(17, 0);

                            if (logIn.isBefore(workStart)) {
                                logIn = workStart;
                            }

                            if (logOut.isAfter(workEnd)) {
                                logOut = workEnd;
                            }

                            // Compute hours
                            double hoursWorked = logOut.getHour() - logIn.getHour();
                            int minuteDiff = logOut.getMinute() - logIn.getMinute();
                            hoursWorked += minuteDiff / 60.0;

                            // Add to pay period
                            if (date.getDayOfMonth() <= 15) {
                                firstPayHours += hoursWorked;
                            } else {
                                secondPayHours += hoursWorked;
                            }
                        }

                    } catch (IOException e) {
                        System.out.println("Error reading Attendance file.");
                    }
                    
                            // COMPUTE GROSS SALARY (hourly rate * hours worked)
                            
                            double firstGross = firstPayHours * hrlyRate;
                            double secondGross = secondPayHours * hrlyRate;
                            
                            // MONTHLY GROSS
                            double monthlyGross = firstGross + secondGross;
                            
//GOVERNMENT DEDUCTIONS
// ================= SSS =================
double sss = 0;

if (monthlyGross < 3250) sss = 135;
else if (monthlyGross < 3750) sss = 157.50;
else if (monthlyGross < 4250) sss = 180;
else if (monthlyGross < 4750) sss = 202.50;
else if (monthlyGross < 5250) sss = 225;
else if (monthlyGross < 5750) sss = 247.50;
else if (monthlyGross < 6250) sss = 270;
else if (monthlyGross < 6750) sss = 292.50;
else if (monthlyGross < 7250) sss = 315;
else if (monthlyGross < 7750) sss = 337.50;
else if (monthlyGross < 8250) sss = 360;
else if (monthlyGross < 8750) sss = 382.50;
else if (monthlyGross < 9250) sss = 405;
else if (monthlyGross < 9750) sss = 427.50;
else if (monthlyGross < 10250) sss = 450;
else if (monthlyGross < 10750) sss = 472.50;
else if (monthlyGross < 11250) sss = 495;
else if (monthlyGross < 11750) sss = 517.50;
else if (monthlyGross < 12250) sss = 540;
else if (monthlyGross < 12750) sss = 562.50;
else if (monthlyGross < 13250) sss = 585;
else if (monthlyGross < 13750) sss = 607.50;
else if (monthlyGross < 14250) sss = 630;
else if (monthlyGross < 14750) sss = 652.50;
else if (monthlyGross < 15250) sss = 675;
else if (monthlyGross < 15750) sss = 697.50;
else if (monthlyGross < 16250) sss = 720;
else if (monthlyGross < 16750) sss = 742.50;
else if (monthlyGross < 17250) sss = 765;
else if (monthlyGross < 17750) sss = 787.50;
else if (monthlyGross < 18250) sss = 810;
else if (monthlyGross < 18750) sss = 832.50;
else if (monthlyGross < 19250) sss = 855;
else if (monthlyGross < 19750) sss = 877.50;
else if (monthlyGross < 20250) sss = 900;
else if (monthlyGross < 20750) sss = 922.50;
else if (monthlyGross < 21250) sss = 945;
else if (monthlyGross < 21750) sss = 967.50;
else if (monthlyGross < 22250) sss = 990;
else if (monthlyGross < 22750) sss = 1012.50;
else if (monthlyGross < 23250) sss = 1035;
else if (monthlyGross < 23750) sss = 1057.50;
else if (monthlyGross < 24250) sss = 1080;
else if (monthlyGross < 24750) sss = 1102.50;
else sss = 1125;


// ================= PHILHEALTH =================
double philhealth = monthlyGross * 0.03;

if (philhealth < 300) philhealth = 300;
if (philhealth > 1800) philhealth = 1800;


// ================= PAGIBIG =================
double pagibig;

if (monthlyGross <= 1500) {
    pagibig = monthlyGross * 0.01;
} else {
    pagibig = monthlyGross * 0.02;
}


// ================= WITHHOLDING TAX =================
double tax = 0;

if (monthlyGross <= 20832) {
    tax = 0;
}
else if (monthlyGross < 33333) {
    tax = (monthlyGross - 20833) * 0.20;
}
else if (monthlyGross < 66667) {
    tax = 2500 + (monthlyGross - 33333) * 0.25;
}
else if (monthlyGross < 166667) {
    tax = 10833 + (monthlyGross - 66667) * 0.30;
}
else if (monthlyGross < 666667) {
    tax = 40833.33 + (monthlyGross - 166667) * 0.32;
}
else {
    tax = 200833.33 + (monthlyGross - 666667) * 0.35;
}

double netSalary = monthlyGross - (sss + philhealth + pagibig + tax);

                    // PAY PERIOD
                    String monthName = Month.of(month).name();
                    monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
                    
                    // FIRST PAY PERIOD

                    System.out.println("=== " + monthName + " ===");
                    
                    System.out.println(); 
                    System.out.println("**First Pay Period**");
                    System.out.println(); 
                    
                    System.out.println("Total Hours Worked: " + firstPayHours);
                    System.out.println("Gross Salary (Hourly Rate): " + firstGross);
                    
                    // SECOND PAY PERIOD
                    
                    System.out.println(); 
                    System.out.println("**Second Pay Period**");
                    System.out.println(); 
                                        
                    System.out.println("Total Hours Worked: " + secondPayHours);
                    System.out.println("Gross Salary (Hourly Rate): " + secondGross);
                    System.out.println(); 
                    
                    System.out.println("**Deductions**");
                    System.out.println("SSS: " + sss);
                    System.out.println("PhilHealth: " + philhealth);
                    System.out.println("PAG-IBIG: " + pagibig);
                    System.out.println("Withholding Tax:" + tax);
                    
                    System.out.println(); 
                    System.out.println("*********************");
                    System.out.println(); 
                    System.out.println("Net Salary: " + netSalary);
                    System.out.println(); 
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}