public class State{
    Variable vars[];
    int rows;
    int goal[] ={0,0,0,0,0,0,0,0,0,0};

    public State(int r){
        rows=r;
        vars=new Variable[r*10];
        /*int values[] = {-1,6,2,0,-1,-1,-1,8,5,7
                        ,-1,0,1,7,8,-1,-1,-1,9,-1
                        ,-1,4,-1,-1,2,-1,3,7,-1,8};
        for(int i=0; i<vars.length; i++){
            vars[i] = new Variable();
            vars[i].value = values[i];
        }
        int goals[] = {13,10,8,7,19,16,11,19,15,17};
        for(int i=0; i<10; i++){
            goal[i] = goals[i];
        }*/
    }

    public void print(){
        for (int i=0; i<rows; i++){
            for (int j=0; j<10; j++)
                if (vars[(i*10)+j].value>=0)
                    System.out.print(vars[(i*10)+j].value+"  ");
                else 
                    System.out.print("_  ");
            System.out.println();
        }
        System.out.println("-----------------------------");
        for (int i=0; i<10; i++)
            System.out.print(goal[i]+" ");
    }

    public State constraintCopy(){
        //a function that returns a copy of the state with the domains satisfying the constraints
        State newState= new State(this.rows);
        int newGoal[]= new int[10];
        for (int i=0; i<10; i++)
            newGoal[i]=goal[i];
        newState.goal=newGoal;
        for (int i=0; i<rows*10; i++)
            newState.vars[i]=new Variable();
        for (int i=0; i<rows*10; i++)
            if (vars[i].value>=0)
                newState.setVar(i, vars[i].value);
        return newState;
    }

    public State copy(){
        //a function that returns a normal copy of the state
        State newState= new State(this.rows);
        for (int i=0; i<10; i++)
            newState.goal[i]=this.goal[i];
        for (int i=0; i<rows*10; i++){
            Variable newVar=new Variable();
            newVar.domSize=vars[i].domSize;
            newVar.value=this.vars[i].value;
            for (int j=0; j<10; j++)
                newVar.domain[j]=this.vars[i].domain[j];
            newState.vars[i]=newVar;
        }
        return newState;
    }
    public boolean isSafe(int index, int val){
      //Row constraint: 
        for(int i=0; i<10; i++){
            int j = (index/10) * 10 + i;
            if(j == index)
                continue;
            if(vars[j].value == val){
            //System.out.println("row cons: the value of index " + j + " is the same as " + index + "which is" + val);
                return false;
            } 
        }
      
      //Connecting cells constraint:
      //Checking the cell on top, skips the first row
        if (index>=10){
            if (vars[index-10].value == val)
            //System.out.println("the upper cell is the same");
                return false;
            if (index%10!=9 && vars[index-10+1].value == val)
                return false;
            if (index%10!=0 && vars[index-10-1].value == val)
                return false;
        }
      //Checking the cell under index, skips the last row
        if (index<(rows-1)*10){
            if (vars[index+10].value == val)         
                return false;
            if (index%10!=9 && vars[index+10+1].value == val)
                return false;
            if (index%10!=0 && vars[index+10-1].value == val)
                return false;
        }
      
      //Sum constraint: 
        int sum=val, unassigned=0; //the current sum
        for (int i=index%10; i<rows*10; i+=10){
            if(i!=index){
                if (vars[i].value>=0)
                   sum+=vars[i].value;
                else
                    unassigned++;
            }
        }
        int sumDiff=goal[index%10]-sum; //how much left to reach the goal
        if(unassigned == 1){
            for (int i=index%10; i<rows*10; i+=10){
                if (index!=i && vars[i].value<0){
                if (sumDiff>10){
                    return false;
                }
                break;
                }
            }
        }
        if(sum>goal[index%10])
            return false;

        return true;
    }
    public void setVar(int index,int v){
        //a function that sets the variable v in the index i and change the
        //domain of the affected variables based on the constraints
        vars[index].value=v;

        for (int vIndex=0; vIndex<rows*10; vIndex++){
            if (vIndex!=index)
                for (int dIndex=0; dIndex<10; dIndex++){
                    if (vars[vIndex].domain[dIndex]>=0 && !isSafe(vIndex, vars[vIndex].domain[dIndex])){
                        vars[vIndex].domSize--;
                        vars[vIndex].domain[dIndex]=-1;
                    }
                }
        }
    }
}