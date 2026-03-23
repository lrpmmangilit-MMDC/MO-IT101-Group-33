package cp1.grp33.h1101.motorphpayrollsystem;

/**
 *
 * @author CP1 Group 33
 *
 * Computes semi-monthly gross pay and monthly government deductions
 * (SSS, PhilHealth, Pag-IBIG, Withholding Tax) for each employee
 * from June through December, based on attendance records.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MotorPHPayrollSystem {

// File paths for employee and attendance data
    static final String EMPLOYEE_FILE   = "resources/MotorPH_Employee Data - Employee Details.csv";
    static final String ATTENDANCE_FILE = "resources/MotorPH_Employee Data - Attendance Record.csv";

    // Date/time formatters shared across methods
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    // =========================================================
    // MAIN — entry point; coordinates login and payroll display
    // =========================================================
    public static void main(String[] args) {

        // Single Scanner opened here and closed at the end (not inside loops)
        try (Scanner scanner = new Scanner(System.in)) {

            // --- Step 1: Authenticate the employee ---
            System.out.print("Enter Employee Number: ");
            String username = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            // Attempt login and retrieve employee record
            String[] employeeRecord = login(username, password);
            if (employeeRecord == null) {
                System.out.println("\nInvalid login credentials.");
                return;
            }

            // Parse employee details from the record array
            String employeeNo = employeeRecord[0];
            String lastName    = employeeRecord[1];
            String firstName   = employeeRecord[2];
            String birthday    = employeeRecord[3];
            double hourlyRate  = Double.parseDouble(employeeRecord[employeeRecord.length - 1]);

            // Display employee header after successful login
            printEmployeeHeader(employeeNo, firstName, lastName, birthday);

            // --- Step 2: Load ALL attendance records once (avoid re-reading per month) ---
            List<String[]> attendanceRecords = loadAttendanceRecords();

            // --- Step 3: Compute and display payroll for June–December ---
            for (int month = 6; month <= 12; month++) {
                processMonthlyPayroll(employeeNo, month, hourlyRate, attendanceRecords);
            }

        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    // =========================================================
    // LOGIN — reads employee CSV and validates credentials
    // =========================================================
    static String[] login(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            br.readLine(); // skip header row

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String employeeNo = fields[0];

                // Simple authentication: password must match the employee number
                if (username.equals(employeeNo) && password.equals(employeeNo)) {
                    System.out.println("\nLOGIN SUCCESSFUL!");
                    return fields; // return the full employee record
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading Employee file: " + e.getMessage());
        }
        return null; // login failed
    }

    // =========================================================
    // PRINT EMPLOYEE HEADER — displays employee info post-login
    // =========================================================
    static void printEmployeeHeader(String employeeNo, String firstName,
                                    String lastName, String birthday) {
        System.out.println("\n********************");
        System.out.println("Employee Number : " + employeeNo);
        System.out.println("Employee Name   : " + firstName + " " + lastName);
        System.out.println("Birthday        : " + birthday);
        System.out.println("********************");
    }

    // =========================================================
    // LOAD ATTENDANCE RECORDS — reads the entire attendance CSV
    // once and stores each row as a String[] in a List.
    // This avoids re-opening the file for every month processed.
    // =========================================================
    static List<String[]> loadAttendanceRecords() {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            br.readLine(); // skip header row

            String line;
            while ((line = br.readLine()) != null) {
                // Basic guard: skip malformed rows that lack required columns
                String[] fields = line.split(",");
                if (fields.length < 6) continue;
                records.add(fields);
            }
        } catch (IOException e) {
            System.out.println("Error reading Attendance file: " + e.getMessage());
        }
        return records;
    }

    // =========================================================
    // PROCESS MONTHLY PAYROLL — computes hours, gross, deductions,
    // and net salary for one calendar month, then prints the result
    // =========================================================
    static void processMonthlyPayroll(String employeeNo, int month,
                                      double hourlyRate,
                                      List<String[]> attendanceRecords) {

        double firstPayHours  = 0; // hours worked days 1–15
        double secondPayHours = 0; // hours worked days 16–end

        // Scan pre-loaded attendance records for this employee and month
        for (String[] fields : attendanceRecords) {
            if (!fields[0].equals(employeeNo)) continue; // skip other employees

            // Parse the attendance date; skip rows that don't match this month
            LocalDate date = LocalDate.parse(fields[3], DATE_FORMAT);
            if (date.getMonthValue() != month) continue;

            // Parse raw log-in and log-out times
            LocalTime logIn  = LocalTime.parse(fields[4], TIME_FORMAT);
            LocalTime logOut = LocalTime.parse(fields[5], TIME_FORMAT);

            // Compute effective working hours for the day
            double hoursWorked = computeDailyHours(logIn, logOut);

            // Split hours into the correct semi-monthly pay period
            if (date.getDayOfMonth() <= 15) {
                firstPayHours  += hoursWorked;
            } else {
                secondPayHours += hoursWorked;
            }
        }

        // --- Gross salary: hourly rate × hours worked per period ---
        double firstGross  = firstPayHours  * hourlyRate;
        double secondGross = secondPayHours * hourlyRate;
        double monthlyGross = firstGross + secondGross;

        // --- Government deductions (applied monthly) ---
        double sss         = computeSSS(monthlyGross);
        double philhealth  = computePhilHealth(monthlyGross);
        double pagibig     = computePagIbig(monthlyGross);
        double tax         = computeWithholdingTax(monthlyGross);

        double totalDeductions = sss + philhealth + pagibig + tax;
        double netSalary = monthlyGross - totalDeductions;

        // --- Print payroll summary for this month ---
        printMonthlyPayroll(month, firstPayHours, firstGross,
                            secondPayHours, secondGross,
                            sss, philhealth, pagibig, tax, netSalary);
    }

    // =========================================================
    // COMPUTE DAILY HOURS — applies grace period, caps to shift end,
    // deducts the 1-hour unpaid lunch break, and returns net hours
    // =========================================================
    static double computeDailyHours(LocalTime logIn, LocalTime logOut) {

        LocalTime shiftStart = LocalTime.of(8, 0);  // official shift start
        LocalTime graceEnd   = LocalTime.of(8, 10); // grace period ends at 8:10
        LocalTime shiftEnd   = LocalTime.of(17, 0); // official shift end

        // Grace period: arrivals at or before 8:10 are treated as 8:00
        
        if (!logIn.isAfter(graceEnd)) {
            logIn = shiftStart;
        }

        // Cap log-out to shift end (overtime is not compensated here)
        if (logOut.isAfter(shiftEnd)) {
            logOut = shiftEnd;
        }

        // Compute raw hours between effective log-in and log-out
        double hoursWorked = logOut.getHour() - logIn.getHour()
                           + (logOut.getMinute() - logIn.getMinute()) / 60.0;

        // Deduct 1-hour unpaid lunch break if the employee worked more than 1 hour
        if (hoursWorked > 1) {
            hoursWorked -= 1.0;
        }

        // Guard against negative values (e.g., log-out before log-in)
        return Math.max(hoursWorked, 0);
    }

    // =========================================================
    // COMPUTE SSS — bracket table based on monthly gross salary
    // Source: SSS contribution schedule (employee share only)
    // =========================================================
    static double computeSSS(double monthlyGross) {
        if (monthlyGross < 3250)  return 135.00;
        if (monthlyGross < 3750)  return 157.50;
        if (monthlyGross < 4250)  return 180.00;
        if (monthlyGross < 4750)  return 202.50;
        if (monthlyGross < 5250)  return 225.00;
        if (monthlyGross < 5750)  return 247.50;
        if (monthlyGross < 6250)  return 270.00;
        if (monthlyGross < 6750)  return 292.50;
        if (monthlyGross < 7250)  return 315.00;
        if (monthlyGross < 7750)  return 337.50;
        if (monthlyGross < 8250)  return 360.00;
        if (monthlyGross < 8750)  return 382.50;
        if (monthlyGross < 9250)  return 405.00;
        if (monthlyGross < 9750)  return 427.50;
        if (monthlyGross < 10250) return 450.00;
        if (monthlyGross < 10750) return 472.50;
        if (monthlyGross < 11250) return 495.00;
        if (monthlyGross < 11750) return 517.50;
        if (monthlyGross < 12250) return 540.00;
        if (monthlyGross < 12750) return 562.50;
        if (monthlyGross < 13250) return 585.00;
        if (monthlyGross < 13750) return 607.50;
        if (monthlyGross < 14250) return 630.00;
        if (monthlyGross < 14750) return 652.50;
        if (monthlyGross < 15250) return 675.00;
        if (monthlyGross < 15750) return 697.50;
        if (monthlyGross < 16250) return 720.00;
        if (monthlyGross < 16750) return 742.50;
        if (monthlyGross < 17250) return 765.00;
        if (monthlyGross < 17750) return 787.50;
        if (monthlyGross < 18250) return 810.00;
        if (monthlyGross < 18750) return 832.50;
        if (monthlyGross < 19250) return 855.00;
        if (monthlyGross < 19750) return 877.50;
        if (monthlyGross < 20250) return 900.00;
        if (monthlyGross < 20750) return 922.50;
        if (monthlyGross < 21250) return 945.00;
        if (monthlyGross < 21750) return 967.50;
        if (monthlyGross < 22250) return 990.00;
        if (monthlyGross < 22750) return 1012.50;
        if (monthlyGross < 23250) return 1035.00;
        if (monthlyGross < 23750) return 1057.50;
        if (monthlyGross < 24250) return 1080.00;
        if (monthlyGross < 24750) return 1102.50;
        return 1125.00; // maximum SSS contribution
    }

    // =========================================================
    // COMPUTE PHILHEALTH — 3% of monthly gross, employee share only
    // Employee pays half of the total premium (premium / 2)
    // =========================================================
    static double computePhilHealth(double monthlyGross) {
        double totalPremium = monthlyGross * 0.03; // 3% total premium

        // Clamp total premium to PhilHealth-defined range
        totalPremium = Math.max(totalPremium, 300.0);
        totalPremium = Math.min(totalPremium, 1800.0);

        return totalPremium / 2.0; // employee pays half
    }

    // =========================================================
    // COMPUTE PAG-IBIG — 1% for ≤ ₱1,500 gross; 2% otherwise
    // Capped at ₱100 (employee share maximum per HDMF rules)
    // =========================================================
    static double computePagIbig(double monthlyGross) {
        double contribution;

        if (monthlyGross <= 1500) {
            contribution = monthlyGross * 0.01;
        } else {
            contribution = monthlyGross * 0.02;
        }

        return Math.min(contribution, 100.0); // cap at ₱100
    }

    // =========================================================
    // COMPUTE WITHHOLDING TAX — progressive BIR tax table
    // Applied on monthly gross (not taxable income after deductions
    // for this simplified implementation)
    // =========================================================
    static double computeWithholdingTax(double monthlyGross) {
        if (monthlyGross <= 20832)  return 0;
        if (monthlyGross < 33333)   return (monthlyGross - 20833) * 0.20;
        if (monthlyGross < 66667)   return 2500   + (monthlyGross - 33333) * 0.25;
        if (monthlyGross < 166667)  return 10833  + (monthlyGross - 66667) * 0.30;
        if (monthlyGross < 666667)  return 40833.33 + (monthlyGross - 166667) * 0.32;
        return                             200833.33 + (monthlyGross - 666667) * 0.35;
    }

    // =========================================================
    // PRINT MONTHLY PAYROLL — formats and prints the complete
    // payroll summary for one month to standard output
    // =========================================================
    static void printMonthlyPayroll(int month,
                                    double firstPayHours,  double firstGross,
                                    double secondPayHours, double secondGross,
                                    double sss, double philhealth,
                                    double pagibig, double tax,
                                    double netSalary) {

        // Capitalize the month name (e.g., "JUNE" → "June")
        String monthName = Month.of(month).name();
        monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase();

        System.out.println("\n=== " + monthName + " ===");

        // First pay period (days 1–15)
        System.out.println("\n  ** First Pay Period  **");
        System.out.printf("  Total Hours Worked : %.2f%n", firstPayHours);
        System.out.printf("  Gross Salary       : %.2f%n", firstGross);

        // Second pay period (days 16–end of month)
        System.out.println("\n  ** Second Pay Period **");
        System.out.printf("  Total Hours Worked : %.2f%n", secondPayHours);
        System.out.printf("  Gross Salary       : %.2f%n", secondGross);

        // Government deductions
        System.out.println("\n  ** Deductions **");
        System.out.printf("  SSS              : %.2f%n", sss);
        System.out.printf("  PhilHealth       : %.2f%n", philhealth);
        System.out.printf("  Pag-IBIG         : %.2f%n", pagibig);
        System.out.printf("  Withholding Tax  : %.2f%n", tax);

        // Net salary after all deductions
        System.out.println("\n  *********************");
        System.out.printf("  Net Salary : %.2f%n", netSalary);
        System.out.println("  *********************");
    }
}


