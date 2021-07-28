package application;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

public class partTwo extends Application {
    
    
    //Database information to connect to database
  	static final String DBINFO = "jdbc:oracle:thin:@artemis.vsnet.gmu.edu:1521/vse18c.vsnet.gmu.edu";
  	static final String DBUSERNAME = "mnousain";
  	static final String DBPASSW = "leebupho";
  	
  	private static Connection conn = null;
  	private boolean statusOfConn;
    private boolean dbConn = false;
    
    //initiates connection to the oracleDriver
  	public boolean initiateConnection(String dbinfo, String uname, String pword)
    {
		
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch(ClassNotFoundException e)
		{
			return false;
		}
		
		try 
		{
			conn = DriverManager.getConnection(dbinfo, uname, pword);
		}
		catch(SQLException e)
		{
			return false;
			
		}
		
		return true;
		
    }
  	
    public void closeConnection() 
    {
        if(conn != null) 
        {
            try 
            {
                conn.close();
                System.out.println("Connection closed.");
            } 
            catch (SQLException e) 
            {
                System.err.println("Error: unable to close connection.");
            }
        }
    }
    
    //checks if the ssn given is a ssn of a manager
    public boolean checkManagerSsn(int ssn) {
       
        String sql;
        Statement query;
        ResultSet mgrssn;

        try {
            query = conn.createStatement();
            sql = "select mgrssn from department";
            mgrssn = query.executeQuery(sql);

            while(mgrssn.next()) 
            {
                if(mgrssn.getInt("Mgrssn") == ssn) 
                {
                    return true;
                }
            }

            query.close();
            mgrssn.close();
           
            return false;
        } 
        catch (SQLException e) 
        {
            System.err.println("Error: Manager SSN not found");
            return false;
        }
    }
    
    //inserts new employee into the database
    public void insertEmployee(String fname, String minit, String lname, String ssn, String bdate, String addr, String sex, String salary, String superssn, String dno) 
    {
		try 
		{
		Statement stmt = conn.createStatement();
		
		String sql = "insert into employee values ('" + fname + "', '" + minit + "', '" + lname + "', " +
		         ssn + ", '" + bdate + "', '" + addr + "', '" + sex + "', " + salary + ", " +
		         superssn + ", " + dno + ", ' ')";
		
		stmt.executeUpdate(sql);
		} 
		catch (SQLException e) 
		{
		System.err.println("Error: Unable to add employee " + "'" + fname + " " + lname + "'");
		System.exit(1);
		}

    }
    
    //uses arrayList to retrieve project information for employee with the ssn passed through
    public ArrayList<String> retrieveProjects(String ssn) {
        
        String sql      = null;
        Statement query   = null;
        ResultSet results    = null;

        ArrayList<String> projectList = new ArrayList<>();

        try {
            query = conn.createStatement();
            sql = "Select Pname, Pnumber from employee join project on Dno=Dnum " + "where Ssn=" + ssn;
            results = query.executeQuery(sql);

            while(results.next()) 
            {
                projectList.add(results.getString("Pname"));
                projectList.add(results.getString("Pnumber"));
            }

            results.close();
            query.close();
        } catch (SQLException e) {
            System.err.println("Error: unable to get projects!");
            System.exit(1);
        }

        return projectList;
    }
    
    //inserts project info into the database
    public void insertProjectHours(String ssn, ArrayList<String> projects) 
    {

        int numProjects = projects.size()/3;
        int projCount = 1;

        for(int i = 0; i < numProjects; i++ ) {

            if(Double.parseDouble(projects.get(projCount+1)) < 0.01) 
            {
                projCount += 3;
                continue;
            }

            try 
            {
                String sql = "insert into works_on values (" + ssn + ", " + projects.get(projCount++) +
                        ", " + projects.get(projCount) +")";
                projCount += 2;

                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);

            } catch (SQLException e) 
            {
                System.err.println("Error: unable to add employee projects.");
                System.exit(1);
            }
        }

    }
    
    //inserts dependent info into database
    public void insertDependent(String ssn, String name, String sex, String bdate, String relation) 
    {

        try {
            String sql = "insert into dependent values (" + ssn + ", '" + name + "', '" + sex +
                    "', '" + bdate + "', '" + relation + "')";

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException e) 
        {
            System.err.println("Error: unable to add dependent");
            System.exit(1);
        }

    }
    
    //uses arrayList to retrieve employee info from database
    public ArrayList<String> retrieveEmployeeInfo(String ssn) {
    	
        String sql;
        Statement query;
        ResultSet results;

        ArrayList<String> empInfoList = new ArrayList<>();

        try {
            query = conn.createStatement();
            sql = "select * from employee where Ssn=" + ssn;
            results = query.executeQuery(sql);
            
            //adds info from database to arrayList
            while(results.next()) {
                empInfoList.add(results.getString("Fname"));
                empInfoList.add(results.getString("Minit"));
                empInfoList.add(results.getString("Lname"));
                empInfoList.add(results.getString("Ssn"));
                empInfoList.add(results.getString("Bdate").substring(0, 11));
                empInfoList.add(results.getString("Address"));
                empInfoList.add(results.getString("Sex"));
                empInfoList.add(results.getString("Salary"));
                empInfoList.add(results.getString("Superssn"));
                empInfoList.add(results.getString("Dno"));
            }

            results.close();
            query.close();

        } catch (SQLException e) 
        {
            System.err.println("Error: unable to get employee information.");
            System.exit(1);
        }

        return empInfoList;
    }
    
    //uses arrayList to retrieve project info
    public ArrayList<String> retrieveProjectInfo(String ssn) {
    
        String sql;
        Statement query;
        ResultSet results;
        
        
        ArrayList<String> projectInfoList = new ArrayList<>();

        try {
            query = conn.createStatement();
            sql = "select Pname, Hours from works_on join project on Pno=Pnumber " +
                    "where Essn=" + ssn;
            results = query.executeQuery(sql);

            while(results.next()) 
            {
            	projectInfoList.add(results.getString("Pname"));
            	projectInfoList.add(results.getString("Hours"));
            }


            results.close();
            query.close();
           
        } 
        catch (SQLException e) 
        {
            System.err.println("Error: unable to retrieve project information");
            return null;
        }

        return projectInfoList;
    }
    
    //uses arrayList to retrieve dependent information from database
    public ArrayList<String> retrieveDependentInfo(String ssn) {
     
        String sql;
        Statement query;
        ResultSet results;

        ArrayList<String> depInfo = new ArrayList<>();

        try {
            query = conn.createStatement();
            sql = "SELECT * from dependent where Essn=" + ssn;
            results = query.executeQuery(sql);

            while(results.next()) {
            	depInfo.add(results.getString("Dependent_name"));
            	depInfo.add(results.getString("Sex"));
            	depInfo.add(results.getString("Bdate"));
            	depInfo.add(results.getString("Relationship"));
            }

          
            results.close();
            query.close();
           
        } catch (SQLException e) 
        {
            System.err.println("Error: unable to retrieve dependent info");
            e.printStackTrace();
            System.exit(1);
        }

        return depInfo;
    }
    
    public static void main(String[] args) {  
        launch(args);  
     }  

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                closeConnection();
            }
        });
        
        this.connectToDatabase(primaryStage);

        //if connected to database proceed to manager login
        if(dbConn) {
            this.managerLogin(primaryStage);
        }

    }
    //established connection to oracle database
    public void connectToDatabase(Stage stage) {
    	stage.setTitle("Connect to Database");
    	
    	Label title = new Label("Connect to Database");
    	title.setFont(Font.font(25));
    	title.setAlignment(Pos.TOP_CENTER);
    	
    	//button for user to click
    	Button btn = new Button("Connect");
    	btn.setAlignment(Pos.CENTER);
    	
    	GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setVgap(10);
		grid.setHgap(10);
		grid.add(title, 0, 0);
		grid.add(btn, 0, 1);
		GridPane.setHalignment(btn, HPos.CENTER);
		
		
		Scene scene = new Scene(grid, 375, 100);
		stage.setScene(scene);
		stage.show();
    	
		//action when the user clicks the Connect button
    	btn.setOnAction(new EventHandler<ActionEvent>()
    			{
    			@Override
    			public void handle(ActionEvent event)
    			{
    				//initiates connection to database
    				statusOfConn = initiateConnection(DBINFO, DBUSERNAME, DBPASSW);
    				
    				if(statusOfConn)
    				{
    					//sets database connection to true since we are now connected
    					dbConn = true;
    					managerLogin(stage);
    				}
    				else
    				{
    					//displays error alert
    					Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Database Connection Error");
                        alert.setHeaderText("Error Connecting to Database");
                        alert.showAndWait();    
    				}
    			}
    			});
    }
    
    //manager login screen
    public void managerLogin(Stage stage) 
    {
        
        stage.setTitle("Manager Login");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10); 
        grid.setVgap(10);
     
        Text connSucc = new Text("Database Connection Successful!");
        connSucc.setFont(Font.font(20));
        grid.add(connSucc, 0, 0, 2, 1);
        Text title = new Text("Please enter your SSN");
        title.setFont(Font.font(20));
        grid.add(title, 0, 1, 2, 1);
        GridPane.setHalignment(title, HPos.CENTER);

        
        Label ssn = new Label("SSN:");
        grid.add(ssn, 0, 2);
        
        //ssn text field
        TextField ssnField = new TextField();
        grid.add(ssnField, 1, 2);

        //sign in button
        Button btn = new Button("Sign In");
        grid.add(btn, 1, 3);
        GridPane.setHalignment(btn, HPos.CENTER);
        
        



        // When sign in button is click it checks to see if the ssn is a manager ssn
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) 
            {
            	//boolean value to check is ssn is a manager's ssn
            	boolean checkManagerSsn = checkManagerSsn(Integer.parseInt(ssnField.getText()));
            	
            	//if ssn is manager ssn then proceeds to insert employee page
                if (checkManagerSsn) 
                {
                    insertEmployee(stage);
                } 
                //else displays error and states that ssn is not a valid manager ssn
                else 
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("SSN Error");
                    alert.setHeaderText("Manager SSN is invalid");
                    alert.setContentText("Please enter a valid manager SSN");
                    alert.showAndWait();
                }
            }
        });

        Scene scene = new Scene(grid, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    //insert new employee informatino
    public void insertEmployee(Stage stage) {

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        //title
        Text title = new Text("Insert Employee");
        title.setFont(Font.font(35));
        grid.add(title, 0, 0, 2, 1);
        GridPane.setHalignment(title, HPos.CENTER);
        
        
        //labels for text fields
        Label fname = new Label("First Name:");
        Label middle = new Label("Middle Initial:");
        Label lname = new Label("Last Name:");
        Label ssn = new Label("SSN:");
        Label birthDate = new Label("Birth Date:");
        Label address = new Label("Address:");
        Label sex = new Label("Sex:");
        Label salary = new Label("Salary:");
        Label superSsn = new Label("Supervisor SSN:");
        Label department = new Label("Department:");
        
        
        //input fields
        TextField fnameField = new TextField();
        TextField middleField = new TextField();
        TextField lnameField = new TextField();
        TextField ssnField = new TextField();
        DatePicker bdatePicker = new DatePicker();
        String pattern = "dd-MMM-yy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        bdatePicker.setPromptText("MM/DD/YYYY");
        TextField addressField = new TextField();
        ChoiceBox sChoice = new ChoiceBox(); //drop down list
        sChoice.setItems(FXCollections.observableArrayList("F", "M")); // two choices male or female
        TextField salField = new TextField();
        TextField superSsnField = new TextField();
        ChoiceBox depChoice = new ChoiceBox(); //dept drop down list
        depChoice.setItems(FXCollections.observableArrayList("Research", "Administration", "Headquaters"));

        //add labels and input fields to grid
        grid.add(fname, 0, 1);
        grid.add(fnameField, 1, 1);
        grid.add(middle, 0, 2);
        grid.add(middleField, 1, 2);
        grid.add(lname, 0, 3);
        grid.add(lnameField, 1, 3);
        grid.add(ssn, 0, 4);
        grid.add(ssnField, 1, 4);
        grid.add(sex, 0, 5);
        grid.add(sChoice, 1, 5);
        grid.add(birthDate, 0, 6);
        grid.add(bdatePicker, 1, 6);
        grid.add(address, 0, 7);
        grid.add(addressField, 1, 7);
        grid.add(salary, 0, 8);
        grid.add(salField, 1, 8);
        grid.add(superSsn, 0, 9);
        grid.add(superSsnField, 1, 9);
        grid.add(department, 0, 10); 
        grid.add(depChoice, 1, 10);

        // submit button
        Button btn = new Button("Submit");
        grid.add(btn, 1, 11);

        //when user hits the submit button it validates the input fields and attempts to add employee into database
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fname = fnameField.getText();
                String middle = middleField.getText();
                String lname = lnameField.getText();
                String ssn   = ssnField.getText();
                String address = addressField.getText();
                String sex = sChoice.getValue().toString();
                String salary = salField.getText();
                String super_ssn = superSsnField.getText();
                String dno = depChoice.getValue().toString();
                String bdate = null;


                if (bdatePicker.getValue() != null) 
                {
                    bdate = bdatePicker.getValue().format(formatter).toUpperCase();
                    
                } 
                else 
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Date Format Error");
                    alert.setHeaderText("Error Formatting Date of Birth");
                    alert.showAndWait();
                    return;
                }

                
                
                //converts string text into corresponding department number
                if(dno.equals("Research")) 
                {
                	dno = "5";
                }
                else if(dno.equals("Administration"))
                {
                	dno = "4";
                }
                else if(dno.equals("Headquaters"))
                {
                	dno = "1";
                }
                
                //inserts new employee into database
                insertEmployee(fname, middle, lname, ssn, bdate, address, sex, salary,
                        super_ssn, dno);

                //continues to screen to add projects
                projects(stage, ssn);
            }
        });
        
        grid.setAlignment(Pos.CENTER);
        Scene scene = new Scene(grid, 600, 600);
        stage.setScene(scene);
        stage.show();
    }
    //screen to add employee project info
    public void projects(Stage stage, String ssn) {

        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        
        Text scenetitle = new Text("Project Hours");
        scenetitle.setFont(Font.font(35));
        grid.add(scenetitle, 0, 0, 2, 1);

        //retrieves project names base on employee department
        ArrayList<String> projectsList = retrieveProjects(ssn);

        //stores references to added projects
        ArrayList<TextField> textFields = new ArrayList<>();

        int txtFldCount = 0;
        int rowCount = 1;

        for(int index = 0; index < projectsList.size(); index+=2) 
        {
            Label project = new Label(projectsList.get(index));
            grid.add(project, 0, rowCount);
            textFields.add(new TextField());
            textFields.get(txtFldCount).setText("0");
            grid.add(textFields.get(txtFldCount), 1, rowCount);
            rowCount++;
            txtFldCount++;
        }

        // checkbox to add dependents for new employee
        CheckBox depBox = new CheckBox();
        depBox.setText("Select if you have dependents");
        grid.add(depBox, 1, ++rowCount);

        //submit button
        Button btn = new Button("Submit");
        grid.add(btn, 1, ++rowCount);
        GridPane.setHalignment(btn, HPos.CENTER);

        //when user clicks Submit button this adds project information and dependend information to database
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Iterator<TextField> iterator = textFields.iterator();
                double sumHours = 0;
                while (iterator.hasNext()) 
                {
                    sumHours += Double.parseDouble(iterator.next().getText());
                }

                if (sumHours > 40) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Max Hours Error");
                    alert.setHeaderText("Maximum Hours Exceeded");
                    alert.setContentText("Hours assigned must be between 0 and 40 hours");
                    alert.showAndWait();
                } 
                else 
                {
                    ArrayList<String> projects = new ArrayList<String>();
                    Iterator<TextField> txtFlsItr = textFields.iterator();
                    Iterator<String> projItr = projectsList.iterator();

                    while (projItr.hasNext()) {
                        projects.add(projItr.next());
                        projects.add(projItr.next());
                        projects.add(txtFlsItr.next().getText());
                    }

                    insertProjectHours(ssn, projects);
                    
                    //is user needs to add more than one dependent
                    if (depBox.isSelected())
                    {
                        addDependents(stage, ssn);
                    } 
                    else 
                    {
                        displayReport(stage, ssn, false);
                    }
                }
            }
        });

        stage.setScene(new Scene(grid, 500, 400));
        stage.show();

    }
    
    //displays report to manager of all the employee information added to database
    public void displayReport(Stage stage, String ssn, boolean hasDependents) {

        // setup layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 10, 25, 10));
        grid.setAlignment(Pos.CENTER);

        // add Title
        Text title = new Text("Employee Report");
        title.setFont(Font.font(35));
        grid.add(title, 0, 0, 2, 1);

        //employee info
        Label empInfo = new Label("New Employee Information:");
        grid.add(empInfo, 0, 1);

        String[] empLabels = {"First Name: ", "Middle Initial: ","Last Name: ", "SSN: ",
                               "Birth Date: ", "Address: ", "Sex: ", "Salary: ",
                               "Supervisor SSN: ", "Department Number: "};

        ArrayList<String> empInfoList = retrieveEmployeeInfo(ssn);
        int rowCount = 2;
        // adds labels and employee information to grid
        for(int i = 0; i < 10; i++) {

            Label label = new Label(empLabels[i]);
            grid.add(label, 1, rowCount);

            Label data = new Label(empInfoList.get(i));
            grid.add(data, 2, rowCount);
            rowCount++;
        }

        
        ArrayList<String> projects = retrieveProjectInfo(ssn);
        
        if(projects != null) {

            Label projectInfo = new Label("Project Assigned:");
            grid.add(projectInfo, 0, rowCount++);

           
            Iterator<String> projItr = projects.iterator();
            //adds employee project labels and information to grid
            while(projItr.hasNext()) 
            {

                Label label = new Label(projItr.next() + ":");
                grid.add(label, 1, rowCount);

                Label data = new Label(projItr.next() + "h");
                grid.add(data, 2, rowCount++);
            }
        }

        int vOffset = 0;

        //Dependents
        if(hasDependents) {
            ArrayList<String> deps = retrieveDependentInfo(ssn);

           
            Label depsInfo = new Label("Dependents:");
            grid.add(depsInfo, 0, rowCount++);

            
            Iterator<String> depsItr = deps.iterator();
            
            //adds employees dependent lables and information to grid
            while(depsItr.hasNext()) {

                Label nLabel = new Label("Name: ");
                grid.add(nLabel, 1, rowCount);
                Label name = new Label(depsItr.next());
                grid.add(name, 2, rowCount++);

                Label sLabel = new Label("Sex: ");
                grid.add(sLabel, 1, rowCount);
                Label sex = new Label(depsItr.next());
                grid.add(sex, 2, rowCount++);

                Label bLabel = new Label("Birth Date: ");
                grid.add(bLabel, 1, rowCount);
                Label bdate = new Label(depsItr.next().substring(0, 11));
                grid.add(bdate, 2, rowCount++);

                Label rLabel = new Label("Relation: ");
                grid.add(rLabel, 1, rowCount);
                Label relation = new Label(depsItr.next());
                grid.add(relation, 2, rowCount++);

                vOffset += 150;
            }
        }

        //close button
        Button btn = new Button("Close");
        grid.add(btn, 1, rowCount);
        GridPane.setHalignment(btn, HPos.CENTER);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });

        stage.setScene(new Scene(grid, 600, 600 + vOffset));
        stage.show();
    }

    public void addDependents(Stage stage, String ssn) {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);                 
        grid.setHgap(10);                               
        grid.setVgap(10);                               
        
        //title
        Text title = new Text("Dependents");
        title.setFont(Font.font(35));
        grid.add(title, 0, 0, 2, 1);

        
        //labels
        Label name = new Label("Name:");
        Label sex = new Label("Sex:");
        Label birthDate = new Label("Birth Date:");
        Label relation = new Label("Relation:");
        
        //input fields
        TextField nameField = new TextField();
        ChoiceBox sChoice = new ChoiceBox();
        sChoice.setItems(FXCollections.observableArrayList("M", "F"));
        DatePicker bdatePicker = new DatePicker();
        String pattern = "dd-MMM-yy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        bdatePicker.setPromptText("MM/DD/YYYY");
        TextField relationField = new TextField();
        CheckBox depChoice = new CheckBox();
        depChoice.setText("Add Dependent");
        
        
        // add labels and input fields to grid
        grid.add(name, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(sex, 0, 2);
        grid.add(sChoice, 1, 2); 
        grid.add(birthDate, 0, 3);
        grid.add(bdatePicker, 1, 3);
        grid.add(relation, 0, 4);
        grid.add(relationField, 1, 4);
        grid.add(depChoice, 1, 5);

        // add Submit button
        Button btn = new Button("Submit");
        grid.add(btn, 1, 6);
        GridPane.setHalignment(btn, HPos.CENTER);

        btn.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent event) {
                String name = nameField.getText();
                String sex = sChoice.getValue().toString();
                String bdate = null;

                if (bdatePicker.getValue() != null) 
                {
                    bdate = bdatePicker.getValue().format(formatter).toUpperCase();
                } 
                else 
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Date Format Error");
                    alert.setHeaderText("Error Formatting Date of Birth");
                    alert.showAndWait();
                    return;
                }

                String relation = relationField.getText();
                
                //insert dependent into database
                insertDependent(ssn, name, sex, bdate, relation);

                if (depChoice.isSelected()) 
                {
                    nameField.clear();
                    relationField.clear();
                } 
                else 
                {
                    displayReport(stage, ssn, true);
                }
            }
        });

        stage.setScene(new Scene(grid, 400, 300));
        stage.show();
    }

}
