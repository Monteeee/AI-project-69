import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class fiddle {
    public static void main(String[] args){

        ArrayList<Integer> mylist = new ArrayList<>();

        mylist.add(1);mylist.add(2);mylist.add(3);mylist.add(4);

        ArrayList<Integer> locaList = new ArrayList<>(mylist);

        for (int i = 0; i < 2 ; i++){
            locaList.remove(i);
        }

        System.out.println(mylist.size());
        System.out.println(locaList.size());


    }

}
