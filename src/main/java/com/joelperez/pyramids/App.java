package com.joelperez.pyramids;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.json.simple.*;

/*
 * Nassef's Egyptian Pyramids App
 * CIS-055 Data Structures, Joel Perez
 */
public class App {

  // arrays give O(1) reads of the pharaohs and pyramids
  protected Pharaoh[] pharaohArray;
  protected Pyramid[] pyramidArray;

  // hash maps give O(1) lookup by id and by hieroglyphic
  protected HashMap<Integer, Pharaoh> pharaohById = new HashMap<Integer, Pharaoh>();
  protected HashMap<String, Pharaoh> pharaohByHieroglyphic = new HashMap<String, Pharaoh>();
  protected HashMap<Integer, Pyramid> pyramidById = new HashMap<Integer, Pyramid>();

  // a set records which pyramids were requested, without duplicates
  protected Set<Integer> requestedPyramids = new HashSet<Integer>();

  public static void main(String[] args) {
    // create and start the app
    App app = new App();
    app.start();
  }

  // main loop for app
  public void start() {
    Scanner scan = new Scanner(System.in);
    Character command = '_';

    // loop until user quits
    while (command != 'q') {
      printMenu();
      System.out.print("Enter a command: ");
      command = menuGetCommand(scan);

      executeCommand(scan, command);
    }

    scan.close();
  }

  // constructor to initialize the app and read the json data
  public App() {
    // read egyptian pharaohs
    String pharaohFile = "src/main/resources/pharaoh.json";
    JSONArray pharaohJSONArray = JSONFile.readArray(pharaohFile);

    // create and initialize the pharaoh array and maps
    initializePharaoh(pharaohJSONArray);

    // read pyramids
    String pyramidFile = "src/main/resources/pyramid.json";
    JSONArray pyramidJSONArray = JSONFile.readArray(pyramidFile);

    // create and initialize the pyramid array and map
    initializePyramid(pyramidJSONArray);
  }

  // initialize the pharaoh array and lookup maps
  private void initializePharaoh(JSONArray pharaohJSONArray) {
    pharaohArray = new Pharaoh[pharaohJSONArray.size()];

    for (int i = 0; i < pharaohJSONArray.size(); i++) {
      // get the object
      JSONObject o = (JSONObject) pharaohJSONArray.get(i);

      // parse the json object
      Integer id = toInteger(o, "id");
      String name = o.get("name").toString();
      Integer begin = toInteger(o, "begin");
      Integer end = toInteger(o, "end");
      Integer contribution = toInteger(o, "contribution");
      String hieroglyphic = o.get("hieroglyphic").toString();

      // add a new pharaoh to the array and both maps
      Pharaoh p = new Pharaoh(id, name, begin, end, contribution, hieroglyphic);
      pharaohArray[i] = p;
      pharaohById.put(id, p);
      pharaohByHieroglyphic.put(hieroglyphic, p);
    }
  }

  // initialize the pyramid array and lookup map
  private void initializePyramid(JSONArray pyramidJSONArray) {
    pyramidArray = new Pyramid[pyramidJSONArray.size()];

    for (int i = 0; i < pyramidJSONArray.size(); i++) {
      // get the object
      JSONObject o = (JSONObject) pyramidJSONArray.get(i);

      // parse the json object
      Integer id = toInteger(o, "id");
      String name = o.get("name").toString();
      JSONArray contributorsJSONArray = (JSONArray) o.get("contributors");
      String[] contributors = new String[contributorsJSONArray.size()];
      for (int j = 0; j < contributorsJSONArray.size(); j++) {
        String c = contributorsJSONArray.get(j).toString();
        contributors[j] = c;
      }

      // add a new pyramid to the array and map
      Pyramid p = new Pyramid(id, name, contributors);
      pyramidArray[i] = p;
      pyramidById.put(id, p);
    }
  }

  // get an integer from a json object, and parse it
  private Integer toInteger(JSONObject o, String key) {
    String s = o.get(key).toString();
    Integer result = Integer.parseInt(s);
    return result;
  }

  // get first character from input
  private static Character menuGetCommand(Scanner scan) {
    Character command = '_';

    String rawInput = scan.nextLine();

    if (rawInput.length() > 0) {
      rawInput = rawInput.toLowerCase();
      command = rawInput.charAt(0);
    }

    return command;
  }

  // ask the user for an id number and validate it
  private Integer readId(Scanner scan, String what) {
    System.out.printf("Enter a %s id: ", what);
    String rawInput = scan.nextLine();

    try {
      return Integer.parseInt(rawInput.trim());
    } catch (NumberFormatException e) {
      System.out.printf("ERROR: '%s' is not a valid id number\n", rawInput);
      return null;
    }
  }

  //
  // commands
  //

  // command 1: print all pharaohs
  private void printAllPharaoh() {
    for (int i = 0; i < pharaohArray.length; i++) {
      printMenuLine();
      pharaohArray[i].print();
      printMenuLine();
    }
  }

  // command 2: print a specific pharaoh by id
  private void printPharaoh(Scanner scan) {
    Integer id = readId(scan, "pharaoh");
    if (id == null) {
      return;
    }

    // O(1) lookup in the hash map
    Pharaoh p = pharaohById.get(id);
    if (p == null) {
      System.out.printf("ERROR: There is no pharaoh with id %d\n", id);
      return;
    }

    printMenuLine();
    p.print();
    printMenuLine();
  }

  // print one pyramid with its contributors and their total contribution
  private void printPyramid(Pyramid pyramid) {
    printMenuLine();
    System.out.printf("Pyramid %s\n", pyramid.name);
    System.out.printf("\tid: %d\n", pyramid.id);

    Integer total = 0;
    for (int i = 0; i < pyramid.contributors.length; i++) {
      // O(1) lookup of each contributor by hieroglyphic
      Pharaoh p = pharaohByHieroglyphic.get(pyramid.contributors[i]);
      if (p == null) {
        System.out.printf("\tcontributor %d: unknown hieroglyphic\n", i + 1);
        continue;
      }
      System.out.printf("\tcontributor %d: %s %d gold coins\n", i + 1, p.name, p.contribution);
      total = total + p.contribution;
    }
    System.out.printf("\ttotal contribution: %d gold coins\n", total);
    printMenuLine();
  }

  // command 3: print all pyramids and their contributors
  private void printAllPyramid() {
    for (int i = 0; i < pyramidArray.length; i++) {
      printPyramid(pyramidArray[i]);
    }
  }

  // command 4: print a specific pyramid by id, and remember the request
  private void printPyramidById(Scanner scan) {
    Integer id = readId(scan, "pyramid");
    if (id == null) {
      return;
    }

    Pyramid pyramid = pyramidById.get(id);
    if (pyramid == null) {
      System.out.printf("ERROR: There is no pyramid with id %d\n", id);
      return;
    }

    // the set keeps each requested pyramid only once, no duplicates
    requestedPyramids.add(id);

    printPyramid(pyramid);
  }

  // command 5: report the requested pyramids, each printed only once
  private void printRequestedPyramids() {
    if (requestedPyramids.isEmpty()) {
      System.out.println("No pyramids have been requested yet");
      return;
    }

    System.out.println("Requested pyramids (no duplicates):");
    for (Integer id : requestedPyramids) {
      printPyramid(pyramidById.get(id));
    }
  }

  private Boolean executeCommand(Scanner scan, Character command) {
    Boolean success = true;

    switch (command) {
      case '1':
        printAllPharaoh();
        break;
      case '2':
        printPharaoh(scan);
        break;
      case '3':
        printAllPyramid();
        break;
      case '4':
        printPyramidById(scan);
        break;
      case '5':
        printRequestedPyramids();
        break;
      case 'q':
        System.out.println("Thank you for using Nassef's Egyptian Pyramid App!");
        break;
      default:
        System.out.println("ERROR: Unknown command");
        success = false;
    }

    return success;
  }

  private static void printMenuCommand(Character command, String desc) {
    System.out.printf("%s\t\t%s\n", command, desc);
  }

  private static void printMenuLine() {
    System.out.println(
      "--------------------------------------------------------------------------"
    );
  }

  // prints the menu
  public static void printMenu() {
    printMenuLine();
    System.out.println("Nassef's Egyptian Pyramids App");
    printMenuLine();
    System.out.printf("Command\t\tDescription\n");
    System.out.printf("-------\t\t---------------------------------------\n");
    printMenuCommand('1', "List all the pharoahs");
    printMenuCommand('2', "Displays a specific Egyptian pharaoh");
    printMenuCommand('3', "List all the pyramids");
    printMenuCommand('4', "Displays a specific pyramid");
    printMenuCommand('5', "Displays a list of requested pyramids.");
    printMenuCommand('q', "Quit");
    printMenuLine();
  }
}
