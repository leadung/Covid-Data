package covid_19;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class Display {
    private Summary summary;

    public Display() {
    }

    public void showTable(Summary summary) {

        Scanner scanner = new Scanner(System.in);

        int number;

        do{
            System.out.println(
                    "1.Cases\n"+
                            "2.Deaths\n"+
                            "3.Vaccines\n"+
                            "Choose an option:"
            );
            number = scanner.nextInt();
        }while(number<1 || number>3);

        // get the groups from the calculated summary
        List<Group> groups = summary.getGroups();
        System.out.printf("%-30s| %-30s%n", "Range", "Value");
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            Date first = group.covidD.get(0).getDate();
            Date last = group.covidD.get(group.covidD.size() - 1).getDate();
            long cases = group.cases;
            long deaths = group.deaths;
            long vaccines = group.vaccines;
            System.out.printf("%-30s| %-30d%n", new SimpleDateFormat("MM/dd/yyyy").format(first) +"-"+ new SimpleDateFormat("MM/dd/yyyy").format(last),
                    (number==1)?cases:((number==2)?deaths:vaccines));
        }
    }

    public void showChart(Summary summary){
        List<Group> groups = summary.getGroups();
        // create dataset for chart
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for(int i=0; i<groups.size(); i++){
            Group group = groups.get(i);
            // put info into line cases
            line_chart_dataset.addValue( group.getCases(), "Cases" , new SimpleDateFormat("MM/dd/yyyy").format(group.getCovidD().get(group.getCovidD().size()-1).getDate()));
            // put info into line vaccine
            line_chart_dataset.addValue( group.getCases(), "Vaccine" , new SimpleDateFormat("MM/dd/yyyy").format(group.getCovidD().get(group.getCovidD().size()-1).getDate()));
            // put info into  line deaths
            line_chart_dataset.addValue( group.getDeaths(), "Deaths" , new SimpleDateFormat("MM/dd/yyyy").format(group.getCovidD().get(group.getCovidD().size()-1).getDate()));
        }


        // creat line chart
        JFreeChart lineChartObject = ChartFactory.createLineChart(
                "covid_19.Summary chart","covid_19.Group",
                "Value",
                line_chart_dataset, PlotOrientation.VERTICAL,
                true,true,false);

        int width = 640;    /* Width of the image */
        int height = 480;   /* Height of the image */
        File lineChart = new File( "LineChart.jpeg" );
        try {
            // save images line chart into file
            ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}