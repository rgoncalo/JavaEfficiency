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
        final int steps = 2000;
        final float delT = 0.5f;
        final float espSqr = 1.0f;
        final float maxDist = 50f;
        final float mass = 5f;
        Lapi lapi=new Lapi();
        for (int teste = 0; teste < nRep; teste++) {
            float[] xyz = new float[size * 3];
            float[] positions = new float[size * 3];
            float[] velocitys = new float[size * 3];
            final int tSize = size * 3;
            //populate
            for (int i = 0; i < tSize; i += 3) {

                float theta = (float) (rnd.nextFloat() * Math.PI * 2);
                float phi = (float) (rnd.nextFloat() * Math.PI * 2);
                float radius = (float) (rnd.nextFloat() * Math.PI * maxDist);
                xyz[i] = (float) (radius * Math.cos(theta) * Math.sin(phi));
                xyz[i + 1] = (float) (radius * Math.sin(theta) * Math.sin(phi));
                xyz[i + 2] = (float) (radius * Math.cos(phi));
                //divide
                if (i == 0) {

                } else if ((i % 2) == 0) {
                    xyz[i] += maxDist * 1.5;
                } else {
                    xyz[i] -= maxDist * 1.5;
                }
            }
            positions=lapi.convertFromVertical(size,3,xyz);
            float[] finalPositions = positions;
            Kernel kernel = new Kernel() {
                public void run() {
                    final int gid = getGlobalId();
                    float accx = 0.f;
                    float accy = 0.f;
                    float accz = 0.f;
                    final float myPosx = finalPositions[gid];
                    final float myPosy = finalPositions[gid + size];
                    final float myPosz = finalPositions[gid + 2 * size];

                    //calculate my forces of bodies on me
                    for (int i = 0; i < size; i++) {
                        float x = finalPositions[i] - myPosx;
                        float y = finalPositions[i + size] - myPosy;
                        float z = finalPositions[i + 2 * size] - myPosz;
                        float v = rsqrt((x * x) + (y * y) + (z * z) + espSqr);
                        float res = mass * v * v * v;
                        accx += res * x;
                        accy += res * y;
                        accz += res * z;
                    }
                    accx = accx * delT;
                    accy = accy * delT;
                    accz = accz * delT;
                    finalPositions[gid] = myPosx + (velocitys[gid] * delT) + (accx * .5f * delT);
                    finalPositions[gid + size] = myPosy + (velocitys[gid + size] * delT) + (accy * .5f * delT);
                    finalPositions[gid + 2 * size] = myPosz + (velocitys[gid + 2 * size] * delT) + (accz * .5f * delT);

                    velocitys[gid] += accx;
                    velocitys[gid + size] += accy;
                    velocitys[gid + 2 * size] += accz;
                }
            };
            kernelTimeI = System.nanoTime();
            for (int ctep = 0; ctep < steps; ctep++) {
                //System.out.println("Step n." + ctep + " complete");
                if (print == 1) {
                    if (ctep == 0) {
                        writeToFile2(size, positions, velocitys, 1, steps);
                    } else {
                        writeToFile2(size, positions, velocitys, 0, steps);
                    }
                }

                kernel.execute(size);
            }
            kernelTimeF = System.nanoTime();
            if (teste == 0) {
                System.out.println(kernel.getTargetDevice());
            }
            System.out.println((kernelTimeF - kernelTimeI));
            kernel.dispose();
        }
    }
    public static void writeToFile2(int size,float[]positions,float[]velocity,int first,int steps) throws IOException {
        String filename="/home/bomberman/Desktop/animes/bodysLog.txt";
        BufferedWriter outputWritter= new BufferedWriter(new FileWriter(filename,true));
        int tSize=size*3;
        int ycounter=0;
        int zcounter=0;
        if(first==1){
            outputWritter.write(Integer.toString(tSize));
            outputWritter.newLine();
            //steps
            outputWritter.write(Integer.toString(steps));
            outputWritter.newLine();
            outputWritter.flush();
        }
        //write positions
        for(int i=0;i<size;i++,ycounter++,zcounter++) {
            //write X
            outputWritter.write(Float.toString(positions[i]));
            outputWritter.newLine();
            //write Y
            outputWritter.write(Float.toString(positions[ycounter+size]));
            outputWritter.newLine();
            outputWritter.flush();
            //write Z
            outputWritter.write(Float.toString(positions[zcounter+size*2]));
            outputWritter.newLine();
            outputWritter.flush();
        }
        //write velocitys
        ycounter=0;
        zcounter=0;
        for(int v=0;v<size;v++,ycounter++,zcounter++){
            //write Vx
            outputWritter.write(Float.toString(velocity[v]));
            outputWritter.newLine();
            //write Vy
            outputWritter.write(Float.toString(velocity[ycounter+size]));
            outputWritter.newLine();
            outputWritter.flush();
            //write Vz
            outputWritter.write(Float.toString(velocity[zcounter+size*2]));
            outputWritter.newLine();
            outputWritter.flush();
        }
        outputWritter.close();
    }

}
