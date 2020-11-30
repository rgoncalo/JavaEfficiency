public final class Body{
//   public static Body[] allBodies;
    float x, y, z, vx, vy, vz;
    public Body(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx=0;
        this.vy=0;
        this.vz=0;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public void setX(float _x) {
        x = _x;
    }

    public void setY(float _y) {
        y = _y;
    }

    public void setZ(float _z) {
        z = _z;
    }

    public float getVx(){
        return vx;
    }

    public float getVy(){
        return vy;
    }

    public float getVz(){
        return vz;
    }

    public void setVx(float vx) {
        this.vx=vx;
    }

    public void setVy(float vy) {
        this.vy=vy;
    }

    public void setVz(float vz) {
        this.vz=vz;
    }

};