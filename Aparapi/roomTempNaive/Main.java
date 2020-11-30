import com.aparapi.Kernel;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String []args) throws IOException {
        //size of 1 line of the square matrix/size of simulation
        final int size = 2048;
        final int nRep = 8;
        final int test = 9;
        final int print = 0;
        Random rnd = new Random(10);
        double kernelTimeI = 0;
        double kernelTimeF = 0;
        for (int rep = 0; rep < nRep; rep++) {
            final int rooml = size;
            final int roomw = size;
            float[] temp = new float[rooml * roomw];
            int phase[] = new int[rooml * roomw];
            int steps = 100;
            int cpuPhase=0;
            roomTemperature(25, temp, size * size);
            assignPhase(phase, size);
            //Blue phase
            Kernel blueKernel = new Kernel() {
                public void run() {
                    final int gid = getGlobalId();
                    if (phase[gid] == 1) {
                        //Borda inferior
                        if (0 <= gid && gid < size) {
                            //Caso normal
                            if (gid != 0 && gid != size - 1) {
                                temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]);
                            }
                            //inferior esquerdo
                            else if (gid == 0) {
                                temp[gid] += 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]);
                            }
                            //inferior direito
                            else //(gid==size-1)
                            {
                                temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]);
                            }
                        }
                        //Borda superior
                        else if ((size * size) - size <= gid && gid < size * size) {
                            //caso normal
                            if (gid != (size * size) - size && gid != (size * size) - 1) {
                                temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                            }
                            //superior esquerdo
                            else if (gid == (size * size) - size) {
                                temp[gid] += 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                            }
                            //superior direito
                            else //(gid==(size*size)-1)
                            {
                                temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                            }
                        }
                        //Borda Esquerda
                        else if (gid % size == 0) {
                            temp[gid] += 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                        }
                        //Borda Direita
                        else if ((gid + 1) % size == 0) {
                            temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                        }
                        //Resto
                        else {
                            temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                        }
                    } else {
                        temp[gid] = temp[gid];
                    }

                }
            };
            Kernel redKernel = new Kernel() {
                public void run() {
                    final int gid = getGlobalId();
                    if (phase[gid] == 0) {
                        //Borda inferior
                        if (0 <= gid && gid < size) {
                            //Caso normal
                            if (gid != 0 && gid != size - 1) {
                                temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]);
                            }
                            //inferior esquerdo
                            else if (gid == 0) {
                                temp[gid] += 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]);
                            }
                            //inferior direito
                            else //(gid==size-1)
                            {
                                temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]);
                            }
                        }
                        //Borda superior
                        else if ((size * size) - size <= gid && gid < size * size) {
                            //caso normal
                            if (gid != (size * size) - size && gid != (size * size) - 1) {
                                temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                            }
                            //superior esquerdo
                            else if (gid == (size * size) - size) {
                                temp[gid] += 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                            }
                            //superior direito
                            else //(gid==(size*size)-1)
                            {
                                temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                            }
                        }
                        //Borda Esquerda
                        else if (gid % size == 0) {
                            temp[gid] += 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                        }
                        //Borda Direita
                        else if ((gid + 1) % size == 0) {
                            temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                        }
                        //Resto
                        else {
                            temp[gid] += 0.25 * (temp[gid - 1] - temp[gid]) + 0.25 * (temp[gid + 1] - temp[gid]) + 0.25 * (temp[gid + size] - temp[gid]) + 0.25 * (temp[gid - size] - temp[gid]);
                        }
                    } else {
                        temp[gid] = temp[gid];
                    }

                }
            };
            kernelTimeI = System.nanoTime();
            for (int i = 0; i < steps; i++) {
                if (i == 20) {
                    int explo = (int) ((size * size) / 2) + (size / 2);
                    temp[explo] = 5000;
                }
                cpuPhase=1;
                cpuPhase=0;
                redKernel.execute(size * size);
                blueKernel.execute(size * size);
                if(print==1){
                    writeRoomTemp(temp,i,size,steps);
                }
            }
            if(rep==0){
                 System.out.println(blueKernel.getTargetDevice());
            }
            kernelTimeF = System.nanoTime();
            System.out.println(kernelTimeF - kernelTimeI);
        }
    }

    public static void writeRoomTemp(float[]temp,int i,int size,int steps) throws IOException {
        String filename="/home/bomberman/Desktop/roomt/tempLog.txt";
        BufferedWriter outputWritter= new BufferedWriter(new FileWriter(filename,true));
        int tSize=size*size;
        if(i==0){
            outputWritter.write(Integer.toString(size));
            outputWritter.newLine();
            outputWritter.write(Integer.toString(steps));
            outputWritter.newLine();
            outputWritter.flush();
        }
        for(int p=0;p<tSize;p++){
            outputWritter.write(Float.toString(temp[p]));
            outputWritter.newLine();
            outputWritter.flush();
        }
        outputWritter.close();
    }
    public static void roomTemperature(float t,float[]temp,int size){
        for(int i=0;i<size;i++){
            temp[i]=t;

        }
    }
    public static void cpuTemperature(float[]temp,int size,int[]phase,int phaseStatus){
        for(int i=0;i<size*size;i++){
            if(phase[i]==phaseStatus){
                //Borda inferior
                if(0<=i&&i<size){
                    //Caso normal
                    if(i!=0&&i!=size-1){
                        temp[i]+=0.25*(temp[i-1]-temp[i])+0.25*(temp[i+1]-temp[i])+0.25*(temp[i+size]-temp[i]);
                    }
                    //inferior esquerdo
                    else if(i==0){
                        temp[i]+=0.25*(temp[i+1]-temp[i])+0.25*(temp[i+size]-temp[i]);
                    }
                    //inferior direito
                    else if(i==size-1){
                        temp[i]+=0.25*(temp[i-1]-temp[i])+0.25*(temp[i+size]-temp[i]);
                    }
                }
                //Borda superior
                else if ((size*size)-size<=i&&i<size*size){
                    //caso normal
                    if (i!=(size*size)-size&&i!=(size*size)-1){
                        temp[i]+=0.25*(temp[i-1]-temp[i])+0.25*(temp[i+1]-temp[i])+0.25*(temp[i-size]-temp[i]);
                    }
                    //superior esquerdo
                    else if (i==(size*size)-size){
                        temp[i]+=0.25*(temp[i]-temp[i])+0.25*(temp[i-size]-temp[i]);
                    }
                    //superior direito
                    else if(i==(size*size)-1){
                        temp[i]+=0.25*(temp[i-1]-temp[i])+0.25*(temp[i-size]-temp[i]);
                    }
                }
                //Borda Esquerda
                else if(i%size==0){
                    temp[i]+=0.25*(temp[i+1]-temp[i])+0.25*(temp[i+size]-temp[i])+0.25*(temp[i-size]-temp[i]);
                }
                //Borda Direita
                else if((i+1)%size==0){
                    temp[i]+=0.25*(temp[i-1]-temp[i])+0.25*(temp[i+size]-temp[i])+0.25*(temp[i-size]-temp[i]);
                }
                //Resto
                else {
                    temp[i]+=0.25*(temp[i-1]-temp[i])+0.25*(temp[i+1]-temp[i])+0.25*(temp[i+size]-temp[i])+0.25*(temp[i-size]-temp[i]);
                }
            }
        }
    }
    public static void assignPhase(int[]phase,int size){
        int diags=size%2;
        //red phase (init)
        for(int i=0;i<size*size;i++){
            phase[i]=0;
        }
        //blue phase
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(i%2==0&&j%2==0){
                    phase[i*size+j]=1;
                }
                if(i%2!=0&&j%2!=0){
                    phase[i*size+j]=1;
                }
            }
        }
    }

}
