package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

import model.Activity;
import model.Equipment;
import model.FilterState;
import model.Timeslot;
import persistence.JsonReader;
import persistence.JsonWriter;


// Activity registration app
public class ActivityRegistration extends JFrame implements ActionListener {
    private Activity swimming;
    private Activity bowling;
    private Activity tennis;

    private Equipment noodle;
    private Equipment ball;
    private Equipment racket;
    private Equipment birdie;
    private Equipment floatie;

    private Scanner input;

    private static final int WIDTH = 1500;
    private static final int HEIGHT = 1000;

    private FilterState filterState;


    private static final String JSON_STRING = "./data/schedule.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private JTextField nameTextField;
    private JTextField equipment1TextField;
    private JTextField equipment2TextField;

    private JFrame popupFrame;

    // EFFECTS: runs activity register application
    public ActivityRegistration() {
        super("Activity Registrar");

        this.filterState = FilterState.ALL;

        jsonWriter = new JsonWriter(JSON_STRING);
        jsonReader = new JsonReader(JSON_STRING);
        input = new Scanner(System.in);
        input.useDelimiter("\r?\n|\r");
        initializeGraphics();
        initializeMenu();
        runApp();

        this.popupFrame = new JFrame();
    }

    // EFFECTS: creates window
    private void initializeGraphics() {
        
        setLayout(new BorderLayout(10, 10));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // REQUIRES: int width and height
    // EFFECTS: returns new window
    private JFrame createNewWindow(int width, int height) {
        JFrame ret = new JFrame();
        ret.setMinimumSize(new Dimension(width, height));
        ret.setLayout(new BorderLayout(10, 10));
        ret.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return ret;
    }

    // EFFECTS: creates initial window for loading and creating schedule
    private void initializeMenu() {
        JLabel title = new JLabel("Activity Registrar", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton load = new JButton("Load saved schedule");
        load.setActionCommand("load");
        load.addActionListener(this);

        JButton newSchedule = new JButton("Create a new schedule");
        newSchedule.setActionCommand("newSchedule");
        newSchedule.addActionListener(this);

        panel.add(load);
        panel.add(newSchedule);
        add(panel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    // REQUIRES: action event
    // EFFECTS: listens to actions performed 
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("load")) {
            readActivities();
            clearCanvas();

            scheduleViewer();   
        } else if (e.getActionCommand().equals("newSchedule")) {
            init();
            clearCanvas();
            scheduleViewer();
        } else if (e.getActionCommand().equals("currentSchedule")) {
            clearCanvas();
            scheduleViewer();
        } else if (e.getActionCommand().equals("closepopup")) {
            popupFrame.dispose();
        } else if (e.getActionCommand().equals("exitwithoutsaving")) {
            dispose();
            System.exit(0);
        } else if (e.getActionCommand().equals("exitwithsaving")) {
            saveActivities();
            dispose();
            System.exit(0);
        } else {
            handleSplitAction(e);
        }
    }

    // REQUIRES: action event that has "-"
    // EFFECTS: calls function based on event given
    public void handleSplitAction(ActionEvent e) {
        String[] split = e.getActionCommand().split("-");
        if (split[0].equals("reserve")) {
            makeNewReservation(split[1], Integer.parseInt(split[2]));
        } else if (split[0].equals("cancel")) {
            cancelReservation(split[1], Integer.parseInt(split[2]));
        } else if (split[0].equals("view")) {
            viewFilteredSchedule(split[1]);
        } else {
            handleTimeslotCommand(split[0], Integer.parseInt(split[1]));
        }
    }

    // REQUIRES: a state for the filter
    // EFFECTS: repaints schedule and changes filter view
    private void viewFilteredSchedule(String state) {
        if (state.equals("All")) {
            filterState = FilterState.ALL;
            clearCanvas();
            scheduleViewer();
        } else if (state.equals("Available")) {
            filterState = FilterState.AVAILABLE;
            clearCanvas();
            scheduleViewer();
        } else {
            filterState = FilterState.BOOKED;
            clearCanvas();
            scheduleViewer();
        }
    }

    // REQUIRES: activity name and timeslot index
    // EFFECTS: cancels timeslot in given activity
    private void cancelReservation(String activityName, int index) {
        Activity a = setActivity(activityName);
        Timeslot t = a.getTimeslots().get(index);
        a.cancelReservation(t.getStart());
        popupFrame.dispose();
        revalidate();
        repaint();
        clearCanvas();
        scheduleViewer();
    }


    // REQUIRES: an activity name, timeslot index
    // MODIFIES: this
    // EFFECTS: updates reservation data
    private void makeNewReservation(String activityName, int index) {
        Activity a = setActivity(activityName);
        Timeslot t = a.getTimeslots().get(index);
        t.cancel();
        String name = nameTextField.getText();
        ArrayList<Equipment> equipmentList = new ArrayList<>();
        int equipment1 = Integer.parseInt(equipment1TextField.getText());
        equipmentList.add(new Equipment(a.getEquipment().get(0).getName(), equipment1));
        if (a.getEquipment().size() > 1) {
            int equipment2 = Integer.parseInt(equipment2TextField.getText());
            equipmentList.add(new Equipment(a.getEquipment().get(1).getName(), equipment2));
        }
        a.makeReservation(name, equipmentList, t);
        popupFrame.dispose();
        revalidate();
        repaint();
        clearCanvas();
        scheduleViewer();
    }

    // REQUIRES: an activity name and a timeslot index
    // EFFECTS: displays GUI to edit timeslot data
    private void handleTimeslotCommand(String activityName, int index) {
        Activity a = setActivity(activityName);
        Timeslot t = a.getTimeslots().get(index);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        if (t.isBooked()) {
            editBookedMenu(panel, t, a);
            
            
            JButton cancel = new JButton("Cancel reservation");
            cancel.setActionCommand("cancel-" + a.getName() + "-" + a.getTimeslots().indexOf(t));
            cancel.addActionListener(this);
            panel.add(cancel);
        } else {
            editAvailableMenu(panel, t, a);
        }
        JButton close = new JButton("Close without saving");
        close.setActionCommand("closepopup");
        close.addActionListener(this);
        
        panel.add(close);
        popupFrame = createNewWindow(300, 400);
        popupFrame.add(panel);
        popupFrame.setVisible(true);

    }

    // REQUIRES: a frame to add booking changing reservation
    // MODIFIES: this
    // EFFECTS: allows user to edit given reservation that is booked
    private void editBookedMenu(JPanel panel, Timeslot t, Activity a) {
        panel.add(new JLabel("Booked by " + t.getName()));
        panel.add(new JLabel("Time: " + t.getStart() + "-" + t.getEnd()));
        panel.add(new JLabel("Equipment booked:"));
        for (Equipment e : t.getEquipment()) {
            panel.add(new JLabel(e.getQuantity() + " " + e.getName()));
        }
        JLabel equip1 = new JLabel("Equipment available:");
        equip1.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(equip1);
        for (Equipment e : a.getEquipment()) {
            panel.add(new JLabel(e.getQuantity() + " " + e.getName()));
        }

        textInputs(panel, a);
        JButton edit = new JButton("Save changes to reservation");
        edit.setActionCommand("reserve-" + a.getName() + "-" + a.getTimeslots().indexOf(t));
        edit.addActionListener(this);
        panel.add(edit);
        
    }

    // REQUIRES: a frame to add text fields, an activity
    // MODIFIES: this
    // EFFECTS: adds text inputs of names and equipment
    private void textInputs(JPanel panel, Activity a) {
        panel.add(new JLabel("Enter the name of the reservation:"));
        nameTextField = new JTextField();
        panel.add(nameTextField);

        panel.add(new JLabel("How many " + a.getEquipment().get(0).getName() + " do you want?"));
        panel.add(new JLabel("Max " + a.getEquipment().get(0).getQuantity()));
        equipment1TextField = new JTextField();
        panel.add(equipment1TextField);

        if (a.getEquipment().size() > 1) {
            panel.add(new JLabel("How many " + a.getEquipment().get(1).getName() + " do you want?"));
            panel.add(new JLabel("Max " + a.getEquipment().get(1).getQuantity()));
        
            equipment2TextField = new JTextField();
            equipment2TextField.setBounds(100, 50, 200, 30);
            panel.add(equipment2TextField);
        }
        
    }

    // REQUIRES: a frame to add booking changing reservation
    // MODIFIES: this
    // EFFECTS: allows user to edit given reservation that is not booked
    private void editAvailableMenu(JPanel panel, Timeslot t, Activity a) {
        JButton save = new JButton("Confirm reservation");
        save.setActionCommand("reserve-" + a.getName() + "-" + a.getTimeslots().indexOf(t));
        save.addActionListener(this);
        panel.add(new JLabel("Available"));
        panel.add(new JLabel("Time: " + t.getStart() + "-" + t.getEnd()));
        JLabel equip1 = new JLabel("Equipment available:");
        equip1.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(equip1);
        for (Equipment e : a.getEquipment()) {
            panel.add(new JLabel(e.getQuantity() + " " + e.getName()));
        }

        textInputs(panel, a);
        panel.add(save);
        
    }

    // REQUIRES: a valid activity name
    // EFFECTS: returns activity object based on string
    private Activity setActivity(String activityName) {
        Activity a;
        if (activityName.equals("Swimming")) {
            a = swimming;
        } else if (activityName.equals("Bowling")) {
            a = bowling;
        } else {
            a = tennis;
        }
        return a;
    }

    // EFFECTS: clears screen
    private void clearCanvas() {
        getContentPane().removeAll();
        repaint();
    }

    // EFFECTS: opens the schedule
    public void scheduleViewer() {
        JPanel flex = new JPanel();
        flex.setLayout(new BoxLayout(flex, BoxLayout.Y_AXIS));
        JPanel grid = new JPanel(new GridLayout(1, 3, 10, 10));

        grid.add(activityPaneller(swimming));
        grid.add(activityPaneller(bowling));
        grid.add(activityPaneller(tennis));

        flex.add(grid);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        
        addBottomButtons(buttons);

        JButton exitWithoutSaving = new JButton("Exit without saving");
        exitWithoutSaving.setActionCommand("exitwithoutsaving");
        exitWithoutSaving.addActionListener(this);

        JButton exitWithSaving = new JButton("Save and exit");
        exitWithSaving.setActionCommand("exitwithsaving");
        exitWithSaving.addActionListener(this);

        buttons.add(exitWithoutSaving);
        buttons.add(exitWithSaving);

        flex.add(buttons);
        add(flex);
        pack();
    }

    // REQUIRES: a given panel
    // MODIFIES: this
    // EFFECTS: adds buttons for saving, closing, and filtering
    private void addBottomButtons(JPanel buttons) {
        JButton viewAll = new JButton("View all timeslots");
        viewAll.setActionCommand("view-All");
        viewAll.addActionListener(this);

        JButton filter = new JButton("View only available timeslots");
        filter.setActionCommand("view-Available");
        filter.addActionListener(this);

        JButton filter2 = new JButton("View only booked timeslots");
        filter2.setActionCommand("view-Booked");
        filter2.addActionListener(this);

        if (filterState.equals(FilterState.ALL)) {
            viewAll.setForeground(Color.BLUE);
        } else if (filterState.equals(FilterState.AVAILABLE)) {
            filter.setForeground(Color.BLUE);
        } else {
            filter2.setForeground(Color.BLUE);
        }

        buttons.add(viewAll);

        buttons.add(filter);
        buttons.add(filter2);
    }
    
    // REQUIRES: a given activity
    // EFFECTS: generates timeslot view with filtering
    public JPanel activityPaneller(Activity a) {
        JPanel ret = new JPanel(new GridLayout(13, 1, 10, 10));

        ret.add(titleMaker(a));
        
        for (Timeslot t : a.getTimeslots()) {
            if (filterState.equals(FilterState.ALL)) {
                ret.add(makeCell(t, a));
            } else if (filterState.equals(FilterState.AVAILABLE)) {
                if (!t.isBooked()) {
                    ret.add(makeCell(t, a));
                }
            } else {
                if (t.isBooked()) {
                    ret.add(makeCell(t, a));
                }
            }
           
        }
        return ret;
    }

    // REQUIRES: a timeslot and an activity
    // EFFECTS: returns a cell to be added to the scheduler
    private JPanel makeCell(Timeslot t, Activity a) {
        JPanel cell = new JPanel(new GridLayout(2, 2));
        setCellColor(cell, t.isBooked());

        addBookingInfo(t, a, cell);
        return cell;
    }

    // REQUIRES: a timeslot, activity, and a cell to alter
    // EFFECTS: fills info for given timeslot
    private void addBookingInfo(Timeslot t, Activity a, JPanel cell) {
        JLabel time = new JLabel("Time: " + t.getStart() + "-" + t.getEnd());
        JLabel name = new JLabel("Available");
        JButton bookEditButton = new JButton("");
        if (t.isBooked()) {
            name = new JLabel("Booked by " + t.getName());
            cell.add(name);
            cell.add(time);
            cell.add(equipmentMaker(t.getEquipment(), true));
            bookEditButton = new JButton("Edit/cancel reservation");
        } else {
            cell.add(name);
            cell.add(time);
            cell.add(equipmentMaker(a.getEquipment(), false));
            bookEditButton = new JButton("Make reservation");   
        }
        bookEditButton.setActionCommand(a.getName() + "-" + a.getTimeslots().indexOf(t));
        bookEditButton.addActionListener(this);
        cell.add(bookEditButton);
        
    }

    // REQUIRES: a list of equipment
    // EFFECTS: returns a panel of equipment info
    private JPanel equipmentMaker(ArrayList<Equipment> equipmentList, Boolean booked) {
        JPanel ret = new JPanel(new GridLayout(2, 1));
        setCellColor(ret, booked);
        for (Equipment e : equipmentList) {
            JLabel info = new JLabel("");
            
            if (booked) {
                info = new JLabel("Booked " + e.getQuantity() + " " + e.getName());
            } else {
                info = new JLabel(e.getQuantity() + " " + e.getName() + " available");
            }
            ret.add(info);
        }

        return ret;
    }

    // REQUIRES: a panel and a valid boolean
    // EFFECTS: sets background of panel based on boolean
    private void setCellColor(JPanel panel, Boolean booked) {
        if (booked) {
            panel.setBackground(new Color(173, 75, 91));
        } else {
            panel.setBackground(Color.LIGHT_GRAY);
        }
    } 

    // REQUIRES: an activity with a valid title
    // EFFECTS: returns title panel for an activity
    private JPanel titleMaker(Activity a) {
        JPanel titlePanel = new JPanel(new GridLayout(1, 2));
        JLabel title = new JLabel(a.getName());
        title.setFont(new Font("Arial", Font.BOLD, 15));
        titlePanel.add(title);

        ImageIcon icon = new ImageIcon("data/images/" + a.getName() + ".png");
        Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        titlePanel.add(new JLabel(new ImageIcon(scaled)));

        return titlePanel;
    }


    // MODIFIES: this
    // EFFECTS: processes user input
    private void runApp() {
        boolean run = true;
        String command = null;

        loadOrNew();

        while (run) {
            activitySelection();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                System.out.println("Do you want to save your updated schedule?");
                System.out.println("\ty <- yes, save");
                System.out.println("\tn <- no, exit without saving");
                command = input.next();
                if (command.equals("y")) {
                    saveActivities();
                    run = false;
                } else if (command.equals("n")) {
                    run = false;
                }
            } else {
                processActivityCommand(command);
            }
        }
    }

    // EFFECTS: writes all activity information to a file
    private void saveActivities() {
        ArrayList<Activity> a = new ArrayList<>();
        try {
            jsonWriter.open();
            a.add(swimming);
            a.add(bowling);
            a.add(tennis);
            jsonWriter.write(a);
            jsonWriter.close(); 
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STRING);
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes equipment, activity bookings and timeslots
    private void init() {
        swimming = new Activity("Swimming");
        swimming.createTimeslots(10, 21);
        noodle = new Equipment("pool noodle", 3);
        floatie = new Equipment("floatie", 2);
        swimming.addEquipment(noodle);
        swimming.addEquipment(floatie);

        bowling = new Activity("Bowling");
        bowling.createTimeslots(14, 22);
        ball = new Equipment("bowling ball", 5);
        bowling.addEquipment(ball);

        tennis = new Activity("Tennis");
        tennis.createTimeslots(8, 20);
        racket = new Equipment("tennis racket", 4);
        birdie = new Equipment("birdie", 10);
        tennis.addEquipment(racket);
        tennis.addEquipment(birdie);

        
    }

    // EFFECTS: allows the user to decide if they want to read data or make a new file
    private void loadOrNew() {
        System.out.println("\nDo you want to load the saved schedule or use a new schedule?");
        System.out.println("\tl -> load saved schedule");
        System.out.println("\tn -> use a new schedule");
        String command = input.next();

        if (command.equals("n")) {
            init();
        } else if (command.equals("l")) {
            readActivities();
        }
    }

    // EFFECTS: reads activities using the JSONReader and stores them
    private void readActivities() {
        try {
            ArrayList<Activity> activities = jsonReader.read();
            swimming = activities.get(0);
            bowling = activities.get(1);
            tennis = activities.get(2);
        } catch (IOException e) {
            System.out.println("Error reading saved schedule, using new schedule.");
            init();
        }
    }

    // EFFECTS: displays menu of initial options of activities
    private void activitySelection() {
        System.out.println("\nSelect an activity to register or view times:");
        System.out.println("\ts -> swimming");
        System.out.println("\tb -> bowling");
        System.out.println("\tt -> tennis");
        System.out.println("\tq -> quit");
    }

    // REQUIRES: a valid string command from the user
    // MODIFIES: this
    // EFFECTS: processes activity selection command
    private void processActivityCommand(String cmd) {
        if (cmd.equals("s")) {
            viewingType(swimming);
        } else if (cmd.equals("b")) {
            viewingType(bowling);
        } else if (cmd.equals("t")) {
            viewingType(tennis);
        } else {
            System.out.println("Selection invalid.");
        }
    }

    // REQUIRES: a valid activity with an array of timeslots size > 0
    // EFFECTS: gets user input and displays timeslots
    private void viewingType(Activity a) {
        System.out.println("Which reservations do you want to view? (#)");
        System.out.println("\t1. Available - make a new reservation");
        System.out.println("\t2. Booked - edit or cancel an existing reservation\n");

        int selection = input.nextInt();
        
        if (selection == 1) {

            if (a.getAvailableReservations().isEmpty()) {
                System.out.println("No available reservations... returning to menu");
                return;
            }

            displayAvailableTimeslots(a.getAvailableReservations(), a);
            System.out.println("\nSelect a time to book (#) or enter -1 to return to the menu");
            int bookNum = input.nextInt();
            
            makeBooking(bookNum, a);
        } else if (selection == 2) {
            if (a.getBookedReservations().isEmpty()) {
                System.out.println("No available reservations... returning to menu");
                return;
            }

            displayUnavailableTimeslots(a.getBookedReservations(), a);
            System.out.println("\nSelect a time to edit or cancel (#) or enter -1 to return to the menu");
            int bookNum = input.nextInt();

            editBookingMenu(bookNum, a);
        } 
    }

    // REQUIRES: an index for a timeslot within a.getTimeslots.size() + 1, 
    // a valid activity with an available timeslot size > 0
    // MODIFIES: this
    // EFFECTS: makes a booking at the given time in the given activity 
    // with a name that is entered by the user and equipment entered by the user
    private void makeBooking(int index, Activity a) {
        if (index == -1) { 
            return; 
        }
        index--;
        Timeslot selected = a.getAvailableReservations().get(index);
        System.out.println("What is the name of the booking?");
        String equipmentString = "";
        String name = input.next();
        
        ArrayList<Equipment> equipmentList = new ArrayList<>();
        for (Equipment e : a.getEquipment()) {
            System.out.println("How many " + e.getName() + "s do you want? - max " + e.getQuantity());
            int quant = input.nextInt();
        
            equipmentString = equipmentString + " " + quant + " " + e.getName();
            equipmentList.add(new Equipment(e.getName(), quant));
        }
        a.makeReservation(name, equipmentList, selected);
        System.out.println("Reservation made for " + name + " from " 
                        + selected.getStart() + "-" + selected.getEnd() + "hrs with " + equipmentString);
    
    }

    // REQUIRES: an index for a timeslot within a.getTimeslots.size() + 1,
    // a valid activity with an unavailable timeslot size > 0
    // MODIFIES: this
    // EFFECTS: edits or cancels a booking depending on user input
    private void editBookingMenu(int index, Activity a) {
        if (index == -1) { 
            return; 
        }
        index--;
        Timeslot selected = a.getBookedReservations().get(index);
        System.out.println("Do you want to edit or cancel the booking?");
        System.out.println("\te <- edit");
        System.out.println("\tc <- cancel");
        String selection = input.next();
        if (selection.equals("e")) {
            editBooking(selected, a);
        } else if (selection.equals("c")) {
            selected.cancel();
            System.out.println("Booking cancelled!\n");
        }
    }

    // REQUIRES: a selected timeslot and an activity
    // MODIFIES: this
    // EFFECTS: edits a timeslot based on user input
    private void editBooking(Timeslot selected, Activity a) {
        ArrayList<Equipment> equipList = new ArrayList<>();
        selected.cancel();
        System.out.println("What is the new name of the booking?");
        String name = input.next();
        for (Equipment e : a.getEquipment()) {
            System.out.println("How many " + e.getName()
                                    + "s do you want? - max " + e.getQuantity());
            int quant = input.nextInt();
            equipList.add(new Equipment(e.getName(), quant));
        }
        a.makeReservation(name, equipList, selected);
        System.out.println("Booking updated!\n");   
    }

    // REQUIRES: an array of Timeslots that are available and an activity
    // EFFECTS: prints all available timeslot information from given activity
    private void displayAvailableTimeslots(ArrayList<Timeslot> timeslots, Activity a) {
        int i = 1;
        System.out.println("Available timeslots:");

        for (Timeslot t : timeslots) {
            String time = i + ". " + t.getStart() + "-" + t.getEnd() + "hrs - ";
            String equipmentInfo = " Equipment: ";
            for (Equipment e : a.getEquipment()) {
                equipmentInfo = equipmentInfo + e.getQuantity() + " " + e.getName() + " ";
            }
            i++;
            System.out.println(time + equipmentInfo);
        
        }
    } 

    // REQUIRES: an array of Timeslots that are unavailable and an activity
    // EFFECTS: prints all unavailable timeslot information from given activity
    private void displayUnavailableTimeslots(ArrayList<Timeslot> timeslots, Activity a) {
        int i = 1;
        System.out.println("Booked timeslots:");

        for (Timeslot t : timeslots) {
            
            String time = i + ". " + t.getStart() + "-" + t.getEnd() + "hrs - Bookee: " + t.getName() + " -";
            String equipmentInfo = " Equipment reserved: ";
            for (Equipment e : t.getEquipment()) {
                equipmentInfo = equipmentInfo + e.getQuantity() + " " + e.getName() + " ";
            }
            i++;
            System.out.println(time + equipmentInfo);
        
        }
    }

}
