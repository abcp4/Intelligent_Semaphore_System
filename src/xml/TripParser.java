package xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Vinnie on 28-Nov-14.
 * <p>
 * TODO:    Classe xml para viagens
 * Carregar toda a info do xml para um número x de carros
 * https://github.com/joaofloressantos/AIAD/blob/transmapi/manhattan/bettermanhattan/logs/trip.xml
 * <p>
 * TODO:    Classe xml de carro com possibilidade de alterar o tipo de carro dado o seu ID
 * a diferentes horas do dia ter fluxo de trânsito diferentes
 */

public class TripParser {

    public void TripParser() {
        System.out.println("Trip parser initialized");
    }

    public static void getSimulationData(ArrayList<CarTrip> data) {
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

        for (CarTrip trip : data) {
            vehicleType = trip.getVehicleType();
            if (vehicleType.equals("nor")) {
                averageC++;
                averageSpeed += trip.getRouteLength() / trip.getDuration();
                averageWait += trip.getWaitSteps();
                averageCO += trip.getCO();
                averageCO2 += trip.getCO2();
                averageHC += trip.getHC();
                averagePMx += trip.getPMx();
                averageNOx += trip.getNOx();
                averageFuel += trip.getFuel();
            } else if (vehicleType.equals("bus")) {
                busC++;
                busAverageSpeed += trip.getRouteLength() / trip.getDuration();
                busAverageWait += trip.getWaitSteps();
                busAverageCO += trip.getCO();
                busAverageCO2 += trip.getCO2();
                busAverageHC += trip.getHC();
                busAveragePMx += trip.getPMx();
                busAverageNOx += trip.getNOx();
                busAverageFuel += trip.getFuel();
            } else if (vehicleType.equals("eme")) {
                emeC++;
                emeAverageSpeed += trip.getRouteLength() / trip.getDuration();
                emeAverageWait += trip.getWaitSteps();
                emeAverageCO += trip.getCO();
                emeAverageCO2 += trip.getCO2();
                emeAverageHC += trip.getHC();
                emeAveragePMx += trip.getPMx();
                emeAverageNOx += trip.getNOx();
                emeAverageFuel += trip.getFuel();
            }
        }

        //Calculating normal car data

        averageSpeed = averageSpeed / averageC;
        averageWait = averageWait / averageC;
        averageCO = averageCO / averageC;
        averageCO2 = averageCO2 / averageC;
        averageHC = averageHC / averageC;
        averagePMx = averagePMx / averageC;
        averageNOx = averageNOx / averageC;
        averageFuel = averageFuel / averageC;

        for (CarTrip trip : data) {
            sdSpeed += Math.pow((trip.getRouteLength() / trip.getDuration() - averageSpeed), 2);
            sdWait += Math.pow((trip.getWaitSteps() - averageWait), 2);
            sdCO += Math.pow((trip.getCO() - averageCO), 2);
            sdCO2 += Math.pow((trip.getCO2() - averageCO2), 2);
            sdHC += Math.pow((trip.getHC() - averageHC), 2);
            sdPMx += Math.pow((trip.getPMx() - averagePMx), 2);
            sdNOx += Math.pow((trip.getNOx() - averageNOx), 2);
            sdFuel += Math.pow((trip.getFuel() - averageFuel), 2);
        }

        sdSpeed = Math.sqrt(sdSpeed / averageC);
        sdWait = Math.sqrt(sdWait / averageC);
        sdCO = Math.sqrt(sdCO / averageC);
        sdCO2 = Math.sqrt(sdCO2 / averageC);
        sdHC = Math.sqrt(sdHC / averageC);
        sdPMx = Math.sqrt(sdPMx / averageC);
        sdNOx = Math.sqrt(sdNOx / averageC);
        sdFuel = Math.sqrt(sdFuel / averageC);

        System.out.println("Normal Vehicle Data: \n--------------------------------------------\n" +
                "Average Speed: " + averageSpeed + "\n" +
                "Speed Standart Deviation: " + sdSpeed + "\n" +
                "Average Wait Steps: " + averageWait + "\n" +
                "Wait Steps Standart Deviation: " + sdWait + "\n" +
                "Average CO: " + averageCO + "\n" +
                "CO Standart Deviation: " + sdCO + "\n" +
                "Average CO2: " + averageCO2 + "\n" +
                "CO2 Standart Deviation: " + sdCO2 + "\n" +
                "Average HC: " + averageHC + "\n" +
                "HC Standart Deviation: " + sdHC + "\n" +
                "Average PMx: " + averagePMx + "\n" +
                "PMx Standart Deviation: " + sdPMx + "\n" +
                "Average NOx: " + averageNOx + "\n" +
                "NOx Standart Deviation: " + sdNOx + "\n" +
                "Average Fuel Consumption: " + averageFuel + "\n" +
                "Fuel Consumption Standart Deviation: " + sdFuel + "\n");

        //Calculating bus data

        busAverageSpeed = busAverageSpeed / busC;
        busAverageWait = busAverageWait / busC;
        busAverageCO = busAverageCO / busC;
        busAverageCO2 = busAverageCO2 / busC;
        busAverageHC = busAverageHC / busC;
        busAveragePMx = busAveragePMx / busC;
        busAverageNOx = busAverageNOx / busC;
        busAverageFuel = busAverageFuel / busC;

        for (CarTrip trip : data) {
            busSdSpeed += Math.pow((trip.getRouteLength() / trip.getDuration() - busAverageSpeed), 2);
            busSdWait += Math.pow((trip.getWaitSteps() - busAverageWait), 2);
            busSdCO += Math.pow((trip.getCO() - busAverageCO), 2);
            busSdCO2 += Math.pow((trip.getCO2() - busAverageCO2), 2);
            busSdHC += Math.pow((trip.getHC() - busAverageHC), 2);
            busSdPMx += Math.pow((trip.getPMx() - busAveragePMx), 2);
            busSdNOx += Math.pow((trip.getNOx() - busAverageNOx), 2);
            busSdFuel += Math.pow((trip.getFuel() - busAverageFuel), 2);
        }

        busSdSpeed = Math.sqrt(busSdSpeed / busC);
        busSdWait = Math.sqrt(busSdWait / busC);
        busSdCO = Math.sqrt(busSdCO / busC);
        busSdCO2 = Math.sqrt(busSdCO2 / busC);
        busSdHC = Math.sqrt(busSdHC / busC);
        busSdPMx = Math.sqrt(busSdPMx / busC);
        busSdNOx = Math.sqrt(busSdNOx / busC);
        busSdFuel = Math.sqrt(busSdFuel / busC);

        System.out.println("Bus Data: \n--------------------------------------------\n" +
                "Average Speed: " + busAverageSpeed + "\n" +
                "Speed Standart Deviation: " + busSdSpeed + "\n" +
                "Average Wait Steps: " + busAverageWait + "\n" +
                "Wait Steps Standart Deviation: " + busSdWait + "\n" +
                "Average CO: " + busAverageCO + "\n" +
                "CO Standart Deviation: " + busSdCO + "\n" +
                "Average CO2: " + busAverageCO2 + "\n" +
                "CO2 Standart Deviation: " + busSdCO2 + "\n" +
                "Average HC: " + busAverageHC + "\n" +
                "HC Standart Deviation: " + busSdHC + "\n" +
                "Average PMx: " + busAveragePMx + "\n" +
                "PMx Standart Deviation: " + busSdPMx + "\n" +
                "Average NOx: " + busAverageNOx + "\n" +
                "NOx Standart Deviation: " + busSdNOx + "\n" +
                "Average Fuel Consumption: " + busAverageFuel + "\n" +
                "Fuel Consumption Standart Deviation: " + busSdFuel + "\n");

        //Calculating eme data

        emeAverageSpeed = emeAverageSpeed / emeC;
        emeAverageWait = emeAverageWait / emeC;
        emeAverageCO = emeAverageCO / emeC;
        emeAverageCO2 = emeAverageCO2 / emeC;
        emeAverageHC = emeAverageHC / emeC;
        emeAveragePMx = emeAveragePMx / emeC;
        emeAverageNOx = emeAverageNOx / emeC;
        emeAverageFuel = emeAverageFuel / emeC;

        for (CarTrip trip : data) {
            emeSdSpeed += Math.pow((trip.getRouteLength() / trip.getDuration() - emeAverageSpeed), 2);
            emeSdWait += Math.pow((trip.getWaitSteps() - emeAverageWait), 2);
            emeSdCO += Math.pow((trip.getCO() - emeAverageCO), 2);
            emeSdCO2 += Math.pow((trip.getCO2() - emeAverageCO2), 2);
            emeSdHC += Math.pow((trip.getHC() - emeAverageHC), 2);
            emeSdPMx += Math.pow((trip.getPMx() - emeAveragePMx), 2);
            emeSdNOx += Math.pow((trip.getNOx() - emeAverageNOx), 2);
            emeSdFuel += Math.pow((trip.getFuel() - emeAverageFuel), 2);
        }

        emeSdSpeed = Math.sqrt(emeSdSpeed / emeC);
        emeSdWait = Math.sqrt(emeSdWait / emeC);
        emeSdCO = Math.sqrt(emeSdCO / emeC);
        emeSdCO2 = Math.sqrt(emeSdCO2 / emeC);
        emeSdHC = Math.sqrt(emeSdHC / emeC);
        emeSdPMx = Math.sqrt(emeSdPMx / emeC);
        emeSdNOx = Math.sqrt(emeSdNOx / emeC);
        emeSdFuel = Math.sqrt(emeSdFuel / emeC);

        System.out.println("Emergency Vehicle Data: \n--------------------------------------------\n" +
                "Average Speed: " + emeAverageSpeed + "\n" +
                "Speed Standart Deviation: " + emeSdSpeed + "\n" +
                "Average Wait Steps: " + emeAverageWait + "\n" +
                "Wait Steps Standart Deviation: " + emeSdWait + "\n" +
                "Average CO: " + emeAverageCO + "\n" +
                "CO Standart Deviation: " + emeSdCO + "\n" +
                "Average CO2: " + emeAverageCO2 + "\n" +
                "CO2 Standart Deviation: " + emeSdCO2 + "\n" +
                "Average HC: " + emeAverageHC + "\n" +
                "HC Standart Deviation: " + emeSdHC + "\n" +
                "Average PMx: " + emeAveragePMx + "\n" +
                "PMx Standart Deviation: " + emeSdPMx + "\n" +
                "Average NOx: " + emeAverageNOx + "\n" +
                "NOx Standart Deviation: " + emeSdNOx + "\n" +
                "Average Fuel Consumption: " + emeAverageFuel + "\n" +
                "Fuel Consumption Standart Deviation: " + emeSdFuel + "\n");
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

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("tripinfo");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    CarTrip carTrip = new CarTrip();

                    Element eElement = (Element) nNode;

                    //System.out.println("Vehicle id : " + eElement.getAttribute("id"));
                    carTrip.setId(Integer.parseInt(eElement.getAttribute("id")));
                    //System.out.println("Vehicle type : " + eElement.getAttribute("vType"));
                    carTrip.setVehicleType(eElement.getAttribute("vType"));
                    //System.out.println("Departure time : " + eElement.getAttribute("depart"));
                    carTrip.setDepart(Float.parseFloat(eElement.getAttribute("depart")));
                    //System.out.println("Departure Lane : " + eElement.getAttribute("departLane"));
                    carTrip.setDepartLane(eElement.getAttribute("departLane"));
                    //System.out.println("Departure Position : " + eElement.getAttribute("departPos"));
                    carTrip.setDepartPos(Float.parseFloat(eElement.getAttribute("departPos")));
                    //System.out.println("Departure Speed : " + eElement.getAttribute("departSpeed"));
                    carTrip.setDepartSpeed(Float.parseFloat(eElement.getAttribute("departSpeed")));
                    //System.out.println("Departure Delay : " + eElement.getAttribute("departDelay"));
                    carTrip.setDepartDelay(Float.parseFloat(eElement.getAttribute("departDelay")));
                    //System.out.println("Arrival Time : " + eElement.getAttribute("arrival"));
                    carTrip.setArrival(Float.parseFloat(eElement.getAttribute("arrival")));
                    //System.out.println("Arrival Lane : " + eElement.getAttribute("arrivalLane"));
                    carTrip.setArrivalLane(eElement.getAttribute("arrivalLane"));
                    //System.out.println("Arrival Position : " + eElement.getAttribute("arrivalPos"));
                    carTrip.setArrivalPos(Float.parseFloat(eElement.getAttribute("arrivalPos")));
                    //System.out.println("Arrival Speed : " + eElement.getAttribute("arrivalSpeed"));
                    carTrip.setArrivalSpeed(Float.parseFloat(eElement.getAttribute("arrivalSpeed")));
                    //System.out.println("Trip Duration : " + eElement.getAttribute("duration"));
                    carTrip.setDuration(Float.parseFloat(eElement.getAttribute("duration")));
                    //System.out.println("Route Length : " + eElement.getAttribute("routeLength"));
                    carTrip.setRouteLength(Float.parseFloat(eElement.getAttribute("routeLength")));
                    //System.out.println("Wait Steps : " + eElement.getAttribute("waitSteps"));
                    carTrip.setWaitSteps(Integer.parseInt(eElement.getAttribute("waitSteps")));

                    /*System.out.println("Reroute No : " + eElement.getAttribute("rerouteNo"));  --  NOT USED*/
                    /*System.out.println("Devices : " + eElement.getAttribute("devices"));  --  NOT USED*/

                    System.out.println("Emission data:");

                    NodeList emissions = eElement.getElementsByTagName("emissions");

                    Node eNode = emissions.item(0);

                    if (eNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element emiElement = (Element) eNode;

                        //System.out.println("CO : " + emiElement.getAttribute("CO_abs"));
                        carTrip.setCO(Float.parseFloat(emiElement.getAttribute("CO_abs")));
                        //System.out.println("CO2 : " + emiElement.getAttribute("CO2_abs"));
                        carTrip.setCO2(Float.parseFloat(emiElement.getAttribute("CO2_abs")));
                        //System.out.println("HC : " + emiElement.getAttribute("HC_abs"));
                        carTrip.setHC(Float.parseFloat(emiElement.getAttribute("HC_abs")));
                        //System.out.println("PMx : " + emiElement.getAttribute("PMx_abs"));
                        carTrip.setPMx(Float.parseFloat(emiElement.getAttribute("PMx_abs")));
                        //System.out.println("NOx : " + emiElement.getAttribute("NOx_abs"));
                        carTrip.setNOx(Float.parseFloat(emiElement.getAttribute("NOx_abs")));
                        //System.out.println("Fuel : " + emiElement.getAttribute("fuel_abs"));
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

}
