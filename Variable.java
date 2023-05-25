public class Variable{
    int value=-1; //-1 means unassigned yet
    int domain[] = {0,1,2,3,4,5,6,7,8,9};
    int domSize=10;

    /*public Variable copy(){
        int newDomain[]=new int[10];
        for (int i=0; i<10; i++)
            newDomain[i]=domain[i];
        Variable newVar= new Variable();
        newVar.value=this.value;
        newVar.domain=newDomain;
        newVar.domSize=this.domSize;
        return newVar;
    }*/
}