package xml;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class VehicleCreator {

    public static void VehicleCreator() {
    }

    public static void main(String[] args) {
        String path = "";
        if (args.length != 1) {
            System.err.println("Provide a file input");
            System.exit(1);
        }
        path = args[0];

        VehicleCreator vc = new VehicleCreator();
        vc.generateVehicles(92, 3, 5, path, path); // normal, emergency, bus
        System.out.println("Edited " + path + " successfully");
    }

    public void generateVehicles(int normalPercentage, int emergencyPercentage, int busPercentage, String xmlPath,
                                 String newXmlPath) {

        if (normalPercentage + emergencyPercentage + busPercentage != 100) {
            System.out.println("Sum of percentages must be 100. Exiting.");
            return;
        }

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("routes");
            doc.appendChild(rootElement);

            Attr namespace = doc.createAttribute("xmlns:xsi");
            namespace.setValue("http://www.w3.org/2001/XMLSchema-instance");
            Attr namespaceSchema = doc.createAttribute("xsi:noNamespaceSchemaLocation");
            namespaceSchema.setValue("http://sumo-sim.org/xsd/routes_file.xsd");

            rootElement.setAttributeNode(namespace);
            rootElement.setAttributeNode(namespaceSchema);

            // vehicle types

            Element eme = doc.createElement("vType");
            Element bus = doc.createElement("vType");
            Element nor = doc.createElement("vType");
            rootElement.appendChild(eme);
            rootElement.appendChild(bus);
            rootElement.appendChild(nor);

            eme.setAttribute("id", "eme");
            eme.setAttribute("accel", "1.8");
            eme.setAttribute("decel", "4.5");
            eme.setAttribute("sigma", "0.5");
            eme.setAttribute("length", "5");
            eme.setAttribute("maxSpeed", "140");
            eme.setAttribute("vClass", "emergency");
            eme.setAttribute("color", "red");

            bus.setAttribute("id", "bus");
            bus.setAttribute("accel", "0.4");
            bus.setAttribute("decel", "8.5");
            bus.setAttribute("sigma", "0.5");
            bus.setAttribute("length", "10");
            bus.setAttribute("maxSpeed", "40");
            bus.setAttribute("vClass", "bus");
            bus.setAttribute("color", "blue");

            nor.setAttribute("id", "nor");
            nor.setAttribute("accel", "2.0");
            nor.setAttribute("decel", "4.5");
            nor.setAttribute("sigma", "0.5");
            nor.setAttribute("length", "5");
            nor.setAttribute("maxSpeed", "120");

            // vehicles

            try {

                File fXmlFile = new File(xmlPath);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document orDoc = dBuilder.parse(fXmlFile);

                //improves speed
                orDoc.getDocumentElement().normalize();

                NodeList vehicleList = orDoc.getElementsByTagName("vehicle");
                NodeList newVehicleList;

                int partition = vehicleList.getLength() / 5;
                int phaseCounter = 0;
                double departureCounter = 1;
                for (int temp = 0; temp < vehicleList.getLength(); temp++) {
                    Node nNode = vehicleList.item(temp).cloneNode(true);
                    Element vehicle = (Element) nNode;
                    vehicle.setAttribute("depart", String.format("%.2f", departureCounter));
                    double d = Math.random() * 100;
                    if (d <= normalPercentage) {
                        vehicle.setAttribute("type", "nor");
                    } else if (d > normalPercentage && d <= emergencyPercentage + normalPercentage) {
                        vehicle.setAttribute("type", "eme");
                    } else {
                        vehicle.setAttribute("type", "bus");
                    }

                    doc.adoptNode(nNode);
                    doc.getDocumentElement().appendChild(nNode);
                    if (temp % partition == 0) {
                        phaseCounter++;
                    }
                    switch (phaseCounter) {
                        case 1:
                        case 3:
                        case 5:
                            departureCounter += 4.0;
                            break;
                        default:
                            departureCounter += 2.0;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(newXmlPath);

            // Output to console for testing
            //  StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            //System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}