import com.aparapi.Kernel;
import com.aparapi.Range;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main{

    public static class NBodyKernel extends Kernel{

        private final Range range;
        Random rnd=new Random(10);
        final float maxDist=50f;
        final float espSqr=1.0f;
        final float mass=5f;
        final float delT=0.5f;
        public Body[] bodies;

        public NBodyKernel(Range _range) {
            range = _range;
            bodies = new Body[range.getGlobalSize(0)];

            for (int body = 0; body < range.getGlobalSize(0); body++) {

                // get the 3D dimensional coordinates
                float theta = (float) (rnd.nextFloat()*Math.PI*2);
                float phi = (float) (rnd.nextFloat() * Math.PI * 2);
                float radius = (float) (rnd.nextFloat() * Math.PI * maxDist);
                float x=(float) (radius * Math.cos(theta) * Math.sin(phi));
                float y=(float) (radius * Math.sin(theta) * Math.sin(phi));
                float z= (float) (radius * Math.cos(phi));
                //divide
                if((body%2)==0) {
                    x+=maxDist*1.5;
                }
                else {
                    x-=maxDist*1.5;
                }
                bodies[body] = new Body(x, y, z);
            }

            //Body.allBodies = bodies;
        }
        public Body[] getBodys(){
            return this.bodies;
        }
         public void run() {
            final int gid = getGlobalId();
            final int count=getGlobalSize();
            final float myPosx=bodies[gid].getX();
            final float myPosy=bodies[gid].getY();
            final float myPosz=bodies[gid].getZ();
            float accx=0.f;
            float accy=0.f;
            float accz=0.f;
            for(int i=0;i<count;i++){
               final float dx=bodies[i].getX()-myPosx;
               final float dy=bodies[i].getY()-myPosy;
               final float dz=bodies[i].getZ()-myPosz;
               final float invDist=rsqrt((dx*dx)+(dy*dy)+(dz*dz)+espSqr);
               final float s= mass*invDist*invDist*invDist;
               accx+=(s*dx);
               accy+=(s*dy);
               accz+=(s*dz);
            }
             accx = accx * delT;
             accy = accy * delT;
             accz = accz * delT;
             bodies[gid].setX(myPosx+bodies[gid].getVx()*delT+(accx*.5f*delT));
             bodies[gid].setY(myPosy+bodies[gid].getVy()*delT+(accy*.5f*delT));
             bodies[gid].setZ(myPosz+bodies[gid].getVz()*delT+(accz*.5f*delT));
             bodies[gid].setVx(accx+bodies[gid].getVx());
             bodies[gid].setVy(accy+bodies[gid].getVy());
             bodies[gid].setVz(accz+bodies[gid].getVz());

            //bodies[body].setX(myPosx+myPosy+myPosz);
        }
    };

    public static void writeToFile(int size,Body[]copy,int first,int steps) throws IOException {
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
            outputWritter.write(Float.toString(copy[i].getX()));
            outputWritter.newLine();
            outputWritter.write(Float.toString(copy[i].getY()));
            outputWritter.newLine();
            outputWritter.write(Float.toString(copy[i].getZ()));
            outputWritter.newLine();
        }
        outputWritter.flush();
        for(int v=0;v<size;v++) {
            outputWritter.write(Float.toString(copy[v].getVx()));
            outputWritter.newLine();
            outputWritter.write(Float.toString(copy[v].getVy()));
            outputWritter.newLine();
            outputWritter.write(Float.toString(copy[v].getVz()));
            outputWritter.newLine();
        }
        outputWritter.flush();
        outputWritter.close();
    }



    public static void main(String _args[]) throws IOException {
        final int size=4096;
        final int nRep=8;
        final int print=0;
        final int steps=1000;
        Body[]copy;
        final int test=0;
        float[] coordinates=new float[size*3];
        float[] velocitys=new float[size*3];
        final int tSize=size*3;
        final float mass = 5f;
        final float delT = 0.5f;
        final float espSqr = 1.0f;
        Random rnd=new Random();
        final float maxDist = 50f;
        for(int teste=0;teste<nRep;teste++) {
            double kernelTimeI=0;
            double kernelTimeF=0;
            if(test==0){
                final NBodyKernel kernel = new NBodyKernel(Range.create(size));
                kernelTimeI=System.nanoTime();
                for(int step=0;step<steps;step++) {
                    if(print==1){
                        if(step==0){
                            writeToFile(size,copy,1,steps) ;
                        }
                        else {
                            writeToFile(size,copy,0,steps);
                        }
                    }
                    kernel.execute(kernel.range);
                    copy=kernel.bodies;
                }
                kernelTimeF=System.nanoTime();
                if(teste==0){
                    System.out.println(kernel.getTargetDevice());
                }
                System.out.println((kernelTimeF-kernelTimeI));
                kernel.dispose();
            }
            if(test==1){
                Body[]bodies=new Body[size];
               for(int i=0;i<size;i++){
                   float theta = (float) (rnd.nextFloat() * Math.PI * 2);
                   float phi = (float) (rnd.nextFloat() * Math.PI * 2);
                   float radius = (float) (rnd.nextFloat() * Math.PI * maxDist);
                   float x = (float) (radius * Math.cos(theta) * Math.sin(phi));
                   float y = (float) (radius * Math.sin(theta) * Math.sin(phi));
                   float z = (float) (radius * Math.cos(phi));
                   bodies[i]=new Body(x,y,z);
               }
                int counter=0;
                for(Body b:bodies){
                    coordinates[counter]=b.getX();
                    velocitys[counter]=b.getVx();
                    counter++;
                    coordinates[counter]=b.getY();
                    velocitys[counter]=b.getVy();
                    counter++;
                    coordinates[counter]=b.getZ();
                    velocitys[counter]=b.getVz();
                    counter++;
                }

                Kernel kernel = new Kernel() {
                    public void run() {
                        final int gid = getGlobalId();
                        final int count = tSize;
                        final int mine = gid * 3;
                        float accx = 0.f;
                        float accy = 0.f;
                        float accz = 0.f;
                        final float myPosx = coordinates[mine];
                        final float myPosy = coordinates[mine + 1];
                        final float myPosz = coordinates[mine + 2];
                        for (int i = 0; i < count; i += 3) {
                            final float dx = coordinates[i] - myPosx;
                            final float dy = coordinates[i + 1] - myPosy;
                            final float dz = coordinates[i + 2] - myPosz;
                            final float invDist = rsqrt((dx * dx) + (dy * dy) + (dz * dz) + espSqr);
                            final float s = mass * invDist * invDist * invDist;
                            accx = accx + (s * dx);
                            accy = accy + (s * dy);
                            accz = accz + (s * dz);
                        }
                        accx = accx * delT;
                        accy = accy * delT;
                        accz = accz * delT;
                        coordinates[mine] = myPosx + (velocitys[mine] * delT) + (accx * .5f * delT);
                        coordinates[mine + 1] = myPosy + (velocitys[mine + 1] * delT) + (accy * .5f * delT);
                        coordinates[mine + 2] = myPosz + (velocitys[mine + 2] * delT) + (accz * .5f * delT);

                        velocitys[mine] += accx;
                        velocitys[mine + 1] += accy;
                        velocitys[mine + 2] += accz;
                    }
                };
                kernelTimeI=System.nanoTime();
                for(int step=0;step<steps;step++){
                    kernel.execute(size);
                }
                kernelTimeF=System.nanoTime();
                System.out.println(kernelTimeF-kernelTimeI);
            }

        }

        //final Main kernel = new Main(bodyCount);

    }
}