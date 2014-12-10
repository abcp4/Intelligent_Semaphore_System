package xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;

public class TripParser {

    public void TripParser() {
        System.out.println("Trip parser initialized");
    }

    public static void main (String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        if (args.length != 1) {
            System.err.println("Provide a file input");
            System.exit(1);
        }
        TripParser tp = new TripParser();
        ArrayList<CarTrip> ct = tp.getCarData(args[0]);
        tp.getSimulationData(ct, "result.txt");
    }

    public ArrayList<CarTrip> getCarData(String xmlPath) {
        ArrayList<CarTrip> carTrips = new ArrayList<>();

        try {

            File fXmlFile = new File(xmlPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //improves speed
            doc.getDocumentElement().normalize();


            NodeList nList = doc.getElementsByTagName("tripinfo");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    CarTrip carTrip = new CarTrip();

                    Element eElement = (Element) nNode;

                    carTrip.setId(Integer.parseInt(eElement.getAttribute("id")));
                    carTrip.setVehicleType(eElement.getAttribute("vType"));
                    carTrip.setDepart(Float.parseFloat(eElement.getAttribute("depart")));
                    carTrip.setDepartLane(eElement.getAttribute("departLane"));
                    carTrip.setDepartPos(Float.parseFloat(eElement.getAttribute("departPos")));
                    carTrip.setDepartSpeed(Float.parseFloat(eElement.getAttribute("departSpeed")));
                    carTrip.setDepartDelay(Float.parseFloat(eElement.getAttribute("departDelay")));
                    carTrip.setArrival(Float.parseFloat(eElement.getAttribute("arrival")));
                    carTrip.setArrivalLane(eElement.getAttribute("arrivalLane"));
                    carTrip.setArrivalPos(Float.parseFloat(eElement.getAttribute("arrivalPos")));
                    carTrip.setArrivalSpeed(Float.parseFloat(eElement.getAttribute("arrivalSpeed")));
                    carTrip.setDuration(Float.parseFloat(eElement.getAttribute("duration")));
                    carTrip.setRouteLength(Float.parseFloat(eElement.getAttribute("routeLength")));
                    carTrip.setWaitSteps(Integer.parseInt(eElement.getAttribute("waitSteps")));

                    /*System.out.println("Reroute No : \t" + eElement.getAttribute("rerouteNo"));  --  NOT USED*/
                    /*System.out.println("Devices : \t" + eElement.getAttribute("devices"));  --  NOT USED*/

                    NodeList emissions = eElement.getElementsByTagName("emissions");

                    Node eNode = emissions.item(0);

                    if (eNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element emiElement = (Element) eNode;

                        carTrip.setCO(Float.parseFloat(emiElement.getAttribute("CO_abs")));
                        carTrip.setCO2(Float.parseFloat(emiElement.getAttribute("CO2_abs")));
                        carTrip.setHC(Float.parseFloat(emiElement.getAttribute("HC_abs")));
                        carTrip.setPMx(Float.parseFloat(emiElement.getAttribute("PMx_abs")));
                        carTrip.setNOx(Float.parseFloat(emiElement.getAttribute("NOx_abs")));
                        carTrip.setFuel(Float.parseFloat(emiElement.getAttribute("fuel_abs")));
                    }

                    carTrips.add(carTrip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return carTrips;
    }

    public static void getSimulationData(ArrayList<CarTrip> data, String resultPath) throws FileNotFoundException,
            UnsupportedEncodingException {
        double averageSpeed, averageWait, averageCO, averageCO2, averageHC, averagePMx, averageNOx, averageFuel;
        averageSpeed = averageWait = averageCO = averageCO2 = averageHC = averagePMx = averageNOx = averageFuel = 0;

        double busAverageSpeed, busAverageWait, busAverageCO, busAverageCO2, busAverageHC, busAveragePMx, busAverageNOx,
                busAverageFuel;
        busAverageSpeed = busAverageWait = busAverageCO = busAverageCO2 = busAverageHC = busAveragePMx =
                busAverageNOx = busAverageFuel = 0;

        double emeAverageSpeed, emeAverageWait, emeAverageCO, emeAverageCO2, emeAverageHC, emeAveragePMx,
                emeAverageNOx, emeAverageFuel;
        emeAverageSpeed = emeAverageWait = emeAverageCO = emeAverageCO2 = emeAverageHC = emeAveragePMx =
                emeAverageNOx = emeAverageFuel = 0;

        double sdSpeed, sdWait, sdCO, sdCO2, sdHC, sdPMx, sdNOx, sdFuel;
        sdSpeed = sdWait = sdCO = sdCO2 = sdHC = sdPMx = sdNOx = sdFuel = 0;

        double busSdSpeed, busSdWait, busSdCO, busSdCO2, busSdHC, busSdPMx, busSdNOx, busSdFuel;
        busSdSpeed = busSdWait = busSdCO = busSdCO2 = busSdHC = busSdPMx = busSdNOx = busSdFuel = 0;

        double emeSdSpeed, emeSdWait, emeSdCO, emeSdCO2, emeSdHC, emeSdPMx, emeSdNOx, emeSdFuel;
        emeSdSpeed = emeSdWait = emeSdCO = emeSdCO2 = emeSdHC = emeSdPMx = emeSdNOx = emeSdFuel = 0;

        int averageC = 0;
        int busC = 0;
        int emeC = 0;
        String vehicleType;

        //Gathering vehicle data

        for (CarTrip trip : data) {
            vehicleType = trip.getVehicleType();
            System.out.println(vehicleType);
            if (vehicleType.equals("nor")) {
                System.out.println("NOR!");
                averageC++;
                averageSpeed += trip.getRouteLength() / trip.getDuration();
                averageWait += trip.getWaitSteps();
                averageCO += trip.getCO() / trip.getRouteLength();
                averageCO2 += trip.getCO2() / trip.getRouteLength();
                averageHC += trip.getHC() / trip.getRouteLength();
                averagePMx += trip.getPMx() / trip.getRouteLength();
                averageNOx += trip.getNOx() / trip.getRouteLength();
                averageFuel += trip.getFuel() / trip.getRouteLength();
            } else if (vehicleType.equals("bus")) {
                System.out.println("BUS!");
                busC++;
                busAverageSpeed += trip.getRouteLength() / trip.getDuration();
                busAverageWait += trip.getWaitSteps();
                busAverageCO += trip.getCO() / trip.getRouteLength();
                busAverageCO2 += trip.getCO2() / trip.getRouteLength();
                busAverageHC += trip.getHC() / trip.getRouteLength();
                busAveragePMx += trip.getPMx() / trip.getRouteLength();
                busAverageNOx += trip.getNOx() / trip.getRouteLength();
                busAverageFuel += trip.getFuel() / trip.getRouteLength();
            } else if (vehicleType.equals("eme")) {
                System.out.println("EME!");
                emeC++;
                emeAverageSpeed += trip.getRouteLength() / trip.getDuration();
                emeAverageWait += trip.getWaitSteps();
                emeAverageCO += trip.getCO() / trip.getRouteLength();
                emeAverageCO2 += trip.getCO2() / trip.getRouteLength();
                emeAverageHC += trip.getHC() / trip.getRouteLength();
                emeAveragePMx += trip.getPMx() / trip.getRouteLength();
                emeAverageNOx += trip.getNOx() / trip.getRouteLength();
                emeAverageFuel += trip.getFuel() / trip.getRouteLength();
            }
        }

        //Calculating avarages

        averageSpeed = averageSpeed / averageC;
        averageWait = averageWait / averageC;
        averageCO = averageCO / averageC;
        averageCO2 = averageCO2 / averageC;
        averageHC = averageHC / averageC;
        averagePMx = averagePMx / averageC;
        averageNOx = averageNOx / averageC;
        averageFuel = averageFuel / averageC;

        busAverageSpeed = busAverageSpeed / busC;
        busAverageWait = busAverageWait / busC;
        busAverageCO = busAverageCO / busC;
        busAverageCO2 = busAverageCO2 / busC;
        busAverageHC = busAverageHC / busC;
        busAveragePMx = busAveragePMx / busC;
        busAverageNOx = busAverageNOx / busC;
        busAverageFuel = busAverageFuel / busC;

        emeAverageSpeed = emeAverageSpeed / emeC;
        emeAverageWait = emeAverageWait / emeC;
        emeAverageCO = emeAverageCO / emeC;
        emeAverageCO2 = emeAverageCO2 / emeC;
        emeAverageHC = emeAverageHC / emeC;
        emeAveragePMx = emeAveragePMx / emeC;
        emeAverageNOx = emeAverageNOx / emeC;
        emeAverageFuel = emeAverageFuel / emeC;

        // Regathering data for standart deviation

        for (CarTrip trip : data) {
            vehicleType = trip.getVehicleType();
            if (vehicleType.equals("nor")) {
                sdSpeed += Math.pow((trip.getRouteLength() / trip.getDuration() - averageSpeed), 2);
                sdWait += Math.pow((trip.getWaitSteps() - averageWait), 2);
                sdCO += Math.pow(((trip.getCO() / trip.getRouteLength()) - averageCO), 2);
                sdCO2 += Math.pow(((trip.getCO2() / trip.getRouteLength()) - averageCO2), 2);
                sdHC += Math.pow(((trip.getHC() / trip.getRouteLength()) - averageHC), 2);
                sdPMx += Math.pow(((trip.getPMx() / trip.getRouteLength()) - averagePMx), 2);
                sdNOx += Math.pow(((trip.getNOx() / trip.getRouteLength()) - averageNOx), 2);
                sdFuel += Math.pow(((trip.getFuel() / trip.getRouteLength()) - averageFuel), 2);
            } else if (vehicleType.equals("bus")) {
                busSdSpeed += Math.pow((trip.getRouteLength() / trip.getDuration() - busAverageSpeed), 2);
                busSdWait += Math.pow((trip.getWaitSteps() - busAverageWait), 2);
                busSdCO += Math.pow(((trip.getCO() / trip.getRouteLength()) - busAverageCO), 2);
                busSdCO2 += Math.pow(((trip.getCO2() / trip.getRouteLength()) - busAverageCO2), 2);
                busSdHC += Math.pow(((trip.getHC() / trip.getRouteLength()) - busAverageHC), 2);
                busSdPMx += Math.pow(((trip.getPMx() / trip.getRouteLength()) - busAveragePMx), 2);
                busSdNOx += Math.pow(((trip.getNOx() / trip.getRouteLength()) - busAverageNOx), 2);
                busSdFuel += Math.pow(((trip.getFuel() / trip.getRouteLength()) - busAverageFuel), 2);
            } else if (vehicleType.equals("eme")) {
                emeSdSpeed += Math.pow((trip.getRouteLength() / trip.getDuration() - emeAverageSpeed), 2);
                emeSdWait += Math.pow((trip.getWaitSteps() - emeAverageWait), 2);
                emeSdCO += Math.pow(((trip.getCO() / trip.getRouteLength()) - emeAverageCO), 2);
                emeSdCO2 += Math.pow(((trip.getCO2() / trip.getRouteLength()) - emeAverageCO2), 2);
                emeSdHC += Math.pow(((trip.getHC() / trip.getRouteLength()) - emeAverageHC), 2);
                emeSdPMx += Math.pow(((trip.getPMx() / trip.getRouteLength()) - emeAveragePMx), 2);
                emeSdNOx += Math.pow(((trip.getNOx() / trip.getRouteLength()) - emeAverageNOx), 2);
                emeSdFuel += Math.pow(((trip.getFuel() / trip.getRouteLength()) - emeAverageFuel), 2);
            }
        }

        // Calculating standart deviations

        sdSpeed = Math.sqrt(sdSpeed / averageC);
        sdWait = Math.sqrt(sdWait / averageC);
        sdCO = Math.sqrt(sdCO / averageC);
        sdCO2 = Math.sqrt(sdCO2 / averageC);
        sdHC = Math.sqrt(sdHC / averageC);
        sdPMx = Math.sqrt(sdPMx / averageC);
        sdNOx = Math.sqrt(sdNOx / averageC);
        sdFuel = Math.sqrt(sdFuel / averageC);

        busSdSpeed = Math.sqrt(busSdSpeed / busC);
        busSdWait = Math.sqrt(busSdWait / busC);
        busSdCO = Math.sqrt(busSdCO / busC);
        busSdCO2 = Math.sqrt(busSdCO2 / busC);
        busSdHC = Math.sqrt(busSdHC / busC);
        busSdPMx = Math.sqrt(busSdPMx / busC);
        busSdNOx = Math.sqrt(busSdNOx / busC);
        busSdFuel = Math.sqrt(busSdFuel / busC);

        emeSdSpeed = Math.sqrt(emeSdSpeed / emeC);
        emeSdWait = Math.sqrt(emeSdWait / emeC);
        emeSdCO = Math.sqrt(emeSdCO / emeC);
        emeSdCO2 = Math.sqrt(emeSdCO2 / emeC);
        emeSdHC = Math.sqrt(emeSdHC / emeC);
        emeSdPMx = Math.sqrt(emeSdPMx / emeC);
        emeSdNOx = Math.sqrt(emeSdNOx / emeC);
        emeSdFuel = Math.sqrt(emeSdFuel / emeC);

        // Printing results
        PrintWriter writer = new PrintWriter(resultPath, "UTF-8");

        String norData = "Normal Vehicle Data: \n--------------------------------------------\n" +
                "Average Speed (m/s): \t" + averageSpeed + "\n" +
                "Speed Standart Deviation: \t" + sdSpeed + "\n" +
                "Average Wait Steps: \t" + averageWait + "\n" +
                "Wait Steps Standart Deviation: \t" + sdWait + "\n" +
                "Average CO (mg/m): \t" + averageCO + "\n" +
                "CO Standart Deviation: \t" + sdCO + "\n" +
                "Average CO2 (mg/m): \t" + averageCO2 + "\n" +
                "CO2 Standart Deviation: \t" + sdCO2 + "\n" +
                "Average HC (mg/m): \t" + averageHC + "\n" +
                "HC Standart Deviation: \t" + sdHC + "\n" +
                "Average PMx (mg/m): \t" + averagePMx + "\n" +
                "PMx Standart Deviation: \t" + sdPMx + "\n" +
                "Average NOx (mg/m): \t" + averageNOx + "\n" +
                "NOx Standart Deviation: \t" + sdNOx + "\n" +
                "Average Fuel Consumption (ml/m): \t" + averageFuel + "\n" +
                "Fuel Consumption Standart Deviation: \t" + sdFuel + "\n";

        writer.println(norData);
        System.out.println(norData);

        String busData = "Bus Data: \n--------------------------------------------\n" +
                "Average Speed (m/s): \t" + busAverageSpeed + "\n" +
                "Speed Standart Deviation: \t" + busSdSpeed + "\n" +
                "Average Wait Steps: \t" + busAverageWait + "\n" +
                "Wait Steps Standart Deviation: \t" + busSdWait + "\n" +
                "Average CO (mg/m): \t" + busAverageCO + "\n" +
                "CO Standart Deviation: \t" + busSdCO + "\n" +
                "Average CO2 (mg/m): \t" + busAverageCO2 + "\n" +
                "CO2 Standart Deviation: \t" + busSdCO2 + "\n" +
                "Average HC (mg/m): \t" + busAverageHC + "\n" +
                "HC Standart Deviation: \t" + busSdHC + "\n" +
                "Average PMx (mg/m): \t" + busAveragePMx + "\n" +
                "PMx Standart Deviation: \t" + busSdPMx + "\n" +
                "Average NOx (mg/m): \t" + busAverageNOx + "\n" +
                "NOx Standart Deviation: \t" + busSdNOx + "\n" +
                "Average Fuel Consumption (ml/m): \t" + busAverageFuel + "\n" +
                "Fuel Consumption Standart Deviation: \t" + busSdFuel + "\n";

        writer.println(busData);
        System.out.println(busData);

        String emeData = "Emergency Vehicle Data: \n--------------------------------------------\n" +
                "Average Speed (m/s): \t" + emeAverageSpeed + "\n" +
                "Speed Standart Deviation: \t" + emeSdSpeed + "\n" +
                "Average Wait Steps: \t" + emeAverageWait + "\n" +
                "Wait Steps Standart Deviation: \t" + emeSdWait + "\n" +
                "Average CO (mg/m): \t" + emeAverageCO + "\n" +
                "CO Standart Deviation: \t" + emeSdCO + "\n" +
                "Average CO2 (mg/m): \t" + emeAverageCO2 + "\n" +
                "CO2 Standart Deviation: \t" + emeSdCO2 + "\n" +
                "Average HC (mg/m): \t" + emeAverageHC + "\n" +
                "HC Standart Deviation: \t" + emeSdHC + "\n" +
                "Average PMx (mg/m): \t" + emeAveragePMx + "\n" +
                "PMx Standart Deviation: \t" + emeSdPMx + "\n" +
                "Average NOx (mg/m): \t" + emeAverageNOx + "\n" +
                "NOx Standart Deviation: \t" + emeSdNOx + "\n" +
                "Average Fuel Consumption (ml/m): \t" + emeAverageFuel + "\n" +
                "Fuel Consumption Standart Deviation: \t" + emeSdFuel + "\n";

        writer.println(emeData);
        System.out.println(emeData);
        writer.flush();
        writer.close();
    }

}
