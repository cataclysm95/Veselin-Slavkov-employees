import java.util.ArrayList; 
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Employee{
	
		private String EmpId;
		private ArrayList<String> Projects;
		private ArrayList<LocalDate[]> WorkInterval;
		
		Employee(String EmpId){
			this.EmpId = EmpId;
			this.Projects = new ArrayList<String>();
			this.WorkInterval = new ArrayList<LocalDate[]>();
		}
		
		public String getEmpId(){
			return this.EmpId;
		}
		
		public void addWork(String ProjectID, String DateFrom, String DateTo){
			LocalDate start, end;
			start = LocalDate.parse(DateFrom);
			if (DateTo.equals("NULL")){
				end = LocalDate.now();
			}
			else{
				end = LocalDate.parse(DateTo);
			}
			this.Projects.add(ProjectID);
			this.WorkInterval.add(new LocalDate[]{start, end});
		}
		
		public LocalDate[] hasWorkedOnProject(String ProjectID){
			// check if employee has worked on the project
			if (this.Projects.contains(ProjectID)){
				// find the index of the project
				int indx = this.Projects.indexOf(ProjectID);
				return this.WorkInterval.get(indx);
			}
			return null;
		}
	
	public static String readFileAsString(String fileName)throws Exception{ 
	    String data = ""; 
	    data = new String(Files.readAllBytes(Paths.get(fileName))); 
	    return data; 
	  }
	
    public static void main(String args[]) throws Exception {
    	String file_path = args[0];
    	// TODO get path as input parameter
    	String data = readFileAsString(file_path);
    	String[] lines = data.split("\n");
    	
    	// create the employees
    	ArrayList<Employee> employees = new ArrayList<Employee>();
    	for(String line : lines){
    		// split the worklog
    		String[] workLog = line.trim().split(", ");
    		
    		String id = workLog[0];
    		String project_id = workLog[1];
    		String start_date_str = workLog[2];
    		String end_date_str = workLog[3];
    		
    		// check if we have already created the employee
    		Employee cur_emp = null;
    		for(Employee emp: employees){
    			if (emp.getEmpId().equals(id)){
    				cur_emp = emp;
    				break;
    			}
    		}
    		if (cur_emp == null){
    			cur_emp = new Employee(id);
    			employees.add(cur_emp);
    		}
    		
    		cur_emp.addWork(project_id, start_date_str, end_date_str);
    	}
    	
    	// find out who worked most time together
    	String tg = "";
    	long max_days = -1;
    	for (int i = 0; i < employees.size(); i++){
    		Employee cur_emp = employees.get(i);

    		for (int j = i + 1; j < employees.size(); j++){
    			Employee cur_colleague = employees.get(j);
    			long days_worked_together_all_projects = 0;
    			//System.out.println(days_worked_together_all_projects);
    			for (String project_id : cur_emp.Projects){
    				int work_inteval_indx = cur_emp.Projects.indexOf(project_id);
    				LocalDate[] cur_emp_start_end = cur_emp.WorkInterval.get(work_inteval_indx);
    				LocalDate[] cur_colleague_start_end = cur_colleague.hasWorkedOnProject(project_id);
    				if (cur_colleague_start_end != null){
    					LocalDate max_start_date = null;
    					LocalDate min_end_date = null;
    					if (cur_emp_start_end[0].isAfter(cur_colleague_start_end[0])){
    						max_start_date = cur_emp_start_end[0];
    					}
    					else{
    						max_start_date = cur_colleague_start_end[0];
    					}
    					if (cur_emp_start_end[1].isAfter(cur_colleague_start_end[1])){
    						min_end_date = cur_colleague_start_end[1];
    					}
    					else{
    						min_end_date = cur_emp_start_end[1];
    					}
    					long days_difference = ChronoUnit.DAYS.between(max_start_date, min_end_date);
    					if (days_difference >= 0){
    						days_worked_together_all_projects += days_difference;
    					}
    				}
    			}
    			if (days_worked_together_all_projects > max_days){
    				max_days = days_worked_together_all_projects;
    				tg = cur_emp.getEmpId() + " " + cur_colleague.getEmpId() + " ";
    			}
    		}
    	}
    	System.out.println(tg + Long.toString(max_days));
    }
}
