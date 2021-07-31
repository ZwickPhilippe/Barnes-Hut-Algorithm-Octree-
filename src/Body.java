import java.awt.*;

// This class represents celestial bodies like stars, planets, asteroids, etc..
public class Body {

    public static final double G = 6.6743e-11;

    private String name;
    private double mass;
    private double radius;
    private Vector3 position; // position of the center.
    private Vector3 currentMovement;
    private Color color; // for drawing the body.
    private Vector3 force;



    public Body(String name, double mass, double radius, Vector3 position, Vector3 currentMovement, Color color) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.position = position;
        this.currentMovement = currentMovement;
        this.color = color;
    }

    // Returns the distance between this body and the specified 'body'.
    public double distanceTo(Body body) {
        return this.position.distanceTo(body.position);
    }

    public static double getG() {
        return G;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getCurrentMovement() {
        return currentMovement;
    }

    public void setCurrentMovement(Vector3 currentMovement) {
        this.currentMovement = currentMovement;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setForce(Vector3 force) {
        this.force = force;
    }


    //Returns a vector representing the gravitational force exerted by 'body' on this body.
    //The gravitational Force F is calculated by F = G*(m1*m2)/(r*r), with m1 and m2 being the masses of the objects
    //interacting, r being the distance between the centers of the masses and G being the gravitational constant.
    //To calculate the force exerted on b1, simply multiply the normalized vector pointing from b1 to b2 with the
    //calculated force
    public Vector3 gravitationalForce(Body body) {
        Vector3 direction = body.position.minus(this.position);
        double distance = direction.length();
        direction.normalize();
        double force = (G * this.mass * body.mass) / (distance * distance);
        return direction.times(force);
    }

    public Vector3 gravitationalForce(double mass, Vector3 position){
        Vector3 direction = position.minus(this.position);
        double distance = direction.length();
        direction.normalize();
        double force = (G * this.mass * mass) / (distance * distance);
        return direction.times(force);
    }

    // Moves this body to a new position, according to the specified force vector 'force' exerted
    // on it, and updates the current movement accordingly.
    // (Movement depends on the mass of this body, its current movement and the exerted force)
    // Hint: see simulation loop in Simulation.java to find out how this is done
    public void move(Vector3 force) {
        Vector3 oldPosition = this.position;
        this.position = this.position.plus(force.times(1 / this.mass)).plus(currentMovement);
        this.currentMovement = this.position.minus(oldPosition);

    }


    public void move(){
        Vector3 oldPosition = this.position;
        this.position = this.position.plus(this.force.times(1 / this.mass)).plus(currentMovement);
        this.currentMovement = this.position.minus(oldPosition);
    }

    public void draw() {
        //this.position.drawAsDot(500000000, this.color);
        this.position.drawAsDot(1e9 * Math.log10(this.radius), this.color);
    }

    // Returns a string with the information about this body including
    // name, mass, radius, position and current movement. Example:
    // "Earth, 5.972E24 kg, radius: 6371000.0 m, position: [1.48E11,0.0,0.0] m, movement: [0.0,29290.0,0.0] m/s."
    public String toString() {
        return "" + this.name + ", " + this.mass + " kg, radius: " + this.radius + " m, position: " + this.position.toString() + " m, movement: " + this.currentMovement.toString() + " m/s.";
    }

    public String getName() {
        return this.name;
    }

    public Color getColor(){
        return this.color;
    }

}

