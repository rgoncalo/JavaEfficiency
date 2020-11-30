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
        final int print = 1;
        Random rnd = new Random(10);
        double kernelTimeI = 0;
        double kernelTimeF = 0;
        final int steps = 2000;
        final float delT = 0.5f;
        final float espSqr = 1.0f;
        final float maxDist = 50f;
        final float mass = 5f;
        for (int teste = 0; teste < nRep; teste++) {
            float[] xyz = new float[size * 3];
            float[] vxyz = new float[size * 3];
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
                if ((i % 2) == 0) {
                    xyz[i] += maxDist * 1.5;
                } else {
                    xyz[i] -= maxDist * 1.5;
                }
            }
            Kernel kernel = new Kernel() {
                public void run() {
                    final int gid = getGlobalId();
                    final int count = tSize;
                    final int mine = gid * 3;
                    float accx = 0.f;
                    float accy = 0.f;
                    float accz = 0.f;
                    final float myPosx = xyz[mine];
                    final float myPosy = xyz[mine + 1];
                    final float myPosz = xyz[mine + 2];
                    for (int i = 0; i < count; i += 3) {
                        final float dx = xyz[i] - myPosx;
                        final float dy = xyz[i + 1] - myPosy;
                        final float dz = xyz[i + 2] - myPosz;
                        final float invDist = rsqrt((dx * dx) + (dy * dy) + (dz * dz) + espSqr);
                        final float s = mass * invDist * invDist * invDist;
                        accx = accx + (s * dx);
                        accy = accy + (s * dy);
                        accz = accz + (s * dz);
                    }
                    accx = accx * delT;
                    accy = accy * delT;
                    accz = accz * delT;
                    xyz[mine] = myPosx + (vxyz[mine] * delT) + (accx * .5f * delT);
                    xyz[mine + 1] = myPosy + (vxyz[mine + 1] * delT) + (accy * .5f * delT);
                    xyz[mine + 2] = myPosz + (vxyz[mine + 2] * delT) + (accz * .5f * delT);

                    vxyz[mine] += accx;
                    vxyz[mine + 1] += accy;
                    vxyz[mine + 2] += accz;
                }
            };
            kernelTimeI = System.nanoTime();
            for (int ctep = 0; ctep < steps; ctep++) {
                if (print == 1) {
                    if (ctep == 0) {
                        writeToFile(tSize, xyz, vxyz, 1, steps);
                    } else {
                        writeToFile(tSize, xyz, vxyz, 0, steps);
                    }
                }
                kernel.execute(tSize);
            }
            kernelTimeF = System.nanoTime();
            if (teste == 0) {
                System.out.println(kernel.getTargetDevice());
            }

            System.out.println((kernelTimeF - kernelTimeI));
            kernel.dispose();
        }
    }

    public static void writeToFile(int size,float[]positions,float[]velocity,int first,int steps) throws IOException {
        int counter=0;
        String filename="/home/bomberman/Desktop/animes/bodysLog.txt";
        BufferedWriter outputWritter= new BufferedWriter(new FileWriter(filename,true));
        if(first==1){
            outputWritter.write(Integer.toString(size));
            outputWritter.newLine();
            //steps
            outputWritter.write(Integer.toString(steps));
            outputWritter.newLine();
            outputWritter.flush();
        }
        for(int i=0;i<size;i++) {
            outputWritter.write(Float.toString(positions[i]));
            outputWritter.newLine();
        }
        outputWritter.flush();
        for(int v=0;v<size;v++) {
            outputWritter.write(Float.toString(velocity[v]));
            outputWritter.newLine();
        }
        outputWritter.flush();
        outputWritter.close();
    }


}
