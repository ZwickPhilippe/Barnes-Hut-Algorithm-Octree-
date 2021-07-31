import java.awt.*;
import java.util.Random;
import java.util.Scanner;

public class Simulation {

    public static final double AU = 150e9;

    public static void main(String[] args) {


        Scanner scanner = new Scanner(System.in);
        System.out.println("How many planets do you want to add? (recommended: 10000)");
        int planetAmount = scanner.nextInt();

        System.out.println("Option (1) Use centerOfOctant for ratio-calculation \n\rOption (2) Use centerOfGravity for ratio-calculation (other, currently used method for ration calculation)");

        boolean useCenterOfOctant=true;
        switch(scanner.nextInt()){
            case 1:
                useCenterOfOctant=true;
                break;
            case 2:
                useCenterOfOctant=false;
                break;
        }
        Octree octree = new Octree(useCenterOfOctant);

        Body sun = new Body("SUN", 2e36, 2e3, new Vector3(1, 1, 1), new Vector3(0, 0, 0), StdDraw.RED);
        octree.add(sun);
        //octree.add(new Body("SUN", 2e35, Double.MAX_VALUE, new Vector3(0, 0, 0), new Vector3(0, 0, 0), StdDraw.BLUE));



        System.out.println("Create planets randomly (1) \n\rCreate planets in 2 clusters (2)\n\rBoth (3)");
        switch ( scanner.nextInt() ) {
            case 1:
                createRandom(planetAmount,octree);
                break;
            case 2:
                Simulation.createGalaxyAround(planetAmount, sun, octree);
                break;
            case 3:
                Simulation.createGalaxyAround(planetAmount/2, sun, octree);
                createRandom(planetAmount/2,octree);
                break;
            default:
                createRandom(planetAmount,octree);
                break;
        }



        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(1000, 1000);
        StdDraw.setXscale(-2 * AU, 2 * AU);
        StdDraw.setYscale(-2 * AU, 2 * AU);
        StdDraw.clear(StdDraw.BLACK);

        long seconds = 0;
        int drawFrequency=4;

        System.out.println("Press KEY_UP to draw less often (times goes by faster) \n\rPress KEY_DOWN to draw more often (simulation runs smoother)\n\rPress i for additional information");
        while (true) {

            seconds++;

            if (seconds % (drawFrequency) == 0) {
                StdDraw.clear(StdDraw.BLACK);
                octree.drawTree();

                StdDraw.show();
            }

            if(StdDraw.isKeyPressed(38)){ //arrow up
                if(drawFrequency<99){
                    drawFrequency++;
                    System.out.println("Drawspeed is now: " + drawFrequency);
                }
            }
            if(StdDraw.isKeyPressed(40)){
                if(drawFrequency>2){
                    drawFrequency--;
                    System.out.println("Drawspeed is now: " + drawFrequency);
                }
            }
            if(StdDraw.isKeyPressed(73)){
                int size = octree.getTreeSize();
                System.out.println("Current size is: " + size);
                System.out.println("Planets that left the bounds " + (planetAmount+1-size));
            }
            octree.calculateForce(); //calculates force of new tree / adds them in a list
            System.out.println(octree.getEmptyNodes());
            octree.moveTree(); //moves the bodies
            octree.rebuildTree(); //rebuild the tree from list


        }
    }

    private static void createRandom(int size, Octree octree){
        Color[] colors = new Color[]{StdDraw.BLUE, StdDraw.GREEN, StdDraw.YELLOW, StdDraw.BOOK_LIGHT_BLUE, StdDraw.ORANGE, StdDraw.BOOK_LIGHT_BLUE, StdDraw.BOOK_RED};

        Random random = new Random();
        double mult = 2 * AU;
        for (int i = 0; i < size; i++) {
            octree.add(new Body("Body", Math.random() * 2e20, 12,
                    new Vector3((2 * random.nextDouble() - 1) * mult, (2 * random.nextDouble() - 1) * mult, 0),
                    new Vector3(50e6 * (2 * random.nextDouble() - 1) , 50e6 * (2 * random.nextDouble() - 1) , 0), colors[i%7]));
        }
    }

    private static void createGalaxyAround(int size, Body body, Octree octree) {
        Random random = new Random(42);
        Body upperSun = new Body("UpperSun", body.getMass() / 2, 50, new Vector3(body.getPosition().getX() + AU, body.getPosition().getY() + AU, 0), new Vector3(-50e5, 20e5, 0), StdDraw.BLUE);
        Body lowerSun = new Body("LowerSun", body.getMass() / 2, 50, new Vector3(body.getPosition().getX() - AU, body.getPosition().getY() - AU, 0), new Vector3(50e5, -20e5, 0), StdDraw.BLUE);
        octree.add(upperSun);
        octree.add(lowerSun);
        for (int i = 0; i < size / 2; i++) {
            octree.add(new Body("Body" + i,
                    (body.getMass() / 2e10) * random.nextDouble() + 2e5,
                    20,
                    new Vector3(body.getPosition().getX() + AU + (body.getPosition().getX() - AU / 2) * (2.5 * random.nextDouble() - 1),
                            body.getPosition().getY() + AU  + (body.getPosition().getY() - AU / 2) * (2.5 * random.nextDouble() - 1),
                            AU * (2 * random.nextDouble() - 1)),
                    new Vector3(20e6 * (random.nextDouble() - 2), 20e5 * (2 * random.nextDouble() - 1), 20e5 * (2 * random.nextDouble() - 1)),
                    StdDraw.GRAY));
        }

        for (int i = 0; i < size / 2; i++) {
            octree.add(new Body("Body" + i,
                    (body.getMass() / 2e10) * random.nextDouble() + 2e5,
                    20,
                    new Vector3(body.getPosition().getX() - AU + (body.getPosition().getX() - AU / 2) * (2.5 * random.nextDouble() - 1),
                            body.getPosition().getY() - AU  + (body.getPosition().getY() - AU / 2) * (2.5 * random.nextDouble() - 1),
                            AU * (2 * random.nextDouble() - 1)),
                    new Vector3(20e6 * (random.nextDouble() + 2), 20e4 * (random.nextDouble() + 2), 20e4 * (random.nextDouble() + 2)),
                    StdDraw.GRAY));
        }

    }
}
