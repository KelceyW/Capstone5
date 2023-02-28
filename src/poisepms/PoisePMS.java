package poisepms;

import java.util.Scanner;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.sql.*;


/**
 * The main class representing the PoisePMS package
 * 
 * @author Kelcey Webb
 *
 */
public class PoisePMS {
	
	//Constants
	private static final String SELECT = "SELECT * FROM project WHERE PROJ_NUM = ";
	private static final String UPDATED = " PROJECTS UPDATED";
	private static final String WHERE = "' WHERE PROJ_NUM = ";
	private static final String C_ID = "CUST_ID";
	private static final String FNAME = "F_NAME";
	private static final String NAME = "PROJ_NAME";
	private static final String NUM = "PROJ_NUM";
	private static final String CONFIRM = " \n\nY - CONFIRM\nN - BACK";
	private static final String INPUT_REQUIRED = "THIS FIELD IS REQUIRED. PLEASE ENTER A VALID INPUT:";
	private static final String TEL = "TELEPHONE NUMBER:";
	private static final String ADDRESS = "PHYSICAL ADDRESS:";
	private static final String LAST = "LAST NAME:";
	private static final String FIRST = "FIRST NAME:";
	private static final String EMAIL = "EMAIL ADDRESS:";
	
	//Variables
	static Scanner input = new Scanner(System.in);
	static Scanner intInput = new Scanner(System.in);
	static LocalDate today = LocalDate.now();
	
	//Array lists
	static ArrayList<String> projects = new ArrayList<> ();
	static ArrayList<String> completeProjects = new ArrayList<> ();
	static ArrayList<String> allProjects = new ArrayList<>();
	static ArrayList<String> searchList = new ArrayList<>();
	static ArrayList<String> architects = new ArrayList<> ();
	static ArrayList<String> projectManagers = new ArrayList<> ();
	static ArrayList<String> customers = new ArrayList<> ();
	static ArrayList<String> structuralEngs = new ArrayList<> ();
	
	/**
	 * The main method.
	 * Method to implement all the other methods and run the program as a whole
	 * 
	 * @param args The command line arguments
	 */
	public static void main (String [] args) {
		
		System.out.println("WELCOME TO POISED PROJECT MANAGER!");
		Boolean valid = true;
		
		while (Boolean.TRUE.equals(valid)) {
			try (
					Connection connection = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/PoisePMS?useSSL=false",
							"root",
							"Kelcey@2024!!"
							)) {
				Statement statement = connection.createStatement();
			
				System.out.println("""
						
						MAIN MENU:
						
						A - ADD PROJECTS
						E - EDIT PROJECTS
						F - FINALISE PROJECTS
						V - VIEW PROJECTS
						S - SEARCH
						Q - QUIT
						""");
				String userInput = input.nextLine().toUpperCase();
				
				if(userInput.isEmpty()) {
					System.out.println(INPUT_REQUIRED);
				}
				
				if(userInput.equals("Q")) {
					break;
				}
				
				if(userInput.equals("A")) {
					createLists(statement);
					getInfo(statement);
				}

				if(userInput.equals("V")) { 
					createLists(statement);
					viewProjectList(statement);
				}
				
				if(userInput.equals("E")) {
					createLists(statement);
					edit(statement);
				}
						
				if(userInput.equals("S")) {
					createLists(statement);
					search(statement);
				}
				
				if(userInput.equals("F")) {
					createLists(statement);
					finaliseProject(statement);
				} 
				statement.close();
				
			} catch (Exception error) {
				System.out.println("ERROR!");
			} 
		} 
	} 
	
	
	/**
	 * Method to create relevant lists from the database.
	 * <br>
	 * @param statement The direct line to the database.
	 * @throws SQLException
	 */
	public static void createLists(Statement statement) throws SQLException {
		
		projects.clear();
		architects.clear();
		customers.clear();
		projectManagers.clear();
		structuralEngs.clear();
		ResultSet results;
		
		String createProj = "SELECT * FROM project";
		results = statement.executeQuery(createProj);
		while (results.next()) {
			projects.add(results.getInt(NUM) + " - "
						+ results.getString(NAME) + "\n");
		}
		
		String createArch = "SELECT * FROM architect";
		results = statement.executeQuery(createArch);
		while (results.next()) {
			architects.add(results.getInt("ARCH_ID") + " - "
							+ results.getString(FNAME));
		}
		
		String createMan = "SELECT * FROM projectManager";
		results = statement.executeQuery(createMan);
		while (results.next()) {
			projectManagers.add(results.getInt("MAN_ID") + " - "
							+ results.getString(FNAME));
		}
		
		String createCust = "SELECT * FROM customer";
		results = statement.executeQuery(createCust);
		while (results.next()) {
			customers.add(results.getInt(C_ID) + " - "
							+ results.getString(FNAME));
		}
		
		String createEng = "SELECT * FROM structuralEngineer";
		results = statement.executeQuery(createEng);
		while (results.next()) {
			structuralEngs.add(results.getInt("ENG_ID") + " - "
							+ results.getString(FNAME));
		}
		
		String createComp = "SELECT * FROM project WHERE COMPLETE != 'No'";
		results = statement.executeQuery(createComp);
		while (results.next()) {
			completeProjects.add(results.getInt(NUM) + " - "
						+ results.getString(NAME) + "\n");
		}
	}
	
		
	/**
	 * Method to get information from a user,
	 * and save the information to a specified database.
	 * <br>
	 * Certain inputs have to be in specific data types or they will be rejected.
	 * @param statement The direct line to the database
	 * @throws SQLException
	 */
	public static void getInfo(Statement statement) throws SQLException {
		
		StringBuilder sbProject = new StringBuilder();
		StringBuilder sbAddress = new StringBuilder();
		StringBuilder sbArch = new StringBuilder();
		StringBuilder sbProjMan = new StringBuilder();
		StringBuilder sbCust = new StringBuilder();
		StringBuilder sbEng = new StringBuilder();
			
		System.out.println("PLEASE ENTER THE PROJECT DETAILS BELOW:\n");
		
		int projectNumber = projects.size() + 101;
			
		System.out.println("PLEASE ENTER THE PROJECT NAME:");
		String projectName = input.nextLine();
			
		System.out.println("PLEASE ENTER THE BUILDING TYPE:");
		String buildingType = input.nextLine();
		buildingType = emptyCheck(buildingType);

		System.out.println("PLEASE ENTER THE " + ADDRESS + ":");
		String physicalAddress = input.nextLine();
		physicalAddress = emptyCheck(physicalAddress);

		System.out.println("PLEASE ENTER THE ERF NUMBER:");
		String num = input.nextLine();
		num = intCheck(num);
		int erfNum = Integer.parseInt(num);
		
		System.out.println("PLEASE ENTER THE DEADLINE (YYYY-MM-DD):");
		String date = input.nextLine();
		date = dateCheck(date);
		LocalDate deadline = LocalDate.parse(date);

		System.out.println("PLEASE ENTER THE TOTAL FEE CHARGED:");
		String fee = input.nextLine();
		fee = doubleCheck(fee);
		double totalFee = Double.parseDouble(fee);

		System.out.println("PLEASE ENTER THE TOTAL AMOUNT PAID TO DATE:");
		String paid = input.nextLine();
		paid = doubleCheck(paid);
		double totalPaid = Double.parseDouble(paid);
			
		System.out.println("\nPLEASE ENTER THE ARCHITECT'S DETAILS BELOW:");
			
		int archID = architects.size() + 1001;
			
		System.out.println(FIRST);
		String arcFirstName = input.nextLine();
		arcFirstName = emptyCheck(arcFirstName);
			  	
		System.out.println(LAST);
		String arcLastName = input.nextLine();
		arcLastName = emptyCheck(arcLastName);
			  
		System.out.println(TEL);
		String arcTelNum = input.nextLine();
		arcTelNum = emptyCheck(arcTelNum);
			  
		System.out.print(EMAIL);
		String arcEmail = input.nextLine();
		arcEmail = emptyCheck(arcEmail);
			  
		System.out.print(ADDRESS);
		String arcAdd = input.nextLine();
		arcAdd = emptyCheck(arcAdd);
			
		System.out.println("\nPLEASE ENTER THE PROJECT MANAGER'S DETAILS BELOW:");
			
		int projManID = projectManagers.size() + 2001;
			
		System.out.println(FIRST);
		String projFirstName = input.nextLine();
		projFirstName = emptyCheck(projFirstName);
		  	
		System.out.println(LAST);
		String projLastName = input.nextLine();
		projLastName = emptyCheck(projLastName);
		  
		System.out.println(TEL);
		String projTelNum = input.nextLine();
		projTelNum = emptyCheck(projTelNum);
		 
		System.out.print(EMAIL);
		String projEmail = input.nextLine();
		projEmail = emptyCheck(projEmail);
		  
		System.out.print(ADDRESS);
		String projAddress = input.nextLine();
		projAddress = emptyCheck(projAddress);
			
		System.out.println("\nPLEASE ENTER THE CUSTOMERS'S DETAILS BELOW:");
			
		int custID = customers.size() + 3001;
			
		System.out.println(FIRST);
		String custFirstName = input.nextLine();
		custFirstName = emptyCheck(custFirstName);
			  	
		System.out.println(LAST);
		String custLastName = input.nextLine();
		custLastName = emptyCheck(custLastName);
			  
		System.out.println(TEL);
		String custTelNum = input.nextLine();
		custTelNum = emptyCheck(custTelNum);
			  
		System.out.print(EMAIL);
		String custEmail = input.nextLine();
		custEmail = emptyCheck(custEmail);
			  
		System.out.print(ADDRESS);
		String custAddress = input.nextLine();
		custAddress = emptyCheck(custAddress);
			
		System.out.println("\nPLEASE ENTER THE STRUCTURAL ENGINEER'S DETAILS BELOW:");
			
		int engID = structuralEngs.size() + 4001;
			
		System.out.println(FIRST);
		String engFirstName = input.nextLine();
		engFirstName = emptyCheck(engFirstName);
			  	
		System.out.println(LAST);
		String engLastName = input.nextLine();
		engLastName = emptyCheck(engLastName);
			  
		System.out.println(TEL);
		String engTelNum = input.nextLine();
		engTelNum = emptyCheck(engTelNum);
			  
		System.out.print(EMAIL);
		String engEmail = input.nextLine();
		engEmail = emptyCheck(engEmail);
			  
		System.out.print(ADDRESS);
		String engAddress = input.nextLine();
		engAddress = emptyCheck(engAddress);
			
		String complete = "No";
							
		if (projectName.isEmpty()) {
			projectName = buildingType + " " + custLastName;
		}
		
		sbProject.append("INSERT INTO project VALUES (" + projectNumber + ", '" +
					projectName + "', '" + buildingType + "', " + erfNum + ", "
					+ totalFee + ", " + totalPaid + ", '" + deadline + "', '" +
					complete + "', " + archID + ", " + projManID + ", " + custID + ", " + engID + ")");
		String addProj = sbProject.toString();
			
		sbAddress.append("INSERT INTO address VALUES (" + erfNum + ", '" + physicalAddress + "')" );
		String addAddress = sbAddress.toString();
			
		sbArch.append("INSERT INTO architect VALUES (" + archID + ", '" + arcFirstName + "', '" +
				arcLastName + "', '" + arcTelNum + "', '" + arcEmail + "', '" + arcAdd + "')");
		String addArch =  sbArch.toString();
			
		sbProjMan.append("INSERT INTO projectManager VALUES (" + projManID + ", '" + projFirstName
				+ "', '" + projLastName + "', '" + projTelNum + "', '" + projEmail + "', '" + 
				projAddress + "')");
		String addMan = sbProjMan.toString();
			
		sbCust.append("INSERT INTO customer VALUES (" + custID + ", '" + custFirstName + "', '" +
				custLastName + "', '" + custTelNum + "', '" + custEmail + "', '" + custAddress + "')");
		String addCust = sbCust.toString();
			
		sbEng.append("INSERT INTO structuralEngineer VALUES (" + engID + ", '" + engFirstName + "', '" +
				engLastName + "', '" + engTelNum + "', '" + engEmail + "', '" + engAddress + "')");
		String addEng = sbEng.toString();
		
		System.out.println("\nCONFIRM ADD NEW PROJECT " + projectName + CONFIRM);
		String confirmAdd = input.nextLine().toUpperCase();
		
		if (confirmAdd.equals("Y")) {
			int addArchitect = statement.executeUpdate(addArch);
			int addAdd = statement.executeUpdate(addAddress);
			int addManager = statement.executeUpdate(addMan);
			int addCustomer = statement.executeUpdate(addCust);
			int addProject = statement.executeUpdate(addProj);
			int addEngineer = statement.executeUpdate(addEng);
			
			if( addArchitect + addAdd + addManager + addCustomer + addProject + addEngineer == 6) {
				System.out.println(addProject + " PROJECT ADDED SUCCESSFULLY");
			}
		}
	}
		
		
	/**
	 * Method to specify which details of a Project to change.
	 * <br>
	 * This method allows the user to choose which Project and which details
	 * of the Project they would like to edit
	 * @param statement The direct line to the database
	 * @throws SQLException
	 * @see projectEditor
	 * 
	 */
	public static void edit(Statement statement) throws SQLException {
		
		if(projects.isEmpty()) {
			System.out.println("THERE ARE NO PROJECTS AVAILABLE TO EDIT, PLEASE ADD A PROJECT FIRST");
			
		} else {
			System.out.println("""
				PLEASE ENTER THE NUMBER OF THE PROJECT YOU WOULD LIKE TO EDIT:
				
				"""+ printArrayList(projects));
		
		int editNum = intInput.nextInt();
		projectEditor(statement, editNum);
		}
	}
		
		
	/**
	 * Method to change the details of a Project,
	 * specifying the number of the project to be changed.
	 * <br>
	 * This method changes the details of a specified Project and replaces the new details in the 
	 * database.
	 * @param statement The direct line to the database
	 * @param num The number of the Project Object which needs to be edited
	 * @throws SQLException
	 * @see edit()
	 * 
	 */
	public static void projectEditor(Statement statement, int num) throws SQLException {
		
		System.out.println("""
				1 - EDIT BUILDING TYPE
				2 - EDIT DEADLINE
				3 - EDIT TOTAL FEE
				4 - EDIT TOTAL PAID
				""");
		String editProj = input.nextLine();
				
		if(editProj.equals("1")) {
			System.out.println("PLEASE ENTER THE NEW BUILDING TYPE:");
			String newType = input.nextLine();
			newType = emptyCheck(newType);
			
			String editType = "UPDATE project SET BUILD_TYPE = '" + newType +
					WHERE + num;
			int type = statement.executeUpdate(editType);
			System.out.println(type + UPDATED);
		}
				
		if(editProj.equals("2")) {
			System.out.println("PLEASE ENTER THE NEW DEALINE(YYYY-MM-DD):");
			String newProjDeadline = input.nextLine();
			newProjDeadline = dateCheck(newProjDeadline);
			
			String editDeadline = "UPDATE project SET DEADLINE = '" + newProjDeadline +
					WHERE + num;
			int deadline = statement.executeUpdate(editDeadline);
			System.out.println(deadline + UPDATED);
		}
		
		if(editProj.equals("3")) {
			System.out.println("PLEASE ENTER THE NEW TOTAL FEE:");
			String newProjFee = input.nextLine();
			newProjFee = doubleCheck(newProjFee);
			
			String editFee = "UPDATE project SET TOTAL_FEE = '" + newProjFee +
					WHERE + num;
			int fee = statement.executeUpdate(editFee);
			System.out.println(fee + UPDATED);
		}
		
		if(editProj.equals("4")) {
			System.out.println("PLEASE ENTER THE NEW TOTAL AMOUNT PAID:");
			String newProjPaid = input.nextLine();
			newProjPaid = doubleCheck(newProjPaid);
			
			String editPaid = "UPDATE project SET TOTAL_PAID = '" + newProjPaid +
					WHERE + num;
			int paid = statement.executeUpdate(editPaid);
			System.out.println(paid + UPDATED);
		}
	}
	
	
	/**
	 * Method to finalise a Project.
	 * <br>
	 * This method adds a completion date to a specified Project and replaces the new details in the 
	 * database
	 * @param statement The direct line to the database
	 * @throws SQLException
	 * @see createInvoice
	 */
	public static void finaliseProject(Statement statement) throws SQLException {
		
		if(projects.isEmpty()) {
			System.out.println("THERE ARE NO PROJECTS AVAILABLE TO FINALISE, PLEASE ADD A PROJECT FIRST");
			
		} else {
			System.out.println("""
			PLEASE ENTER THE NUMBER OF THE PROJECT YOU WOULD LIKE TO FINALISE:
				
			""" + printArrayList(projects));
			int finNum = intInput.nextInt();
			
			System.out.println("\nPLEASE ENTER THE COMPLETION DATE OF THE PROJECT:(YYYY-MM-DD):");
			String compDate = input.nextLine();
			int invoiceNum = completeProjects.size() +101;
			String invoice = "Invoice 00" + invoiceNum;
			int custID = 0;
			String cust = "";
			
			System.out.println("""
				CONFIRM CHANGE:
				FINALISE PROJECT""" + " " + finNum + CONFIRM
				);
			String finChoice = input.nextLine().toUpperCase();
		
			if (finChoice.equals("Y")) {
				String finalise = "UPDATE project SET COMPLETE = 'Yes " + compDate 
							+ WHERE + finNum;
				int finalChange = statement.executeUpdate(finalise);
				System.out.println(finalChange + " PROJECT FINALISED");	
				
				String findCust = SELECT + finNum;
				ResultSet results = statement.executeQuery(findCust);
				while (results.next()) {
					custID = results.getInt(C_ID);
				}
				
				StringBuilder stringBuilder = new StringBuilder();
				String customer = "SELECT * FROM customer WHERE CUST_ID = " + custID;
				results = statement.executeQuery(customer);
				while (results.next()) {
					stringBuilder.append (cust + "Customer Name:\t\t\t\t" + results.getString(FNAME)
					+ results.getString("L_NAME") + "\nCustomer Telephone number:\t\t"
					+ results.getString("TEL_NUM") + "\nCutomer Email Address:\t\t\t"
					+ results.getString("EMAIL_ADD") + "\nCustomer Address:\t\t\t"
					+ results.getString("ADDRESS"));
					cust = stringBuilder.toString();
				}
				
				String balance = SELECT + finNum;
				results = statement.executeQuery(balance);
				while (results.next() ) {
					double balanceDue = results.getDouble("TOTAL_FEE") - results.getDouble("TOTAL_PAID");
					createInvoice(invoice, balanceDue, cust);
				}
			}
		}
	}
			
	
	/**
	 * Method to create a Tax Invoice in the form of text file,
	 * specifying the name of the invoice, the balance due and the customer.
	 * <br>
	 * This method creates a text file with an invoice number, which includes the customer's details
	 * and amount payable 
	 * 
	 * @param name The invoice number 
	 * @param balance The amount payable
	 * @param customer The customer's details 
	 * 
	 * @see finaliseProject()
	 */
	public static void createInvoice(String name, double balance, String customer) {
		
		if (balance > 0) {
			
			System.out.println ("THERE IS AN OUTSTANDING BALANCE ON THIS PROJECT OF R" + balance);
			try (FileWriter fileWriter = new FileWriter(name + ".txt", true);
					Formatter formatter = new Formatter(fileWriter);) {
				formatter.format("%s", "\t\t\tTAX INVOICE\n\n");
				formatter.format("%s", "DATE:\t" + today + "\t\t\tINVOICE NUMBER:\t" + name + "\n\n");
				formatter.format("%s", "INVOICE FROM:\nPOSIED PROJECT MANAGEMENT\n\nINVOICE TO:\n");
				formatter.format("%s", customer +"\n\n\nAMOUNT PAYABLE:\t\t\t\tR" + balance);
				System.out.println (name + " HAS BEEN GENERATED");
				
				} catch (Exception error) {
					System.out.println ("ERROR! FAILED TO CREATE INVOICE");
				}
			}
		}
	
		
	/**
	 * Method to view a list of Projects,
	 * according to specified conditions.
	 * <br>
	 * This method allows the user to choose which list they would like to view
	 * @param statement The direct line to the database
	 * @throws SQLException
	 */
	public static void viewProjectList(Statement statement) throws SQLException {
		
		if(projects.isEmpty()) {
			System.out.println("THERE ARE NO PROJECTS AVAILABLE TO VIEW, PLEASE ADD A PROJECT FIRST");
			
		} else {
			System.out.println("""
				
				1 - VIEW ALL PROJECTS
				2 - VIEW COMPLETED PROJECTS
				3 - VIEW INCOMPLETE PROJECTS
				4 - VIEW OVERDUE PROJECTS
				
				""");
			String viewChoice = input.nextLine().toUpperCase();
			String viewAll = "SELECT * FROM project";
			String viewComplete = "SELECT * FROM project WHERE COMPLETE != 'No'";
			String viewIncomplete = "SELECT * FROM project WHERE COMPLETE = 'No'";
			String viewOverdue = "SELECT * FROM project WHERE COMPLETE = 'No' and DEADLINE < '" + today + "'";
			ResultSet results = null;
			
			if(viewChoice.equals("1")) {
				results = statement.executeQuery(viewAll);
			}
				
			if(viewChoice.equals("2")) {
				results = statement.executeQuery(viewComplete);
			}
			
			if(viewChoice.equals("3")) {
				results = statement.executeQuery(viewIncomplete);
			}
			
			if(viewChoice.equals("4")) {
				results = statement.executeQuery(viewOverdue);	
			}
			
			while (results.next()) {
				System.out.println(
						"PROJECT " +
						results.getInt(NUM) + " " +
						results.getString(NAME));
			}
			viewFullProject(statement);
		}
	}
	
				
	/**
	 * Method to view all the details of a specified Project.
	 * <br>
	 * This method allows the user to choose a Project number and view all
	 * the details for that Project
	 * @param statement The direct line to the database
	 * @throws SQLException 
	 */
	public static void viewFullProject(Statement statement) throws SQLException {
		
		System.out.println("""
				
				ENTER THE PROJECT NUMBER TO VIEW THE FULL PROJECT
				ENTER M TO RETURN TO THE MAIN MENU:
				""");
		String view = input.nextLine().toUpperCase();
		
		if (!"M".equals(view)) {
			String updateView = SELECT + view;
			ResultSet results = statement.executeQuery(updateView);
			while (results.next()) {
				System.out.println(
						"PROJECT NUMBER:\t\t" + results.getInt(NUM) + 
						"\nPROJECT NAME:\t\t" + results.getString(NAME) +
						"\nBUILDING TYPE:\t\t" + results.getString("BUILD_TYPE") +
						"\nERF NUMBER:\t\t" + results.getInt("ERF_NUM") +
						"\nTOTAL FEE:\t\t" + results.getDouble("TOTAL_FEE") +
						"\nTOTAL PAID:\t\t" + results.getDouble("TOTAL_PAID") +
						"\nDEADLINE:\t\t" + results.getDate("DEADLINE") +
						"\nCOMPLETE:\t\t" + results.getString("COMPLETE") +
						"\nARCHITECT:\t\t" + results.getInt("ARCH_ID") +
						"\nPROJECT MANAGER:\t" + results.getInt("MAN_ID") +
						"\nCUSTOMER:\t\t" + results.getInt(C_ID) +
						"\nSTRUCTURAL ENG:\t\t" + results.getInt("ENG_ID")
						);
			}
		}
	}

		
	/**
	 * Method to find a specified Project.
	 * <br>
	 * This method allows the user to enter either a project number,
	 * or name and see a list of all the projects with that number or name
	 * @param statement - The direct line to the database
	 * @throws SQLException
	 */
	public static void search(Statement statement) throws SQLException {
		
		if(projects.isEmpty()) {
			System.out.println("THERE ARE NO PROJECTS AVAILABLE TO SEARCH, PLEASE ADD A PROJECT FIRST");
			
		} else {
			System.out.println("""
				1) SEARCH BY PROJECT NAME
				2) SEARCH BY PROJECT NUMBER
				""");
			String searchChoice = input.nextLine().toUpperCase();
			
			if(searchChoice.equals("1")) {
				System.out.println("PLEASE ENTER A PROJECT NAME");
				String searchInput = input.nextLine();
				String search = "SELECT * FROM project WHERE PROJ_NAME = '" + searchInput + "'";

				ResultSet results = statement.executeQuery(search);
				while (results.next()) {
					System.out.println(
							"PROJECT: " + results.getInt(NUM)
							+ " - " + results.getString(NAME));
					viewFullProject(statement);
				}
			}
			
			if(searchChoice.equals("2")) {
				System.out.println("PLEASE ENTER A PROJECT NUMBER");
				String searchInput = input.nextLine();
				String searchNum = SELECT + searchInput;
					
				ResultSet results = statement.executeQuery(searchNum);
				while (results.next()) {
					System.out.println(
							"PROJECT: " + results.getInt(NUM)
							+ " - " + results.getString(NAME));
					viewFullProject(statement);
					}
				}
			}
		}
	

	/**
	 * Method to check if a specified input is in the correct Integer format,
	 * specifying a String to be checked.
	 * <br>
	 * @param num The input to be checked
	 * @return True or False
	 */
	public static boolean isInt(String num) {
		
		try {
			Integer.parseInt(num);
	        return true;
	        
		} catch (NumberFormatException error) {
			return false;
		}
	}

	/**
	 * Method to check if a specified input is in the correct double format,
	 * specifying a string to be checked
	 * <br>
	 * @param num The input to be checked 
	 * @return True or False
	 */
	public static boolean isDouble(String num) {
		
		try {
			Double.parseDouble(num);
			return true;
			
		} catch (NumberFormatException error) {
			return false;
		}
	}
	
	/**
	 * Method to check if a specified input is in the correct date format,
	 * specifying a string to be checked.
	 * <br>
	 * @param date The input to be checked
	 * @return True or False
	 */
	public static boolean isDate(String date) {
		
		try {
			LocalDate.parse(date);
			return true;
			
		} catch (DateTimeParseException error) {
			return false;
		}
	}
	
	/**
	 * Method to print an ArrayList to the screen in a specified format,
	 * specifying the ArrayList to be printed.
	 * <br>
	 * @param arrayList The ArrayList to be printed
	 * @return The ArrayList in the specified format
	 */
	public static String printArrayList (List<String> arrayList) {
		
		String formattedString = arrayList.toString();
		formattedString = formattedString.replace(", \n", "\n");
		formattedString = formattedString.replace("[", "");
		formattedString = formattedString.replace("]", "");
		formattedString = formattedString.replace(", ", "");
		formattedString = formattedString.trim();
		
		return formattedString;
	}
	
	/**
	 * Method to continuously request a new input, while an input is empty
	 * specifying a string to be checked
	 * <br>
	 * @param variable The input to be checked
	 * @return The input when is it in the correct format (not empty) 
	 */
	public static String emptyCheck (String variable) {
		
		while (variable.isEmpty()) {
			System.out.println(INPUT_REQUIRED);
			variable = input.nextLine();
		}
		return variable;
	}
	
	/**
	 * Method to continuously request a new input, while an input
	 * is not in the correct Integer format, specifying a string to be checked
	 *<br>
	 * @param num The input to be checked
	 * @see isInt
	 * @return The input when it is in the correct format
	 */
	public static String intCheck (String num) {
		
		while (!isInt(num)) {  
			System.out.println(INPUT_REQUIRED);
			num = input.nextLine();
		}
		
		return num;
	}
	
	/**
	 * Method to continuously request a new input, while an input 
	 * is not in the correct double format, specifying a string to be checked
	 * <br>
	 * @param num The input to be checked
	 * @see isDouble
	 * @return The input when it is in the correct format 
	 */
	public static String doubleCheck (String num) {
		
		while (!isDouble(num)) {
			System.out.println(INPUT_REQUIRED);
			num = input.nextLine();
		}
		
		return num;
	}
	
	/**
	 * Method to continuously request a new input, while an input 
	 * is not in the correct date format, specifying a string to be checked
	 * <br>
	 * @param date The input to be checked
	 * @see isDate
	 * @return The input when it is in the correct format 
	 */
	public static String dateCheck (String date) {
		
		while (!isDate(date)) {
			System.out.println(INPUT_REQUIRED);
			date = input.nextLine();
		}
		
		return date;
	}
}
	
	


