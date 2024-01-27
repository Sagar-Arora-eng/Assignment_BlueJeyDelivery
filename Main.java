import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm a";
    private static final long SEVEN_DAYS_IN_MILLIS = 7 * 24 * 60 * 60 * 1000;
    private static final long TEN_HOURS_IN_MILLIS = 10 * 60 * 60 * 1000;
    private static final long ONE_HOUR_IN_MILLIS = 1 * 60 * 60 * 1000;
    private static final long FOURTEEN_HOURS_IN_MILLIS = 14 * 60 * 60 * 1000;

    public static void main(String[] args) {
        String fileName = "C:\\Users\\DELL\\Downloads\\datasheet.csv";

        try {
            List<EmployeeRecord> employeeRecords = readCSV(fileName);
            analyzeAndPrintEmployees(employeeRecords);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static List<EmployeeRecord> readCSV(String fileName) throws IOException, ParseException {
        List<EmployeeRecord> employeeRecords = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            // Skipping the header line
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String timeIn = values[2].trim().isEmpty() ? "01/01/1970 00:00 AM" : values[2].trim();
                String timeOut = values[3].trim().isEmpty() ? "01/01/1970 00:00 AM" : values[3].trim();

                EmployeeRecord record = new EmployeeRecord(
                        values[0].trim(),
                        values[1].trim(),
                        timeIn,
                        timeOut,
                        values[4].trim(),
                        values[5].trim(),
                        values[6].trim(),
                        values[7].trim(),
                        values[8].trim()
                );
                employeeRecords.add(record);
            }
        }

        return employeeRecords;
    }

    private static void analyzeAndPrintEmployees(List<EmployeeRecord> employeeRecords) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        Set<EmployeeRecord> consecutiveDaysSet = new HashSet<>();
        Set<EmployeeRecord> lessThanTenHoursSet = new HashSet<>();
        Set<EmployeeRecord> moreThanFourteenHoursSet = new HashSet<>();

        for (EmployeeRecord record : employeeRecords) {
            if (!record.getTimeIn().isEmpty() && !record.getTimeOut().isEmpty()) {
                Date timeIn = dateFormat.parse(record.getTimeIn());
                Date timeOut = dateFormat.parse(record.getTimeOut());

                long timeDifference = timeOut.getTime() - timeIn.getTime();
                long hoursBetweenShifts = timeIn.getTime() - timeOut.getTime();

                if (timeDifference >= SEVEN_DAYS_IN_MILLIS) {
                    consecutiveDaysSet.add(record);
                }

                if (Math.abs(hoursBetweenShifts) < TEN_HOURS_IN_MILLIS && Math.abs(hoursBetweenShifts) > ONE_HOUR_IN_MILLIS) {
                    lessThanTenHoursSet.add(record);
                }

                if (timeDifference > FOURTEEN_HOURS_IN_MILLIS) {
                    moreThanFourteenHoursSet.add(record);
                }
            }
        }

        // Print section-wise results
        System.out.println("Employees who have worked for 7 consecutive days:");
        printEmployeeSet(consecutiveDaysSet);

        System.out.println("\nEmployees who have less than 10 hours between shifts but greater than 1 hour:");
        printEmployeeSet(lessThanTenHoursSet);

        System.out.println("\nEmployees who have worked for more than 14 hours in a single shift:");
        printEmployeeSet(moreThanFourteenHoursSet);
    }

    private static void printEmployeeSet(Set<EmployeeRecord> employees) {
        for (EmployeeRecord record : employees) {
            System.out.println("Employee: " + record.getEmployeeName() + ", Position: " + record.getPositionID());
        }
    }
}

class EmployeeRecord {
    private String positionID;
    private String positionStatus;
    private String timeIn;
    private String timeOut;
    private String timecardHours;
    private String payCycleStartDate;
    private String payCycleEndDate;
    private String employeeName;
    private String fileNumber;

    public EmployeeRecord(String positionID, String positionStatus, String timeIn, String timeOut,
                          String timecardHours, String payCycleStartDate, String payCycleEndDate,
                          String employeeName, String fileNumber) {
        this.positionID = positionID;
        this.positionStatus = positionStatus;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.timecardHours = timecardHours;
        this.payCycleStartDate = payCycleStartDate;
        this.payCycleEndDate = payCycleEndDate;
        this.employeeName = employeeName;
        this.fileNumber = fileNumber;
    }

    public String getPositionID() {
        return positionID;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public String getEmployeeName() {
        return employeeName;
    }
}
