package covid_19;

import covid_19.CovidData;
import covid_19.Data;
import covid_19.Group;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Summary {

    // save list group data
    private final List<Group> groups;
    private final List<CovidData> covidData;

    // number of day in the group
    private long interval;
    private long surplus;

    public Summary(List<CovidData> covidData) {
        this.covidData = covidData;
        this.groups = new ArrayList<>();
    }

    public void setUpSummary(Data data) {

        Scanner scanner = new Scanner(System.in);

        int number;

        do {
            System.out.println(
                    "Choose a group condition:\n" +
                            "1. No grouping.\n" +
                            "2. Number of groups.\n" +
                            "3. Number of days.\n"
            );
            number = scanner.nextInt();
            // when user type wrong, type again
        } while (number < 1 || number > 3);

        switch (number) {

            case 1:
                // no grouping option.
                this.interval = 1;
                break;

            case 2:
                // the variable k is calculation for start date to the end date
                long k = ChronoUnit.DAYS.between(data.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        data.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) + 1;
                System.out.println("Enter number of groups: ");
                // option #2 is for number of group, get k/numberofGroup to get the divided group
                long numberOfGroup = scanner.nextInt();
                interval = k / numberOfGroup;
                // there will be chances that the number will be divied not equally. so get the surplus. to divide the residuals among groups
                surplus = k % numberOfGroup;
                break;

            default:
                k = ChronoUnit.DAYS.between(data.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        data.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) + 1;
                do {
                    System.out.println("Enter number of days: ");
                    interval = scanner.nextLong();
                } while (k % (interval) != 0);

        }

        do {
            System.out.println(
                    "1.New total.\n"
                            + "2.Up to.\n"
                            + "Choose result type:"
            );
            number = scanner.nextInt();
        } while (number < 1 || number > 2);

        groups.removeAll(groups);

        long sur = surplus;
        int c = 0;
        if (sur > 0) c = 1;

        for (int i = 0; i < covidData.size(); i += interval + c) {
            // get i in raw list data
            CovidData cd = covidData.get(i);
            // check if it is right for the conditions
            if (data.checkInvalid(cd)) {
                Group group = new Group();
                group.setIndex(i);
                if (sur-- > 0) c = 1;
                else c = 0;

                for (int j = 0; j < interval + c; j++) {
                    CovidData cd1 = covidData.get(i + j);
                    if (data.checkInvalid(cd1)) {
                        group.covidD.add(cd1);
                    }
                }
                groups.add(group);
            }
        }

        // calcultaion metric in every group
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            // get the last element
            CovidData last = group.covidD.get(group.covidD.size() - 1);
            if (number == 2) {
                // select the total number of cases at that time
                group.setCases(last.getNewCases());
                group.setDeaths(last.getNewDeaths());
                group.setVaccines(last.getPeopleVaccinated());
                groups.set(i, group);
            } else {
                // choose the number of increments in that group
                // get data for the last day - data for the day immediately before the group
                //because the data has been accumulated
                System.out.println(group);
                CovidData pre = null;
                if(group.getIndex()>0){
                    pre = covidData.get(group.getIndex() - 1);
                    group.setCases(last.getNewCases() - pre.getNewCases());
                    group.setDeaths(last.getNewDeaths() - pre.getNewDeaths());
                    group.setVaccines(last.getPeopleVaccinated() - pre.getPeopleVaccinated());
                }else{
                    group.setCases(last.getNewCases());
                    group.setDeaths(last.getNewDeaths());
                    group.setVaccines(last.getPeopleVaccinated());
                }

                groups.set(i, group);
            }
        }
    }

    public List<Group> getGroups() {
        return groups;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public List<CovidData> getCovidData() {
        return covidData;
    }
}