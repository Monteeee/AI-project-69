import java.util.concurrent.ThreadLocalRandom;

public class fiddle {
    public static void main(String[] args){
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3 + 1);
        System.out.println(randomNum);
    }

}
