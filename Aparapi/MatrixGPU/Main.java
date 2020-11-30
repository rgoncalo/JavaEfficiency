import com.aparapi.Kernel;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


//Matrix multiplication in GPU aparapi
public class Main {
    public static void main(String []args) throws IOException {
        final int size = 128;
        final int nRep = 8;
        final int print = 1;
        Random rnd = new Random(10);
        double kernelTimeI = 0;
        double kernelTimeF = 0;
        for (int run = 0; run < nRep; run++) {
            float[] matA = new float[size * size];
            float[] matB = new float[size * size];
            float[] matR = new float[size * size];
            fillMatrix(matA, size * size);
            fillMatrix(matB, size * size);
            Kernel kernel = new Kernel() {
                public void run() {
                    int gid = getGlobalId();
                    int line = gid / size;
                    int col = gid % size;
                    for (int i = 0; i < size; i++) {
                        matR[gid] += matA[line * size + i] * matB[i * size + col];
                    }
                }
            };
            kernelTimeI = System.nanoTime();
            kernel.execute(size * size);
            kernelTimeF = System.nanoTime();
            System.out.println(kernelTimeF - kernelTimeI);
            System.out.println(kernel.getTargetDevice());
            kernel.dispose();
            if(print==1) {
                printResults(size, matR);
            }
        }
    }

    public static void fillMatrix(float[] matrix,int size) {
        Random rnd=new Random(10);
        for(int i=0;i<size;i++){
            matrix[i]=rnd.nextFloat();
        }
    }

    public static void printResults(int size,float[]matR){
        int totalElements=size*size;
        for(int i=0;i<totalElements;i++){
            System.out.println(matR[i]);
        }
    }


}
