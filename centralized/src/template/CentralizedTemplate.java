package template;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
//the list of imports
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import logist.LogistSettings;

import logist.Measures;
import logist.behavior.AuctionBehavior;
import logist.behavior.CentralizedBehavior;
import logist.agent.Agent;
import logist.config.Parsers;
import logist.simulation.Vehicle;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

/**
 * A very simple auction agent that assigns all tasks to its first vehicle and
 * handles them sequentially.
 *
 */
@SuppressWarnings("unused")
public class CentralizedTemplate implements CentralizedBehavior {

    private Topology topology;
    private TaskDistribution distribution;
    private Agent agent;
    private long timeout_setup;
    private long timeout_plan;
    List<T> listT = new ArrayList<T>();
    List<Act> listAct = new ArrayList<Act>();
    List<Tv> listTv = new ArrayList<Tv>();
    double cost;
    double nextcost;
    Hashtable<Integer, List<Act>> nextTask = new Hashtable<Integer, List<Act>>();
    Hashtable<Integer, List<Act>> nextTask_clone = new Hashtable<Integer, List<Act>>();
    Hashtable<Act, Integer> time = new Hashtable<Act, Integer>();
    List<Integer> LastMove = new ArrayList<Integer>();
	long NUMBER_OF_TRAINING_STEP = 500000;
    @Override
    public void setup(Topology topology, TaskDistribution distribution,
            Agent agent) {
        
        // this code is used to get the timeouts
        LogistSettings ls = null;
        try {
            ls = Parsers.parseSettings("config\\settings_default.xml");
        }
        catch (Exception exc) {
            System.out.println("There was a problem loading the configuration file.");
        }
        
        // the setup method cannot last more than timeout_setup milliseconds
        timeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
        // the plan method cannot execute more than timeout_plan milliseconds
        timeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);
        
        this.topology = topology;
        this.distribution = distribution;
        this.agent = agent;
        
    }

    @Override
    public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
        long time_start = System.currentTimeMillis();
        
        
        cost = 20000;
        
        //List<Vehicle> variable = new ArrayList<Vehicle>();
        //create a hashtable with list of task corresponding to vehicle id
        
        // Set all task to null at the beginning ?
        
        List<Task> taskList = new ArrayList<Task>();
        for (Task task : tasks) {
        	taskList.add(task)	;
        }
        
        
     // generate all possible action that have to be performed ( 2 for each task )  
        
        
        for(int i = 0 ; i < taskList.size() ; i++)
        {	Task task = taskList.get(i);
        	listAct.add(new Act(task, false));
        	listAct.add(new Act(task, true));
        }
        //initilize tab
        
        for(int j = 0; j < vehicles.size() ; j++) {
        	List<Act> act = new ArrayList<Act>();
           
        	nextTask.put(j, act);
        }
        
        //time array useless for now
        for(int i = 0 ; i < listAct.size() ; i++)
        {	Act action = listAct.get(i);
        	time.put(action,i);
        }
        
        
        int n = 0; 
    	
    	while(n < listAct.size() ) {
    		for(int j = 0 ; j < vehicles.size() ; j++) {
    			List<Act> act = nextTask.get(j);
    			act.add(listAct.get(n));
        		act.add(listAct.get(n+1));
        		nextTask.put(j, act);
        		

        		n =  n + 2;
        		if(n == listAct.size() ) {break;}
        		        		
    		}
    		
    		
    		
    	}
    	
    	
        
    	
    	
    	//A faire : Constraint function and transition + swap
    	// MAking swap or transfer
    	//impaire = deliver
    	
    	//nextTask_clone.clear();
    	//nextTask_clone.putAll(nextTask);
    	
    	nextTask_clone = (Hashtable<Integer, List<Act>>) nextTask.clone();
    	
		for(int i = 0 ; i< vehicles.size();i++) {
        swap(listAct.get(1),listAct.get(32),i );
        swap(listAct.get(9),listAct.get(24),i );
		}
		
    	
        	
       
		
        
        
        
        
        /*
        
        for(int i = 0 ; i< vehicles.size();i++)
        {
        	listTv.add(new Tv(vehicles.get(i),new T(taskList.size()*2,1,taskList.get(taskList.size()*2 - 1 -i),null,null, false))); //genere 4 tache pour les vehicles dont nextask == null
        	
        }
        
        int i= (taskList.size()*2) - vehicles.size() - 1;
        int j = 0;
        int ti = taskList.size()*2 -1 ;
        while(i >= 0)
        {
        	Task task = taskList.get(i); 
        	Tv vehicle = listTv.get(j); 
        	boolean up = false;
        	if( i < taskList.size() ) {up = true ;}
        	
        	listT.add(new T(ti,1,task,vehicle, null, up)); //prend la place du dernier dans le graph
        	if(constraint() == false)
        	{
        		listT.remove(listT.size() - 1);
        		j = j + 1 ;
        		if(j == 3) {j = 0;}
        		
        	}
        	else {i++;} //si toute les contrtaintes respecté, on passe a la task suivante
        }
      */  
        
//		System.out.println("Agent " + agent.id() + " has tasks " + tasks);
		
		
		
		List<Plan> plans = new ArrayList<Plan>();
		for(int k = 0 ; k< NUMBER_OF_TRAINING_STEP ; k++) {
			
		plans.clear();	
		
		for(int i = 0 ; i< vehicles.size(); i++)
        {
			Plan planVehicle= naivePlan(vehicles.get(i), nextTask.get(i));
			plans.add(planVehicle);
			//System.out.println(planVehicle);
        }		
        
                
		
        
        while (plans.size() < vehicles.size()) {
            plans.add(Plan.EMPTY);
        }
        
        nextcost = 0;
        for(int i = 0 ; i< plans.size(); i++)
        {
        	nextcost = nextcost + plans.get(i).totalDistance();
        	
        }
        
        
        
        
        
        
        
        
        
        
        nextTask = optimize( vehicles, taskList);
        
		
		
        
		
        long time_end = System.currentTimeMillis();
        long duration = time_end - time_start;
        System.out.println("The plan was generated in "+duration+" milliseconds.    COST = "+cost );
		
        
        
		}
		
        return plans;
		
        
        
        
        
        
    }


    private Plan naivePlan(Vehicle vehicle,  List<Act> actions) {
        City current = vehicle.getCurrentCity();
        Plan plan = new Plan(current);
        
        
        
        for(int i =0 ; i < actions.size() ; i++)
        {
        	Act action =   actions.get(i);
        	if(action.get_deliver() == false) { // if action is to pickup task
        		
        		for (City city : current.pathTo(action.get_task().pickupCity)) {
                    plan.appendMove(city);
                }

                plan.appendPickup(action.get_task());
                current = action.get_task().pickupCity;
        	}
        	else { // if action is to deliver task
        		
        		for (City city : current.pathTo(action.get_task().deliveryCity)) {
                    plan.appendMove(city);
                }
        		
        		plan.appendDelivery(action.get_task());
        		current = action.get_task().deliveryCity;
        	}
        	 
        }
        
        return plan;
    }
        
        
        
        /*
        
        for (Task task : tasks) {
            // move: current city => pickup location
            for (City city : current.pathTo(task.pickupCity)) {
                plan.appendMove(city);
            }

            plan.appendPickup(task);

            // move: pickup location => delivery location
            for (City city : task.path()) {
                plan.appendMove(city);
            }

            plan.appendDelivery(task);

            // set current city
            current = task.deliveryCity;
        }
        return plan;
    }*/
    
    
    
    
    
    
    public void give( Task task,int v1, int v2) {//echange de tasks entre vehicle ce gfait par pair de tasks liées ( tasks liées ont la même tache mais un boolean up différent)
    	List<Act> actions1 = nextTask_clone.get(v1);
    	List<Act> actions2 = nextTask_clone.get(v2);
    	int id1 = 0;
    	int id2 = 0;
    	
    	
    	
    	for(int i = 0; i < actions1.size(); i++)
    	{ 	//find actions according to task
    		if(actions1.get(i).get_task().id == task.id && !actions1.get(i).get_deliver()) {
    			id1 = i;
    			
    	    	
    		}
    		if(actions1.get(i).get_task().id == task.id && actions1.get(i).get_deliver()) {
    			id2 = i;
    			
    		}
    	}
    	
    	
    	
    	Act act1 = actions1.get(id1);
    	Act act2 = actions1.get(id2);
    	
    	actions2.add(act2);
		actions1.remove(act2);
    	actions2.add(act1);
		actions1.remove(act1);
    	//actions1.remove(act1);
    	//actions1.remove(act2);
    	
    	
    	
    	nextTask_clone.put(v1, actions1);
    	nextTask_clone.put(v2, actions2);
    	
    	

	}
    
    
    public void swap( Act action1,Act action2,int v ) {//echange de action place in the same vehicle
    	List<Act> actions1 = nextTask_clone.get(v);
    	
    	int id1 = 0;
    	int id2 = 0;
    	for(int i = 0; i < actions1.size(); i++)
    	{ 	
    		if(actions1.get(i) == action1) {id1 = i;}
    		if(actions1.get(i) == action2) {id2 = i;}
    	}
    	Act inter = actions1.get(id1);
    	actions1.set(id1, actions1.get(id2));
    	actions1.set(id2, inter);

    	nextTask_clone.put(v, actions1);

	}
    
    
  //condition qui assure que les tasks liées sont dans le même vehicle et dans le bon ordre
    private boolean constraint(List<Vehicle> vehicles, Hashtable<Integer, List<Act>> nextTask_clone )
    {
    	
    	boolean validation = true;
    	for(int v =0; v < vehicles.size() ; v++) // for all vehicle
    	{
    		List<Act> actions = nextTask_clone.get(v);
    		int weight = 0;
    		for(int i = 0; i< actions.size() ; i++ ) { // for all possible actions
    			Act action1 = actions.get(i);
    			
    			for(int j = 0; j< actions.size() ; j++ ) {// for all possible actions
    				Act action2 = actions.get(j);
    				if( action1.get_task().id == action2.get_task().id )  { //if same task
    					if( i != j ) 
    					{// diff action
	    					
	    					
	    					if(action1.get_deliver()  ) 
	    					{ // if action1 is deliver
	    						if(i<j) {swap(action1,action2,v);
	    								}	//if action1 comes after deliver
	    					}
	    					else
	    					{ // if action1 is pickup
	    						if(i>j) {swap(action1,action2,v);
	    								}// if action1 comes before pick up 
	    						
	    					}
	    					
	    				}
					}
    			}
    			
    			//if(actions.get(i+1) == action1 ) {validation = false;}//condition 1
    			
    			
    		if(action1.get_deliver()) {weight = weight - action1.get_task().weight;} // deliver
    		else {weight = weight + action1.get_task().weight;} // pick up
    		if(vehicles.get(v).capacity() < weight) // overcapacity
    		{
    			validation = false;
    			System.out.println("Vehicle: " + v +" is OVERLOADED");
    		}
    		
    		}
    		
    		
    	}
    	
    		
    		
    		
    		
    
    	return validation;
    }
    
    
    
    
    private Hashtable<Integer, List<Act>>  optimize(List<Vehicle> vehicles, List<Task> taskList ){
    	
		
		
    	
    	
    	
        
        if (nextcost < cost ) {
        	
        	//nextTask.clear(); 			// if cost is better then we update the nextTask
	    	//nextTask.putAll(nextTask_clone);
	    	nextTask = (Hashtable<Integer, List<Act>>) nextTask_clone.clone(); // make nextTak the new checkpoint 
	    	cost = nextcost;
	    	//System.out.println("New Plan: "+ cost);
        }
        else { 
        	
        	//nextTask_clone.clear();
        	//nextTask_clone.putAll(nextTask);
        	nextTask_clone = (Hashtable<Integer, List<Act>>) nextTask.clone(); // load the last checkpoint
        	//System.out.println("Stay on previous plan: "+ cost);
        	
        }
        //here do transformation Next_clone = transf(next)
        
        Random rand = new Random();
        
    	do {
    		if(rand.nextBoolean()) { //give action
    			
    			int v1 = rand.nextInt(vehicles.size());
    			int v2 = rand.nextInt(vehicles.size());
    			while(v1 == v2 || nextTask_clone.get(v1).size() == 2 ) {v1 = rand.nextInt(vehicles.size());
    							v2 = rand.nextInt(vehicles.size());}// assure vehicle different
    			
    			int t = rand.nextInt(nextTask_clone.get(v1).size()); // pick random action in choosen vehicle
    			Task task = nextTask_clone.get(v1).get(t).get_task(); // get task
    			give(task,v1,v2);
    		}
			else {	//swap action
				
				int v1 = rand.nextInt(vehicles.size());
				int a1 = 0;
				int a2 = 0;
				
    			while(a1 == a2) {   a1 = rand.nextInt(nextTask_clone.get(v1).size());
								    a2 = rand.nextInt(nextTask_clone.get(v1).size());}  // assure action in the v is diff
    			
    			Act action1 = nextTask_clone.get(v1).get(a1);
    			Act action2 = nextTask_clone.get(v1).get(a2);
    			swap(action1,action2,v1);
    			
    			
    			
    		}
    		
    		
    		//System.out.println("NOPE !");
    		}
    	while(constraint(vehicles,nextTask_clone) == false);
    		
    	
	//System.out.println("Solution !");    
        		
    return nextTask_clone;
		
    }
    
    
/*
    private boolean Constraint( Hashtable<Integer, List<Task>> variable, Hashtable<Task, Integer>  time ) {
    	boolean validation = true;
    	List<Task> vehicle_tasks = new ArrayList<Task>();
    	//check for each vehicle
    	for(int i =0 ; i < variable.size(); i++)
    	{
    		
    		vehicle_tasks =  variable.get(i);
    		// for each Task (in the right order ) of a vehicle 
    		for(int j = 0 ; j < vehicle_tasks.size(); j++) 
    		{
    			Task task = vehicle_tasks.get(j);
    			if (nextTask(task,null) == task ) {validation = false;} ; //condition 1 
    			
    		}
    		 	
    	}
    	
    	
    	return validation;
    	
    }*/
    
  /*  
    private Task nextTask(Task task, Vehicle v) { //return next task according to task or vehicle
    	Task nxTask = null;
    	if(task != null ) {
    		for(int i = 0; i <  )
    			get(Task);
    		
    	}
    	else if( v != null) {}
    	return nxTask;
    	
    }
    
    */
    
}

class Act{
	private Task task;
	private boolean deliver;
	
	public Act(Task task, boolean deliver) 
	{
		
		this.deliver = deliver;
		this.task = task;
		
	}
	
	public boolean get_deliver() {
		return this.deliver;
	}
	public Task get_task() {
		return this.task;
	}
	
	
	
	 
}
class T{
	private int time_p;
	private int time_d;
	private Task task;
	private T nextTask;
	private Tv vehicle;
	private boolean up;
	
	public T(int time_p, int time_d, Task task, Tv vehicle,T nextTask, boolean up) 
	{
		this.time_p = time_p;
		this.time_d = time_p;
		this.up = up;
		this.task = task;
		this.nextTask = nextTask;
		this.vehicle = vehicle;
		constraint();
	}
	 
	public boolean constraint() {
		boolean validation = true;
		
		
		//condition qui assure que les tasks liées sont dans le même vehicle et dans le bon ordre
		
		
		
		
		
		
		return validation;}
	
	
	
	
	
	public boolean get_up() {
		return this.up;
	}
	
	public int get_time_p() {
		return this.time_p;
	}
	public int get_time_d() {
		return this.time_d;
	}
	public T get_nextTask() {
		return this.nextTask;
	}
	
	public Task get_Task() {
		return this.task;
	}
	
	
	public Tv get_vehicle() {
		return this.vehicle;
	}
	
	
	public void set_time_p(int time) {
		this.time_p = time;
	}
	public void set_time_d(int time) {
		this.time_d = time;
	}
	
	public void set_nextTask(T nexttask) {
		this.nextTask = nexttask;
	}
	
	public void set_vehicle(Tv v) {
		this.vehicle = v;
	}
	
}


class Tv{

	private T nextTask;
	private Vehicle v;
	
	public Tv( Vehicle v,T nextTask) 
	{
		
		this.nextTask = nextTask;
		this.v = v;
		
	}
	 
	public boolean constraint() {
		boolean validation = true;
		if(this.nextTask.get_time_p() != 1 ) {validation = false;} //condition 2
		
		if(this.nextTask.get_vehicle() !=  this ) {validation = false;} //condition 4
		
		
		
		
		return validation;
		
	}

	public T get_nextTask() {
		return this.nextTask;
	}
	

	public Vehicle get_vehicle() {
		return this.v;
	}
	
	
	public void set_nextTask(T nexttask) {
		this.nextTask = nexttask;
	}

}

