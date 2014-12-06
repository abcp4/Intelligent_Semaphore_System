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

                    System.out.println("Vehicle id : " + eElement.getAttribute("id"));
                    carTrip.setId(Integer.parseInt(eElement.getAttribute("id")));
                    System.out.println("Departure time : " + eElement.getAttribute("depart"));
                    carTrip.setDepart(Float.parseFloat(eElement.getAttribute("depart")));
                    System.out.println("Departure Lane : " + eElement.getAttribute("departLane"));
                    carTrip.setDepartLane(eElement.getAttribute("departLane"));
                    System.out.println("Departure Position : " + eElement.getAttribute("departPos"));
                    carTrip.setDepartPos(Float.parseFloat(eElement.getAttribute("departPos")));
                    System.out.println("Departure Speed : " + eElement.getAttribute("departSpeed"));
                    carTrip.setDepartSpeed(Float.parseFloat(eElement.getAttribute("departSpeed")));
                    System.out.println("Departure Delay : " + eElement.getAttribute("departDelay"));
                    carTrip.setDepartDelay(Float.parseFloat(eElement.getAttribute("departDelay")));
                    System.out.println("Arrival Time : " + eElement.getAttribute("arrival"));
                    carTrip.setArrival(Float.parseFloat(eElement.getAttribute("arrival")));
                    System.out.println("Arrival Lane : " + eElement.getAttribute("arrivalLane"));
                    carTrip.setArrivalLane(eElement.getAttribute("arrivalLane"));
                    System.out.println("Arrival Position : " + eElement.getAttribute("arrivalPos"));
                    carTrip.setArrivalPos(Float.parseFloat(eElement.getAttribute("arrivalPos")));
                    System.out.println("Arrival Speed : " + eElement.getAttribute("arrivalSpeed"));
                    carTrip.setArrivalSpeed(Float.parseFloat(eElement.getAttribute("arrivalSpeed")));
                    System.out.println("Trip Duration : " + eElement.getAttribute("duration"));
                    carTrip.setDuration(Float.parseFloat(eElement.getAttribute("duration")));
                    System.out.println("Route Length : " + eElement.getAttribute("routeLength"));
                    carTrip.setRouteLength(Float.parseFloat(eElement.getAttribute("routeLength")));
                    System.out.println("Wait Steps : " + eElement.getAttribute("waitSteps"));
                    carTrip.setWaitSteps(Integer.parseInt(eElement.getAttribute("waitSteps")));

                    /*System.out.println("Reroute No : " + eElement.getAttribute("rerouteNo"));  --  NOT USED*/
                    /*System.out.println("Devices : " + eElement.getAttribute("devices"));  --  NOT USED*/

                    System.out.println("Emission data:");

                    NodeList emissions = eElement.getElementsByTagName("emissions");

                    Node eNode = emissions.item(0);

                    if (eNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element emiElement = (Element) eNode;

                        System.out.println("CO : " + emiElement.getAttribute("CO_abs"));
                        carTrip.setCO(Float.parseFloat(emiElement.getAttribute("CO_abs")));
                        System.out.println("CO2 : " + emiElement.getAttribute("CO2_abs"));
                        carTrip.setCO2(Float.parseFloat(emiElement.getAttribute("CO2_abs")));
                        System.out.println("HC : " + emiElement.getAttribute("HC_abs"));
                        carTrip.setHC(Float.parseFloat(emiElement.getAttribute("HC_abs")));
                        System.out.println("PMx : " + emiElement.getAttribute("PMx_abs"));
                        carTrip.setPMx(Float.parseFloat(emiElement.getAttribute("PMx_abs")));
                        System.out.println("NOx : " + emiElement.getAttribute("NOx_abs"));
                        carTrip.setNOx(Float.parseFloat(emiElement.getAttribute("NOx_abs")));
                        System.out.println("Fuel : " + emiElement.getAttribute("fuel_abs"));
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
