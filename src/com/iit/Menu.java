package com.iit;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

public class Menu extends Application {

    static final int SEATING_CAPACITY = 42;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Scanner sc = new Scanner(System.in);

        // create hashmap to store customers
        HashMap<String, Customer> customerHashMap = new HashMap<>();
        String option = "z";

        while (!option.equalsIgnoreCase("Q")) { //select the options
            System.out.println("\n<<------Welcome to Train Booking System !!!------>>");
            System.out.println("Enter \"A\" to add a customer :");
            System.out.println("Enter \"V\" to view all the seat :");
            System.out.println("Enter \"E\" to view empty seat :");
            System.out.println("Enter \"D\" to delete a booked seat :");
            System.out.println("Enter \"F\" to find a seat by customer name :");
            System.out.println("Enter \"O\" to order seat by Name :");
            System.out.println("Enter \"S\" to store data to file :");
            System.out.println("Enter \"L\" to load data from file :");
            System.out.println("Enter \"Q\" to quit :");

            option = sc.next();

            switch (option) {
                case "A":
                case "a":
                    try {
                        System.out.println("Add Customer");
                        addCustomer(customerHashMap);
                    } catch (Exception e) {
                        System.out.println("Invalid Input");
                    }
                    break;
                case "V":
                case "v":
                    try {
                        System.out.println("View All");
                        viewAllSeats(customerHashMap);
                    } catch (Exception e) {
                        System.out.println("Invalid Input");
                    }
                    break;
                case "E":
                case "e":
                    try {
                        System.out.println("View Empty");
                        viewEmptySeat(customerHashMap);
                    } catch (Exception e) {
                        System.out.println("Invalid Input");
                    }
                    break;
                case "D":
                case "d":
                    try {
                        System.out.println("Delete Booking");
                        deleteBooking(sc, customerHashMap);
                    } catch (Exception e) {
                        System.out.println("Invalid Input");
                    }
                    break;
                case "F":
                case "f":
                    try {
                        System.out.println("Find Seat by Customer");
                        findSeatByCustomer(sc, customerHashMap);
                    } catch (Exception e) {
                        System.out.println("Invalid Input");
                    }
                    break;
                case "O":
                case "o":
                    try {
                        System.out.println("Order Seats by Customer\'s name:");
                        orderSeatByName(customerHashMap);
                    } catch (Exception e) {
                        System.out.println("Invalid Input");
                    }
                    break;
                case "S":
                case "s":
                    try {
                        System.out.println("Store Data to File");
                        storeDataToFile(customerHashMap);
                    } catch (Exception e) {
                        System.out.println("Invalid Input");
                    }
                    break;
                case "L":
                case "l":
                    try {
                        System.out.println("Load Data from File");
                        loadDataFromFile(customerHashMap);
                    } catch (Exception e) {
                        System.out.println("Invalid Input");
                    }
                    break;
                case "Q":
                case "q":
                    System.out.println("quit the program");
                    break;
            }


        }


    }


    //add customer and booking date and select route
    private void addCustomer(HashMap<String, Customer> customerHashMap) {
        Stage stage = new Stage();
        VBox vbox = new VBox(20);
        vbox.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(vbox, 750, 450);
        stage.setScene(scene);
        DatePicker date = new DatePicker();

        date.setValue(LocalDate.now());
        date.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });


        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        Label checkInlabel = new Label("Date:");
        gridPane.add(checkInlabel, 0, 0);
        GridPane.setHalignment(checkInlabel, HPos.LEFT);
        gridPane.add(date, 0, 1);

        //select route combo box
        final ComboBox route = new ComboBox();
        route.getItems().addAll(
                "Colombo to Badulla",
                "Badulla to Colombo"
        );


        route.setValue("Colombo to Badulla");

        gridPane.add(new Label("Route: "), 1, 0);
        gridPane.add(route, 1, 1);

        //button to add customer
        GridPane gridPaneSeat = new GridPane();
        Button addButton = new Button("Add Customer");
        gridPane.add(addButton, 2, 1);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                ArrayList<Integer> seats = getReservedSeats(customerHashMap, date.getValue(), route.getValue().toString());

                Button[][] btns = new Button[7][6];
                gridPaneSeat.setVisible(true);

                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < 6; j++) {
                        if ((i * 6 + j + 1) < 10) {  //seat numbering
                            btns[i][j] = new Button("Seat- 0" + (i * 6 + j + 1));
                        } else {
                            btns[i][j] = new Button("Seat- " + (i * 6 + j + 1));
                        }

                        gridPaneSeat.setVgap(10);
                        gridPaneSeat.setHgap(40);

                        int arrayNumber = i * 6 + j;


                        if (seats.contains(arrayNumber + 1)) {
                            btns[i][j].setDisable(true);
                        }
                        //setOnAction
                        btns[i][j].setOnAction(new EventHandler<ActionEvent>() {
                            public void handle(ActionEvent event) {

                                System.out.println("Seat number :" + (arrayNumber + 1));//print seat number in console
                                TextInputDialog td = new TextInputDialog();

                                td.setTitle("Customer Details");
                                td.setHeaderText("Enter your name: firstName<space>surname");

                                final Optional<String> result = td.showAndWait();//show the dialog box for add customer name

                                if (result.isPresent()) {
                                    Customer customer = new Customer();
                                    customer.setName(result.get());
                                    customer.setRoute(route.getValue().toString());
                                    customer.setSeatNumber(arrayNumber + 1);
                                    customer.setDate(date.getValue());

                                    //print customer name , customer route and select date.
                                    System.out.println("\n----New Customer Added---\nCustomer name : " + customer.getName() + "\nRoute : " + customer.getRoute() + "\nSeat Number : " + customer.getSeatNumber() + "\nDate : " + customer.getDate());

                                    //put new customer details to hash map
                                    customerHashMap.put(customer.getName(), customer);

                                }
                                stage.close();
                            }
                        });
                        gridPaneSeat.add(btns[i][j], j, i + 3);
                    }
                }
            }
        });

        date.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            gridPaneSeat.setVisible(false);
        });

        route.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            gridPaneSeat.setVisible(false);
        });

        vbox.getChildren().add(gridPane);
        vbox.getChildren().add(gridPaneSeat);
        stage.setScene(scene);
        stage.showAndWait();
    }

    //view all seats in GUI
    private void viewAllSeats(HashMap<String, Customer> customerHashMap) {
        Stage stage = new Stage();
        VBox vbox = new VBox(20);
        vbox.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(vbox, 750, 450);
        stage.setScene(scene);
        DatePicker date = new DatePicker();

        date.setValue(LocalDate.now());

        // disable previous dates
        date.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        Label checkInlabel = new Label("Date:");
        gridPane.add(checkInlabel, 0, 0);
        GridPane.setHalignment(checkInlabel, HPos.LEFT);
        gridPane.add(date, 0, 1);

        //select route combo box
        final ComboBox route = new ComboBox();
        route.getItems().addAll(
                "Colombo to Badulla",
                "Badulla to Colombo"
        );


        route.setValue("Colombo to Badulla");

        gridPane.add(new Label("Route: "), 1, 0);
        gridPane.add(route, 1, 1);

        GridPane gridPaneSeat = new GridPane();

        //button to add view all seat
        Button addButton = new Button("View All Seats");
        gridPane.add(addButton, 2, 1);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //get reserved seats
                ArrayList<Integer> seats = getReservedSeats(customerHashMap, date.getValue(), route.getValue().toString());
                Button[][] btns = new Button[7][6];
                gridPaneSeat.setVisible(true);

                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < 6; j++) {
                        if ((i * 6 + j + 1) < 10) {
                            btns[i][j] = new Button("Seat- 0" + (i * 6 + j + 1));
                        } else {
                            btns[i][j] = new Button("Seat- " + (i * 6 + j + 1));
                        }

                        gridPaneSeat.setVgap(10);
                        gridPaneSeat.setHgap(40);

                        int arrayNumber = i * 6 + j;

                        if (seats.contains(arrayNumber + 1)) {
                            btns[i][j].setStyle("-fx-background-color: #ff4d4d"); //booking seats are red
                        } else {
                            btns[i][j].setStyle("-fx-background-color: #00ff00"); //empty seats are green
                        }

                        gridPaneSeat.add(btns[i][j], j, i + 3);
                    }

                }

            }

        });

        date.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            gridPaneSeat.setVisible(false);
        });

        route.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            gridPaneSeat.setVisible(false);
        });

        vbox.getChildren().add(gridPane);
        vbox.getChildren().add(gridPaneSeat);
        stage.setScene(scene);
        stage.showAndWait();
    }

    //view only empty seats
    private void viewEmptySeat(HashMap<String, Customer> customerHashMap) {
        Stage stage = new Stage();
        VBox vbox = new VBox(20);
        vbox.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(vbox, 750, 450);
        stage.setScene(scene);
        DatePicker date = new DatePicker();

        date.setValue(LocalDate.now());
        date.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        Label checkInlabel = new Label("Date:");
        gridPane.add(checkInlabel, 0, 0);
        GridPane.setHalignment(checkInlabel, HPos.LEFT);
        gridPane.add(date, 0, 1);


        final ComboBox route = new ComboBox();
        route.getItems().addAll(
                "Colombo to Badulla",
                "Badulla to Colombo"
        );


        route.setValue("Colombo to Badulla");

        gridPane.add(new Label("Route: "), 1, 0);
        gridPane.add(route, 1, 1);

        GridPane gridPaneSeat = new GridPane();

        //button to add customer
        Button addButton = new Button("View Empty Seats");
        gridPane.add(addButton, 2, 1);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                ArrayList<Integer> seats = getReservedSeats(customerHashMap, date.getValue(), route.getValue().toString());
                Button[][] btns = new Button[7][6];
                gridPaneSeat.setVisible(true);

                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < 6; j++) {
                        if ((i * 6 + j + 1) < 10) {
                            btns[i][j] = new Button("Seat- 0" + (i * 6 + j + 1));
                        } else {
                            btns[i][j] = new Button("Seat- " + (i * 6 + j + 1));
                        }

                        gridPaneSeat.setVgap(10);
                        gridPaneSeat.setHgap(40);

                        int arrayNumber = i * 6 + j;

                        if (seats.contains(arrayNumber + 1)) {
                            btns[i][j].setStyle("-fx-background-color: #000000");
                        } else {
                            btns[i][j].setStyle("-fx-background-color: #00ff00");
                        }

                        gridPaneSeat.add(btns[i][j], j, i + 3);
                    }

                }

            }

        });

        date.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            gridPaneSeat.setVisible(false);
        });

        route.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            gridPaneSeat.setVisible(false);
        });

        vbox.getChildren().add(gridPane);
        vbox.getChildren().add(gridPaneSeat);
        stage.setScene(scene);
        stage.showAndWait();
    }

    //delete a booked seat
    private void deleteBooking(Scanner sc, HashMap<String, Customer> customerHashMap) {

        //get date,seat number,route option as user inputs
        System.out.println("Enter date: eg:2020-03-19");
        String date = sc.next();

        System.out.println("Enter seat number: ");
        String seatNumberS = sc.next();

        System.out.println("Enter route: \n 1. Colombo to Badulla\n 2. Badulla to Colombo");
        String routeS = sc.next();

        try {
            LocalDate localDate = LocalDate.parse(date); // convert string to date
            int seatNumber = Integer.parseInt(seatNumberS);
            int route = Integer.parseInt(routeS);
            if (seatNumber <= SEATING_CAPACITY && seatNumber > 0) {
                String routeOption;
                if (route == 1) {
                    routeOption = "Colombo to Badulla";
                    customerDelete(customerHashMap, localDate, seatNumber, routeOption);
                } else if (route == 2) {
                    routeOption = "Badulla to Colombo";
                    customerDelete(customerHashMap, localDate, seatNumber, routeOption);
                } else {
                    System.out.println("Invalid route");
                }
            } else {
                System.out.println("Seat Number is invalid.");
            }
        } catch (Exception e) {
            System.out.println("Invalid Input: Data as YYYY-mm-dd : Seat number and route option as integer");
        }
    }

    private void customerDelete(HashMap<String, Customer> customerHashMap, LocalDate localDate, int seatNumber, String routeOption) {
        Iterator<Map.Entry<String, Customer>> iter = customerHashMap.entrySet().iterator();
        boolean isCustomer = false;
        while (iter.hasNext() && !isCustomer) {
            Map.Entry<String, Customer> entry = iter.next();
            //check customer is available
            if ((localDate.equals(entry.getValue().getDate()) && (seatNumber == entry.getValue().getSeatNumber()) && routeOption.equals(entry.getValue().getRoute()))) {
                System.out.println("Customer is successfully deleted. \nName :"+entry.getValue().getName()+"\nBooking Date :" + entry.getValue().getDate() + "\nSeat Number : " + entry.getValue().getSeatNumber() + "\nRoute : " + entry.getValue().getRoute());
                iter.remove();
                isCustomer = true;
            }
        }
        if (!isCustomer) {
            System.out.println("No Customer Found.");
        }
    }

    //find a seat by customer name
    private void findSeatByCustomer(Scanner sc, HashMap<String, Customer> customerHashMap) {
        System.out.println("Enter customer\'s first name:");
        String firstName = sc.next();

        System.out.println("Enter customer\'s last name:");
        String lastName = sc.next();

        Customer customer = customerHashMap.get(firstName.trim()+" "+lastName.trim());

        if ((customer != null)) {
            System.out.println("Customer " + customer.getName() + " Details \nSeat Number: " + customer.getSeatNumber() + "\nDate: " + customer.getDate() + "\nRoute: " + customer.getRoute());
        } else {
            System.out.println("No customer by given name");
        }
    }

    //order seat by name
    private void orderSeatByName(HashMap<String, Customer> customerHashMap) {
        Set<String> keys = customerHashMap.keySet();
        String[] keyArray = new String[keys.size()];
        keys.toArray(keyArray);

        //bubble sort
        String temp;

        for (int j = 0; j < keyArray.length; j++) {
            for (int i = j + 1; i < keyArray.length; i++) {
                // comparing adjacent strings
                if (keyArray[i].compareTo(keyArray[j]) < 0) {
                    temp = keyArray[j];
                    keyArray[j] = keyArray[i];
                    keyArray[i] = temp;
                }
            }
        }

        if(keyArray.length>0){
            for (String key : keyArray) {
                System.out.println(key + " : Seat Number : " + customerHashMap.get(key).getSeatNumber()+ " : Date : " + customerHashMap.get(key).getDate()+ " : Route : " + customerHashMap.get(key).getRoute());
            }
        }else {
            System.out.println("No Passengers to display");
        }

    }

    //store data to file
    private void storeDataToFile(HashMap<String, Customer> customerHashMap) {
        //write to file : "data"
        try {
            if(!customerHashMap.isEmpty()){
                File fileTwo = new File("src/data.txt");
                FileOutputStream fos = new FileOutputStream(fileTwo);
                PrintWriter pw = new PrintWriter(fos);

                for (Map.Entry<String, Customer> m : customerHashMap.entrySet()) {
                    pw.println(m.getKey() + "," + m.getValue().getSeatNumber() + "," + m.getValue().getRoute() + "," + m.getValue().getDate());
                }

                pw.flush();
                pw.close();
                fos.close();
            }else{
                System.out.println("No data to store");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in file writing");
        }
    }

    //load data from file
    private void loadDataFromFile(HashMap<String, Customer> customerHashMap) {
        //read from file
        try {
            File toRead = new File("src/data.txt");
            FileInputStream fis = new FileInputStream(toRead);

            Scanner sc = new Scanner(fis);

            //read data from file line by line:
            String currentLine;
            while (sc.hasNextLine()) {

                // line by line from the file
                currentLine = sc.nextLine();

                // split values
                String[] customer = currentLine.split(",");

                String name = customer[0];
                String seatNumber = customer[1];
                String route = customer[2];
                String date = customer[3];


                if (!customerHashMap.containsKey(name)) { // check if that customer is already exists in the data structure
                    // create new customer
                    Customer customerAdd = new Customer();
                    customerAdd.setName(name);
                    customerAdd.setSeatNumber(Integer.parseInt(seatNumber));
                    customerAdd.setRoute(route);
                    customerAdd.setDate(LocalDate.parse(date));

                    // add created customer to hash map
                    customerHashMap.put(name, customerAdd);

                    System.out.println(name + " : " + seatNumber + "," + route + "," + date);
                }


            }
            fis.close();

        } catch (Exception e) {
            System.out.println("Error in Read Data");
        }

    }

    //get reserved seats
    private ArrayList<Integer> getReservedSeats(HashMap<String, Customer> customerHashMap, LocalDate date, String route) {
        ArrayList<Integer> seats = new ArrayList<Integer>();

        customerHashMap.forEach((k, v) -> {
            if (date.equals(v.getDate()) && route.equals(v.getRoute())) {
                seats.add(v.getSeatNumber());
            }

        });
        return seats;
    }

}