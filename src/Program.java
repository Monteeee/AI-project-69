public class Program {
    public static void main(String[] args) {
        Model model = new Model();
//        model.toString();
        model.updateModel(1000);
        System.out.println(model.toString());
    }
}
