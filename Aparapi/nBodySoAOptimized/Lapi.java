public class Lapi {
    public Lapi(){

    }
    public void set(int index,float value,float[]values){
        values[index]=value;
    }

    public float[] convertFromVertical(int sizeOfSimulation,int sizeOfObject,float[]array) {
        float[] aux = new float[sizeOfSimulation * sizeOfObject];
        int initiate;
        int objectCounter;
        int run=1;
        for(int i=0;i<sizeOfObject;i++){
            initiate=i*sizeOfSimulation;
            objectCounter=i;
            for(int x=initiate;x<sizeOfSimulation*run;x++){
                aux[x]=array[objectCounter];
                objectCounter+=sizeOfObject;
            }
            run++;
        }
        return aux;
    }
}
