import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> list2 = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            NodeList employeeList = doc.getDocumentElement().getElementsByTagName("employee");

            long id = 0;
            String firstName = null;
            String lastName = null;
            String country = null;
            int age = 0;

            for (int i = 0; i < employeeList.getLength(); i++) {
                Node node = employeeList.item(i);
                NodeList employeeElements = node.getChildNodes();

                for (int j = 0; j < employeeElements.getLength(); j++) {
                    Node node_ = employeeElements.item(j);
                    switch (node_.getNodeName()) {
                        case "id":
                            id = Long.parseLong(node_.getTextContent());
                            break;
                        case "firstName":
                            firstName = node_.getTextContent();
                            break;
                        case "lastName":
                            lastName = node_.getTextContent();
                            break;
                        case "country":
                            country = node_.getTextContent();
                            break;
                        case "age":
                            age = Integer.parseInt(node_.getTextContent());
                            break;
                    }
                }
                Employee employee = new Employee(id, firstName, lastName, country, age);
                list2.add(employee);
            }
        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return list2;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> result = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            result = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void writeString(String string, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(string);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}