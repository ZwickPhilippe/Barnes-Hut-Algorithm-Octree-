import java.awt.*;
// This class represents vectors in a 3D vector space.
public class Vector3 {

    private double x;
    private double y;
    private double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Returns the sum of this vector and vector 'v'.
    public Vector3 plus(Vector3 v) {

        double newX = this.x + v.x;
        double newY = this.y + v.y;
        double newZ = this.z + v.z;
        return new Vector3(newX, newY, newZ);
    }

    // Returns the product of this vector and 'd'.
    public Vector3 times(double d) {

        double newX = this.x * d;
        double newY = this.y * d;
        double newZ = this.z * d;

        return new Vector3(newX, newY, newZ);
    }

    // Returns the sum of this vector and -1*v.
    public Vector3 minus(Vector3 v) {

        double newX = this.x - v.x;
        double newY = this.y - v.y;
        double newZ = this.z - v.z;
        return new Vector3(newX, newY, newZ);
    }

    // Returns the Euclidean distance of this vector
    // to the specified vector 'v'.
    public double distanceTo(Vector3 v) {

        double newX = this.x - v.x;
        double newY = this.y - v.y;
        double newZ = this.z - v.z;

        return Math.sqrt(newX * newX + newY * newY + newZ * newZ);

    }

    // Returns the length (norm) of this vector.
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    // Normalizes this vector: changes the length of this vector such that it becomes 1.
    // The direction and orientation of the vector is not affected.
    public void normalize() {
        double length = length();
        this.x = (this.x / length);
        this.y = (this.y / length);
        this.z = (this.z / length);
    }

    // Draws a filled circle with a specified radius centered at the (x,y) coordinates of this vector
    // in the existing StdDraw canvas. The z-coordinate is not used.
   /* public void drawAsDot(double radius, Color color) {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(this.x, this.y, radius);
    }*/



    // Returns the coordinates of this vector in brackets as a string
    // in the form "[x,y,z]", e.g., "[1.48E11,0.0,0.0]".
    public String toString() {

        return "[" + this.x + "," + this.y + "," + this.z + "]";

    }

    public void drawAsDot(double radius, Color color) {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(this.x, this.y, radius);
    }

    //Getters and Setters

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isEqualTo(Vector3 vector) {
        return this.x == vector.x && this.y == vector.y && this.z == vector.z;
    }

}

