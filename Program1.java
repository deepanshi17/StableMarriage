/*
 * Name: <your name>
 * EID: <your EID>
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Your solution goes in this class.
 * 
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */


public class Program1 extends AbstractProgram1 {

	class Queue{
		int first, last, size;
		int capacity;
		int queue[];
		
		public Queue(int capacity) {
			this.capacity = capacity;
			first = 0;
			size = 0;
			last = capacity - 1;
			queue = new int [capacity];
		}
		
		boolean isFull(Queue q) {
			return(q.size == q.capacity);
		}
		
		boolean isEmpty(Queue q) {
			return(q.size == 0);
		}
		
		void push(int i) {
			if(!isFull(this)) {
				last += 1;
				last %= capacity;
				queue[last] = i;
				size ++;
			}else {
				System.out.println("Queue is full");
				return;
			}
		}
		
		int pop() {
			if(!isEmpty(this)) {
				int i = queue[first];
				first += 1;
				first %= capacity;
				size--;
				return i;
			}else {
				return -1;
			}
		}
	}
	
    /**
     * Determines whether a candidate Matching represents a solution to the Stable Marriage problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
//	@Override
//	public Matching stableMarriageGaleShapley_locationoptimal(Matching marriage) {
//		ArrayList<Integer> employee_matching = new ArrayList<Integer>();
//		employee_matching.add(0, 1);
//		employee_matching.add(1, 0);
//		employee_matching.add(2, 4);
//		employee_matching.add(3, 3);
//		employee_matching.add(4, 2);
//		employee_matching.add(5, 1);
//		return new Matching(marriage, employee_matching);
//		}
	
    @Override
    public boolean isStableMatching(Matching marriage) {
        /* TODO implement this function */
    	int count = 0;
    	for(int emp = (marriage.getEmployeeCount()-1); emp < marriage.getEmployeeCount(); emp++) {
    		if(marriage.getEmployeeMatching().get(emp) == -1) {
    			for(int loc = 0; loc < marriage.getLocationCount(); loc++) {
    				while(marriage.getLocationPreference().get(loc).get(count) != emp) {
    					count++;
    				}
    				for(int q = 0; q < marriage.getLocationSlots().get(loc); q++) {
    					int maybe_matched = marriage.getLocationPreference().get(loc).get(q);
    					if(marriage.getEmployeeMatching().get(maybe_matched) == loc){
    						return false;
    					}
    				}
    			}
    		}
    	}
    	for(int i = 0; i < marriage.getEmployeeCount(); i++) {
    		if(marriage.getEmployeeMatching().get(i) != -1) { 
    		int matched_location = marriage.getEmployeeMatching().get(i);
    		int preference_counter = 0;

    		while(marriage.getLocationPreference().get(matched_location).get(preference_counter) != i){
    			int maybe_preferred = marriage.getLocationPreference().get(matched_location).get(preference_counter);
    			int maybes_match = marriage.getEmployeeMatching().get(maybe_preferred);
    			int pref_counter2 = 0;
    			if(maybes_match != -1) {
    			while(marriage.getEmployeePreference().get(maybe_preferred).get(pref_counter2) != maybes_match) {
    				if(marriage.getEmployeePreference().get(maybe_preferred).get(pref_counter2) == matched_location) return false;
    				pref_counter2++;
    			}
    			}
    			preference_counter++;
    		}
    		}
    	}
        return true; /* TODO remove this line */
    }
    


    /**
     * Determines a employee optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
	public Matching stableMarriageGaleShapley_employeeoptimal(Matching marriage) {
		/* TODO implement this function */
		int[] hires = new int[marriage.getEmployeeCount()];
		int[][] jobs = new int[marriage.getLocationCount()][marriage.getEmployeeCount()];
		int[] proposalCount = new int[marriage.getEmployeeCount()];
		ArrayList<Integer> matching = new ArrayList<Integer>();
		Queue q = new Queue(marriage.getEmployeeCount());
		marriage.setEmployeeMatching(matching);
		for (int i = 0; i < marriage.getEmployeeCount(); i++) {
			q.push(i);
		}
		for (int i = 0; i < marriage.getEmployeeCount(); i++) {
			matching.add(-1);
		}
		int[] employeeCount = new int[marriage.getEmployeeCount()];
		while (!q.isEmpty(q)) {
			while(proposalCount[q.queue[q.first]] >= marriage.getLocationCount() && !q.isEmpty(q)) {
				q.pop();
			}
			if(q.isEmpty(q)) {
				marriage.setEmployeeMatching(matching);
		        return marriage;
			}
			int topQueue = q.queue[q.first];
			int highest_preference = marriage.getEmployeePreference().get(topQueue).get(proposalCount[topQueue]);
			proposalCount[topQueue]++;
			if (employeeCount[highest_preference] < marriage.getLocationSlots().get(highest_preference)) {
				hires[topQueue] = 1;
				jobs[highest_preference][employeeCount[highest_preference]] = topQueue;
				employeeCount[highest_preference]++;
				matching.set(topQueue, highest_preference);
				q.pop();
			} else {
				for (int slot = 0; slot < marriage.getLocationSlots().get(highest_preference); slot++) {
					int current = jobs[highest_preference][slot];
					if(current != -1) {
					for (int j = 0; j < marriage.getEmployeeCount(); j++) {
						if (marriage.getLocationPreference().get(highest_preference).get(j) == topQueue){
							matching.set(topQueue, highest_preference);
							q.pop();
							matching.set(current, -1);
							employeeCount[current]--;
							jobs[highest_preference][slot] = -1;
							q.push(current);
							hires[highest_preference] = 1;
							jobs[highest_preference][employeeCount[highest_preference]] = topQueue;
							employeeCount[highest_preference]++;
							j = marriage.getEmployeeCount();
							slot = marriage.getLocationSlots().get(highest_preference);
						} else if (marriage.getLocationPreference().get(highest_preference).get(j) == current) {
							j = marriage.getEmployeeCount();
						}
					}
					}
				}
			}
		}
		marriage.setEmployeeMatching(matching);
        return marriage;
    	}

    /**
     * Determines a location optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_locationoptimal(Matching marriage) {
        /* TODO implement this function */
    	
    	int [] hires = new int [marriage.getEmployeeCount()];
    	int [][] jobs = new int [marriage.getLocationCount()][marriage.getEmployeeCount()];
    	int [] employeeCount = new int [marriage.getEmployeeCount()];
    	int[] proposalCount = new int[marriage.getEmployeeCount()];
    	ArrayList<Integer> matching = new ArrayList<Integer>(); 
    	Queue q = new Queue(marriage.getLocationCount());
    	for(int i = 0; i < marriage.getLocationCount(); i++) {
    		q.push(i);
    	}
    	for(int i = 0; i < marriage.getEmployeeCount(); i++) {
    		matching.add(-1);
    	}
    	marriage.setEmployeeMatching(matching);
    	while(!q.isEmpty(q)) {
    			int topQueue = q.queue[q.first];
    			int highest_preference = marriage.getLocationPreference().get(topQueue).get(proposalCount[topQueue]);
    			proposalCount[topQueue]++;
    			if(marriage.getEmployeeMatching().get(highest_preference) == -1) {
    				hires[topQueue] = 1;
    				jobs[topQueue][highest_preference] = 1;
    				employeeCount[topQueue]++;
    				matching.set(highest_preference, topQueue);
    				if(employeeCount[topQueue] >= marriage.getLocationSlots().get(topQueue)) {
    					q.pop();
    				}
    			}
    			else{
    				int current = marriage.getEmployeeMatching().get(highest_preference);
    				for(int j = 0; j < marriage.getLocationCount(); j++) {
    					if(marriage.getEmployeePreference().get(highest_preference).get(j) == topQueue) {
    						matching.set(highest_preference, topQueue);
    						
    						if(employeeCount[topQueue] >= marriage.getLocationSlots().get(topQueue)) {
    	    					q.pop();
    	    				}
    						hires[topQueue] = 1;
    						if(employeeCount[current] == marriage.getLocationSlots().get(current)) {
    							q.push(current);
    						}
    						employeeCount[current]--;
    						matching.set(current, -1);
    						jobs[topQueue][highest_preference] = 1;
    						employeeCount[topQueue]++;
    						if(employeeCount[topQueue] >= marriage.getLocationSlots().get(topQueue)) {
    	    					q.pop();
    	    				}
    						j = marriage.getLocationCount();
    					}
    					else if(marriage.getEmployeePreference().get(highest_preference).get(j) == current) {
    						j = marriage.getLocationCount();
    					}
    				}
    			}
    	}
    	marriage.setEmployeeMatching(matching);
        return marriage; /* TODO remove this line */
    }
}
