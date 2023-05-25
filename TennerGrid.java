import java.util.*;

class TennerGrid{
    public static void main(String[] args) {
        //create and print the game
        State game=generator(5);
        State gameState1=game.constraintCopy();
        //State game = new State(3);
        State gameState2 = game.constraintCopy();
        State gameState3 = game.constraintCopy();
        System.out.println("\n\nGame:");
        game.print();
        long startTime = System.currentTimeMillis();
        System.out.println("\nSolve by BackTrack:");
        boolean sol = BT(0,gameState1);
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time in milli seconds: "+estimatedTime);
        //gameState1.print();
        System.out.println("\n"+sol);
        
        
        System.out.println("\nSolve by BackTrack + MRV:");
        startTime = System.currentTimeMillis();
        sol = BT(0,gameState3);
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("\nTime in milli seconds: "+estimatedTime);
        //gameState1.print();
        System.out.println("\n"+sol);

        
        System.out.println("\nSolve by Forward Checking:");
        startTime = System.currentTimeMillis();
        boolean solved=backtrack(0, gameState2);
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time in milli seconds: "+estimatedTime);
        System.out.println("\n"+solved);
    }

    public static State generator(int size){
        State gen=new State(size);
        int row[]={0,1,2,3,4,5,6,7,8,9};

        //Fill the 1st row with numbers 0-9 randomly shuffled
        shuffle(row);
        for (int i=0; i<10; i++){
            gen.vars[i]=new Variable();
            gen.vars[i].value=row[i];
        }

        //Fill the other rows randomly, but making sure every element in the row is
        //not equal to the element of the same index in the previos row
        for (int i=10; i<size*10; i+=10){
            while(true){
                shuffle(row);
                int j;
                for (j=0; j<10; j++){
                    if (row[j]==gen.vars[j+i-10].value)
                        break;
                    if (j>0 && row[j]==gen.vars[j+i-10-1].value)
                        break;
                    if (j<9 && row[j]==gen.vars[j+i-10+1].value)
                        break;
                }
                if (j==10)
                    break;
            }
            for (int j=0; j<10; j++){
                gen.vars[i+j]=new Variable();
                gen.vars[i+j].value=row[j];
            }
        }
        
        //calculate the goal row (sum of the previos rows)
        for (int i=0; i<10; i++)
            for (int j=0; j<size; j++)
                gen.goal[i]+=gen.vars[(j*10)+i].value;
        
        //print a sample solution
        gen.print();

        //hide some numbers
        Random random=new Random();
        int h,hide=random.nextInt(size*10/2);
        hide+=size*10/4;
        for (int i=0; i<hide; i++){
            h=random.nextInt(size*10);
            gen.vars[h].value=-1;
        }
        return gen;
    }
   public static boolean BTMRV(int index, State state){
      //We reached the end of the grid, the problem is totally solved
      int unassignedVars = 0;
      for(int i=0; i<state.rows*10; i++){
         if(state.vars[i].value == -1)
            unassignedVars++;
      }
      if(unassignedVars == 0)
         return true;
      
      //Choose the variable with the Minimum Remaining Values:
      int maxCons = 0;
      int ind = -1;
      for(int i=0; i<state.rows*10; i++){
         if(state.vars[i].value == -1){
            //int localCons;
            int countCons = 0;
            for(int j=0; j<10; j++){
               //int temp = checks;
               if(!state.isSafe(index, i))
                  countCons++;
               //checks = temp;
            
            }
            if(countCons > maxCons){
               maxCons = countCons;
               ind = i;
            }
         }
      }
      
      
      for (int i=0; i<10; i++){
         //Keep a copy in case we fail and backtrack
         State extraState = state.copy(); 
         
         //A possible value from the domain is found, test it then assign it
         if(state.isSafe(index, i)){
            state.vars[index].value = i;
            //The new value is assigned, move to the next index
            if (BTMRV(ind, state))
               return true;
            else{
               //FAIL in assigning the next index: BrackTrack
               state = extraState;
            }
         }
      }
      return false;   
      
   }
    
    //Solve by BACKTRACKING:
   public static boolean BT(int index, State state){
      //We reached the end of the grid, the problem is totally solved
      if (index>=state.rows*10){
         state.print();
         return true;
      }
      
      //The variable is assigned, move to the next index
      if (state.vars[index].value>=0)
         return BT(index+1, state);
         
      for (int i=0; i<10; i++){
         //Keep a copy in case we fail and backtrack
         State extraState = state.copy(); 
         
         //A possible value from the domain is found, test it then assign it
         if(state.isSafe(index, i)){
            state.vars[index].value = i;
            //The new value is assigned, move to the next index
            if (BT(index+1, state))
               return true;
            else{
               //FAIL in assigning the next index: BrackTrack
               state = extraState;
            }
         }
      }
      return false;
   } 
      
    //solve the problem by simple backtracking
    public static boolean backtrack(int index, State state){
        if (index>=state.rows*10){
            state.print();
            return true;
        }
        if (state.vars[index].value>=0)
            return backtrack(index+1, state);
        for (int i=0; i<10; i++){
            if (state.vars[index].domain[i]<0)
                continue;
            State extraState=state.copy(); //keep a copy in case we fail and backtrack
            //System.out.println("setting "+state.vars[index].domain[i]+" in "+index);
            state.setVar(index, state.vars[index].domain[i]);
            if (!validDomains(state)){
                //System.out.println(state.vars[index].domain[i]+" not valid in "+index);
                state=extraState;
                continue;
            }
            if (backtrack(index+1, state))
                return true;
            else{
                state=extraState;
                //System.out.println("backtracking..");
            }
        }
        return false;
    }

    public static boolean validDomains(State state){
        for (int i=0; i<state.rows*10; i++)
            if  (state.vars[i].domSize<=0)
                return false;
        return true;
    }

    public static void shuffle(int[] array){
    int index, temp;
    Random random = new Random();
    for (int i = array.length - 1; i > 0; i--)
    {
        index = random.nextInt(i + 1);
        temp = array[index];
        array[index] = array[i];
        array[i] = temp;
    }
}
}