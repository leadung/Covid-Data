package covid_19;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Scanner scanner = new Scanner(System.in);
        List<CovidData> covidData = new ArrayList<>();

        try {
            //get path to project
            String userDirectory = System.getProperty("user.dir");
            // read file covid-data.csv
            BufferedReader br = new BufferedReader(new FileReader(userDirectory + "/src/covid-data.csv"));
            // read field name line in file data
            String line = br.readLine();
            //get first line data
            line = br.readLine();

            while (line != null) {
                CovidData cd = new CovidData(line.split(","));
                // If the data under consideration has the same iso code as the row before it, then it will be accumulated to facilitate the calculation
                if (covidData.size()>0 && cd.getIsoCode().equalsIgnoreCase(covidData.get(covidData.size() - 1).getIsoCode())) {
                    CovidData cd_last = covidData.get(covidData.size() - 1);
                    cd.setNewCases(cd.getNewCases() + cd_last.getNewCases());
                    cd.setNewDeaths(cd.getNewDeaths() + cd_last.getNewDeaths());
                }
                // Add the line of data just read to the list
                covidData.add(cd);
                // read the next line
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // The covid_19.Data object stores the condition of the start - end date and selected area
        Data data = new Data();
        // The object that stores the grouping, the groups after the split, and the data calculated in the groups
        Summary summary = new Summary(covidData);
        // covid_19.Display object to create a table or chart
        Display display = new Display();
        // Loop to receive user requests

        loop:
        while (true) {
            System.out.println("1.Choose geographic area.\n" +
                    "2.Choose the time range.\n" +
                    "3.Summary.\n" +
                    "4.Show table.\n" +
                    "5.Show chart.\n" +
                    "6.Finish.\n" +
                    "Choose an option: ");
            int number = scanner.nextInt();

            switch (number) {
                case 1:
                    data.setGeographicArea(covidData);
                    break;
                case 2:
                    data.enterDate();
                    break;
                case 3:
                    summary.setUpSummary(data);
                    break;
                case 4:
                    display.showTable(summary);
                    break;
                case 5:
                    display.showChart(summary);
                    break;
                default:
                    break loop;
            }
        }
        System.out.println(data);
    }
}